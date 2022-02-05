// /* eslint-disable no-new */
// import { App, Stack } from '@serverless-stack/resources'
// import * as ec2 from '@aws-cdk/aws-ec2'
// import { SubnetType } from '@aws-cdk/aws-ec2'
// import * as ecr from '@aws-cdk/aws-ecr'
// import * as ecs from '@aws-cdk/aws-ecs'
// import { LogDriver } from '@aws-cdk/aws-ecs'
// // eslint-disable-next-line camelcase
// import iam from '@aws-cdk/aws-iam'
// import * as route53 from '@aws-cdk/aws-route53'
// import * as targets from '@aws-cdk/aws-route53-targets'
// import { RemovalPolicy } from '@aws-cdk/core'
// import * as acm from '@aws-cdk/aws-certificatemanager'
// import {
//   ApplicationLoadBalancer,
//   ApplicationProtocol,
//   ApplicationTargetGroup,
//   ListenerAction
// } from '@aws-cdk/aws-elasticloadbalancingv2'
// import { AppStackProps } from './AppStackProps'
// import { constructId } from './index'
//
// export default class EcsStackStageProd extends Stack {
//   constructor (scope: App, id: string, props: AppStackProps) {
//     super(scope, id, props)
//
//     const pelefeleStageECR = new ecr.Repository(this, constructId('pelefele-stage-ecr', props), {
//       repositoryName: 'pelefele-stage',
//       removalPolicy: RemovalPolicy.DESTROY
//     })
//     pelefeleStageECR.grantPull(new iam.ServicePrincipal('ecs.amazonaws.com'))
//
//     const pelefeleProdECR = new ecr.Repository(this, constructId('pelefele-prod-ecr', props), {
//       repositoryName: 'pelefele-prod',
//       removalPolicy: RemovalPolicy.DESTROY
//     })
//     pelefeleProdECR.grantPull(new iam.ServicePrincipal('ecs.amazonaws.com'))
//
//     const cluster = new ecs.Cluster(this, constructId('pelefele-ecs-cluster', props), {
//       vpc: props.vpc!
//     })
//
//     const hostedZoneId = props.allStagesSecrets!.secretValueFromJson(`${props.stageUpperCase}_HOSTED_ZONE_ID`).toString()
//     const awssWsHostedZone = route53.HostedZone.fromHostedZoneAttributes(this, constructId('awsws-hosted-zone', props), {
//       zoneName: props.hostedZoneName,
//       hostedZoneId
//     })
//
//     const certificate = new acm.Certificate(this, constructId('alb-awss-ws-certificate', props), {
//       domainName: 'alb.awss.ws',
//       validation: acm.CertificateValidation.fromDns(awssWsHostedZone)
//     })
//
//     const loadBalancer = new ApplicationLoadBalancer(this,
//       constructId('application-load-balancer-stage-prod', props), {
//         vpc: props.vpc!,
//         vpcSubnets: {
//           subnetType: SubnetType.PUBLIC
//         },
//         internetFacing: true
//       })
//
//     const tlsListener = loadBalancer.addListener(constructId('tls-listener', props), {
//       port: 443,
//       protocol: ApplicationProtocol.HTTPS,
//       certificates: [certificate],
//       open: true
//     })
//
//     // redirect 80 to 443
//     loadBalancer.addListener(
//       constructId('app-lb-listener-http', props), {
//         port: 80,
//         protocol: ApplicationProtocol.HTTP
//       }).addAction(constructId('listener-action', props), {
//       action: ListenerAction.redirect({
//         protocol: ApplicationProtocol.HTTPS,
//         port: '443'
//       })
//     })
//
//     // STAGE
//     props.fargateTaskDefinitionStage = new ecs.FargateTaskDefinition(this,
//       constructId('fargate-task-definition-stage', props),
//       {
//         cpu: 1024,
//         memoryLimitMiB: 4096
//       })
//
//     const stageContainer = props.fargateTaskDefinitionStage.addContainer(
//       constructId('fargate-task-definition-container-stage', props), {
//         image: ecs.ContainerImage.fromEcrRepository(pelefeleStageECR, 'latest'),
//         logging: LogDriver.awsLogs({
//           streamPrefix: 'pelefele-fargate-service-stage'
//         })
//       })
//
//     stageContainer.addPortMappings({ containerPort: 80 })
//     const stageFargateService = new ecs.FargateService(this,
//       constructId('fargate-service-stage', props), {
//         cluster,
//         taskDefinition: props.fargateTaskDefinitionStage,
//         desiredCount: 1,
//         assignPublicIp: true
//       })
//     stageFargateService.connections.allowFrom(loadBalancer, ec2.Port.tcp(80))
//     loadBalancer.connections.allowTo(stageFargateService, ec2.Port.tcp(80))
//
//     const cfnServiceStage = stageFargateService.node.tryFindChild('Service') as ecs.CfnService
//     cfnServiceStage.launchType = undefined
//     cfnServiceStage.capacityProviderStrategy = [
//       {
//         capacityProvider: 'FARGATE_SPOT',
//         weight: 4
//       },
//       {
//         capacityProvider: 'FARGATE',
//         weight: 1
//       }
//     ]
//
//     // tlsListener.addTargetGroups(constructId('target-group-stage', props), {
//     //   targetGroups: [new ApplicationTargetGroup(this,
//     //     constructId('application-target-group-stage', props), {
//     //       targets: [stageFargateService],
//     //       protocol: ApplicationProtocol.HTTP,
//     //       vpc: props.vpc
//     //     })]
//     // })
//
//     tlsListener.addTargets(constructId('target-stage', props), {
//       port: 80,
//       protocol: ApplicationProtocol.HTTP,
//       targets: [stageFargateService]
//     })
//     tlsListener.connections.allowDefaultPortFromAnyIpv4('Public access enabled')
//
//     // PROD
//     // props.fargateTaskDefinitionProd = new ecs.FargateTaskDefinition(this,
//     //   constructId('fargate-task-definition-prod', props),
//     //   {
//     //     cpu: 1024,
//     //     memoryLimitMiB: 4096
//     //   })
//     //
//     // const prodContainer = props.fargateTaskDefinitionProd.addContainer(
//     //   constructId('fargate-task-definition-container-prod', props), {
//     //     image: ecs.ContainerImage.fromEcrRepository(pelefeleProdECR, 'latest'),
//     //     logging: LogDriver.awsLogs({
//     //       streamPrefix: 'pelefele-fargate-service-prod'
//     //     })
//     //   })
//     //
//     // prodContainer.addPortMappings({ containerPort: 80 })
//     //
//     // const prodFargateService = new ecs.FargateService(this,
//     //   constructId('fargate-service-prod', props), {
//     //     cluster,
//     //     taskDefinition: props.fargateTaskDefinitionProd,
//     //     desiredCount: 1,
//     //     assignPublicIp: true
//     //   })
//     //
//     // prodFargateService.connections.allowFrom(loadBalancer, ec2.Port.tcp(80))
//     // loadBalancer.connections.allowTo(prodFargateService, ec2.Port.tcp(80))
//     //
//     // const cfnServiceProd = prodFargateService.node.tryFindChild('Service') as ecs.CfnService
//     // cfnServiceProd.launchType = undefined
//     // cfnServiceProd.capacityProviderStrategy = [
//     //   {
//     //     capacityProvider: 'FARGATE_SPOT',
//     //     weight: 4
//     //   },
//     //   {
//     //     capacityProvider: 'FARGATE',
//     //     weight: 1
//     //   }
//     // ]
//     //
//     // tlsListener.addTargetGroups(constructId('target-group-prod', props), {
//     //   targetGroups: [new ApplicationTargetGroup(this,
//     //     constructId('application-target-group-prod', props), {
//     //       targets: [prodFargateService],
//     //       protocol: ApplicationProtocol.HTTPS,
//     //       vpc: props.vpc
//     //     })]
//     // })
//
//     new route53.ARecord(this, constructId('a-record-elb', props), {
//       zone: awssWsHostedZone,
//       recordName: 'alb.awss.ws',
//       target: route53.RecordTarget.fromAlias(new targets.LoadBalancerTarget(loadBalancer))
//     })
//
//     props.allStagesSecrets?.grantRead(props.fargateTaskDefinitionStage.taskRole)
//     // props.allStagesSecrets?.grantRead(props.fargateTaskDefinitionProd.taskRole)
//     pelefeleStageECR.grantPull(props.fargateTaskDefinitionStage.taskRole)
//     // pelefeleProdECR.grantPull(props.fargateTaskDefinitionProd.taskRole)
//
//     // bastion setup
//     const bastionHost = new ec2.BastionHostLinux(this, 'BastionHostLinux', {
//       vpc: props.vpc!,
//       subnetSelection: {
//         subnetType: ec2.SubnetType.PUBLIC
//       },
//       securityGroup: props.sgForPublicSubnet,
//       instanceName: 'BastionHost',
//       instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.NANO),
//       machineImage: ec2.MachineImage.latestAmazonLinux(),
//       blockDevices: [
//         {
//           deviceName: '/dev/xvda',
//           volume: ec2.BlockDeviceVolume.ebs(8)
//         }]
//     })
//     bastionHost.instance.instance.applyRemovalPolicy(RemovalPolicy.DESTROY)
//     bastionHost.instance.instance.addPropertyOverride('KeyName', 'mumbai-ssh')
//
//     stageFargateService.connections.allowFrom(ec2.Peer.ipv4(bastionHost.instancePublicIp + '/32'), ec2.Port.tcp(80))
//     // prodFargateService.connections.allowFrom(ec2.Peer.ipv4(bastionHost.instancePublicIp + '/32'), ec2.Port.tcp(80))
//
//     //
//     // const sourceOutput = new codepipeline.Artifact()
//     // const transformedOutput = new codepipeline.Artifact()
//     // const containerName = 'web'
//     // const buildProject = new codebuild.PipelineProject(
//     //   this,
//     //   'PipelineProject',
//     //   {
//     //     buildSpec: codebuild.BuildSpec.fromObject({
//     //       version: 0.2,
//     //       phases: {
//     //         build: {
//     //           commands: [
//     //             // https://docs.aws.amazon.com/codepipeline/latest/userguide/file-reference.html#pipelines-create-image-definitions
//     //             'echo "[{\\"name\\":\\"$CONTAINER_NAME\\",\\"imageUri\\":\\"$REPOSITORY_URI\\"}]" > imagedefinitions.json'
//     //           ]
//     //         }
//     //       },
//     //       artifacts: {
//     //         files: ['imagedefinitions.json']
//     //       }
//     //     }),
//     //     environment: {
//     //       buildImage: codebuild.LinuxBuildImage.STANDARD_2_0
//     //     },
//     //     environmentVariables: {
//     //       // Container name as it exists in the task definition
//     //       CONTAINER_NAME: {
//     //         value: containerName
//     //       },
//     //       // ECR URI
//     //       REPOSITORY_URI: {
//     //         value: pelefeleStageECR.repositoryUri
//     //       }
//     //     }
//     //   }
//     // )
//     //
//     // pelefeleStageECR.grantPullPush(buildProject.grantPrincipal)
//     //
//     // new codepipeline.Pipeline(this, 'Pipeline', {
//     //   stages: [
//     //     {
//     //       stageName: 'Source',
//     //       actions: [
//     //         new codepipelineActions.EcrSourceAction({
//     //           actionName: 'Push',
//     //           repository: pelefeleStageECR,
//     //           output: sourceOutput
//     //         })
//     //       ]
//     //     },
//     //     {
//     //       stageName: 'Build',
//     //       actions: [
//     //         new codepipelineActions.CodeBuildAction({
//     //           actionName: 'Build',
//     //           input: sourceOutput,
//     //           outputs: [transformedOutput],
//     //           project: buildProject
//     //         })
//     //       ]
//     //     },
//     //     {
//     //       stageName: 'Deploy',
//     //       actions: [
//     //         new codepipelineActions.EcsDeployAction({
//     //           actionName: 'Deploy',
//     //           input: transformedOutput,
//     //           service: loadBalancedService.service
//     //         })
//     //       ]
//     //     }
//     //   ]
//     // })
//   }
// }
