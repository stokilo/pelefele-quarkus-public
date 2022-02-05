/* eslint-disable no-new */
import { App, Stack } from '@serverless-stack/resources'
import * as ec2 from '@aws-cdk/aws-ec2'
import { SubnetType } from '@aws-cdk/aws-ec2'
import * as ecr from '@aws-cdk/aws-ecr'
import * as ecs from '@aws-cdk/aws-ecs'
import { LogDriver } from '@aws-cdk/aws-ecs'
// eslint-disable-next-line camelcase
import * as ecs_patterns from '@aws-cdk/aws-ecs-patterns'
import iam from '@aws-cdk/aws-iam'
import * as route53 from '@aws-cdk/aws-route53'
import { RemovalPolicy } from '@aws-cdk/core'
import * as acm from '@aws-cdk/aws-certificatemanager'
import { ApplicationProtocol, ListenerAction } from '@aws-cdk/aws-elasticloadbalancingv2'
import { AppStackProps } from './AppStackProps'
import { constructId } from './index'

export default class EcsStack extends Stack {
  constructor (scope: App, id: string, props: AppStackProps) {
    super(scope, id, props)

    const ecrRepo = new ecr.Repository(this, constructId('pelefele-ecr', props), {
      repositoryName: 'pelefele-' + props.stage,
      removalPolicy: RemovalPolicy.DESTROY
    })
    ecrRepo.grantPull(new iam.ServicePrincipal('ecs.amazonaws.com'))

    const cluster = new ecs.Cluster(this, 'QuarkusCluster', {
      vpc: props.vpc!
    })

    const hostedZoneId = props.allStagesSecrets!.secretValueFromJson(`${props.stageUpperCase}_HOSTED_ZONE_ID`).toString()
    const awssWsHostedZone = route53.HostedZone.fromHostedZoneAttributes(this, constructId('awsws-hosted-zone', props), {
      zoneName: props.hostedZoneName,
      hostedZoneId
    })

    // Create a load-balanced Fargate service and make it public
    props.loadBalancerFargateService = new ecs_patterns.ApplicationLoadBalancedFargateService(this, 'QuarkusFargateService', {
      cluster,
      cpu: 256,
      memoryLimitMiB: 512,
      desiredCount: 1,
      taskImageOptions: {
        image: ecs.ContainerImage.fromEcrRepository(ecrRepo, 'latest'),
        environment: {
          // enable this and container will attempt to run in dev mode, this requires a mutable-jar packaging
          // QUARKUS_LAUNCH_DEVMODE: 'true'
        },
        logDriver: LogDriver.awsLogs({
          streamPrefix: 'quarkus'
        })
      },
      publicLoadBalancer: true,
      taskSubnets: {
        // todo: public is unsafe, but isolated requires many vpc endpoints, solve this
        // subnetType: SubnetType.PRIVATE_ISOLATED
        subnetType: SubnetType.PUBLIC
      },
      domainName: props.restApiEndpointCname,
      certificate: new acm.Certificate(this, constructId('alb-awss-ws-certificate', props), {
        domainName: props.restApiEndpointCname,
        validation: acm.CertificateValidation.fromDns(awssWsHostedZone)
      }),
      // todo: public is unsafe, but isolated requires many vpc endpoints, solve this
      assignPublicIp: true,
      domainZone: awssWsHostedZone
    })

    // this is launching containers using Fargate spot instances
    // to save some money use this until app really needs uninterrupted workflow
    const cfnService = props.loadBalancerFargateService.service.node.tryFindChild('Service') as ecs.CfnService
    cfnService.launchType = undefined
    cfnService.capacityProviderStrategy = [{
      capacityProvider: 'FARGATE_SPOT',
      weight: 4
    }, {
      capacityProvider: 'FARGATE',
      weight: 1
    }
    ]
    // 80 listener redirects to 443
    const httpListener = props.loadBalancerFargateService.loadBalancer.addListener('app-lb-listener-http', {
      port: 80,
      protocol: ApplicationProtocol.HTTP
    })
    httpListener.addAction('listener-action', {
      action: ListenerAction.redirect({
        protocol: ApplicationProtocol.HTTPS,
        port: '443'
      })
    })

    // permissions
    props.allStagesSecrets?.grantRead(props.loadBalancerFargateService.taskDefinition.taskRole)

    // bastion host for RDS access
    const bastionHost = new ec2.BastionHostLinux(this, 'BastionHostLinux', {
      vpc: props.vpc!,
      subnetSelection: {
        subnetType: ec2.SubnetType.PUBLIC
      },
      securityGroup: props.sgForPublicSubnet,
      instanceName: 'BastionHost',
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.NANO),
      machineImage: ec2.MachineImage.latestAmazonLinux(),
      blockDevices: [
        {
          deviceName: '/dev/xvda',
          volume: ec2.BlockDeviceVolume.ebs(8)
        }]
    })
    bastionHost.instance.instance.applyRemovalPolicy(RemovalPolicy.DESTROY)
    bastionHost.instance.instance.addPropertyOverride('KeyName', 'mumbai-ssh')

    props.loadBalancerFargateService.service.connections.allowFrom(ec2.Peer.ipv4(bastionHost.instancePublicIp + '/32'), ec2.Port.tcp(80))

    // disabled, not needed for simple setup, manual restart is enough for now, anyway code below should work with small
    // adjustments
    //
    // const sourceOutput = new codepipeline.Artifact()
    // const transformedOutput = new codepipeline.Artifact()
    // const containerName = 'web'
    // const buildProject = new codebuild.PipelineProject(
    //   this,
    //   'PipelineProject',
    //   {
    //     buildSpec: codebuild.BuildSpec.fromObject({
    //       version: 0.2,
    //       phases: {
    //         build: {
    //           commands: [
    //             // https://docs.aws.amazon.com/codepipeline/latest/userguide/file-reference.html#pipelines-create-image-definitions
    //             'echo "[{\\"name\\":\\"$CONTAINER_NAME\\",\\"imageUri\\":\\"$REPOSITORY_URI\\"}]" > imagedefinitions.json'
    //           ]
    //         }
    //       },
    //       artifacts: {
    //         files: ['imagedefinitions.json']
    //       }
    //     }),
    //     environment: {
    //       buildImage: codebuild.LinuxBuildImage.STANDARD_2_0
    //     },
    //     environmentVariables: {
    //       // Container name as it exists in the task definition
    //       CONTAINER_NAME: {
    //         value: containerName
    //       },
    //       // ECR URI
    //       REPOSITORY_URI: {
    //         value: quarkusRemoteRepository.repositoryUri
    //       }
    //     }
    //   }
    // )
    //
    // quarkusRemoteRepository.grantPullPush(buildProject.grantPrincipal)
    //
    // new codepipeline.Pipeline(this, 'Pipeline', {
    //   stages: [
    //     {
    //       stageName: 'Source',
    //       actions: [
    //         new codepipelineActions.EcrSourceAction({
    //           actionName: 'Push',
    //           repository: quarkusRemoteRepository,
    //           output: sourceOutput
    //         })
    //       ]
    //     },
    //     {
    //       stageName: 'Build',
    //       actions: [
    //         new codepipelineActions.CodeBuildAction({
    //           actionName: 'Build',
    //           input: sourceOutput,
    //           outputs: [transformedOutput],
    //           project: buildProject
    //         })
    //       ]
    //     },
    //     {
    //       stageName: 'Deploy',
    //       actions: [
    //         new codepipelineActions.EcsDeployAction({
    //           actionName: 'Deploy',
    //           input: transformedOutput,
    //           service: loadBalancedService.service
    //         })
    //       ]
    //     }
    //   ]
    // })
  }
}
