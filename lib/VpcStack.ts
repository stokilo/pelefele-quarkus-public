/* eslint-disable no-new */
import { App, Stack } from '@serverless-stack/resources'
import * as ec2 from '@aws-cdk/aws-ec2'
import { AppStackProps } from './AppStackProps'
import { constructId, constructName } from './index'


export default class VpcStack extends Stack {
  constructor (scope: App, id: string, props: AppStackProps) {
    super(scope, id, props)

    props.vpc = new ec2.Vpc(this, constructId('ecs-vpc', props), {
      cidr: '10.16.0.0/16',
      natGateways: 0,
      maxAzs: 2,
      enableDnsHostnames: true,
      enableDnsSupport: true,
      subnetConfiguration: [
        {
          cidrMask: 24,
          name: constructName('subnet-public', props),
          subnetType: ec2.SubnetType.PUBLIC
        },
        {
          cidrMask: 24,
          name: constructName('subnet-isolated', props),
          subnetType: ec2.SubnetType.PRIVATE_ISOLATED
        }
      ]
    })

    props.sgForIsolatedSubnet = new ec2.SecurityGroup(this, constructId('vpc-sg-for-isolated-subnet', props), {
      vpc: props.vpc,
      securityGroupName: constructName('vpc-sg-for-isolated-subnet', props),
      allowAllOutbound: true
    })

    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.icmpPing(), 'ICMP from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.tcp(22), '22 from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.tcp(80), '80 from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.tcp(443), '443 from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.tcp(53), 'DNS TCP from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.ipv4('10.16.0.0/16'),
      ec2.Port.udp(53), 'DNS UDP from VPC')
    props.sgForIsolatedSubnet.addIngressRule(ec2.Peer.anyIpv4(),
      ec2.Port.tcp(5432), '5432 from VPC')

    props.sgForPublicSubnet = new ec2.SecurityGroup(this, constructId('vpc-sg-for-public-subnet', props), {
      vpc: props.vpc,
      securityGroupName: constructName('vpc-sg-for-public-subnet', props),
      allowAllOutbound: true
    })

    props.sgForPublicSubnet.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(22), '22 from VPC')
    // props.sgForPublicSubnet.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(5005), '5005 from VPC')
    props.sgForPublicSubnet.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(5432), '5432 from VPC')
    props.sgForPublicSubnet.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80), '80 from VPC')

    // props.vpc.addInterfaceEndpoint(constructId('ecr-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.ECR,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('ecr-docker-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.ECR_DOCKER,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('cloudwatch-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.CLOUDWATCH,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('cloudwatch-logs-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.CLOUDWATCH_LOGS,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addGatewayEndpoint(constructId('s3-endpoint', props), {
    //   service: ec2.GatewayVpcEndpointAwsService.S3,
    //   subnets: [{
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }]
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('secret-manager-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.SECRETS_MANAGER,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('ecs-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.ECS,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('ecs-agent-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.ECS_AGENT,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('ecs-agent-telemetry-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.ECS_TELEMETRY,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })


    //
    // props.vpc.addGatewayEndpoint(constructId('dynamodb-endpoint', props), {
    //   service: ec2.GatewayVpcEndpointAwsService.DYNAMODB,
    //   subnets: [{
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }]
    // })

    // props.vpc.addInterfaceEndpoint(constructId('ssm-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.SSM,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })

    // props.vpc.addInterfaceEndpoint(constructId('step-function-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.STEP_FUNCTIONS,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })
    //
    // props.vpc.addInterfaceEndpoint(constructId('sqs-interface-endpoint', props), {
    //   service: ec2.InterfaceVpcEndpointAwsService.SQS,
    //   subnets: {
    //     subnetType: SubnetType.PRIVATE_ISOLATED
    //   }
    // })

    // to access postgres and run queries
    // https://gist.github.com/kshailen/0d4f78596b0ab12659be908163ed1fc2
    // Suppose:
    // Bastion host IP is 132.5.10.11 and user is ec2-user and key_name is bastion_key.pem
    // Your postgress RDS instance fqdn is postgress.cpypigm0kth7.us-east-1.rds.amazonaws.com

    // ssh -i "bastion-key-name.pem" -NL 8886:writer.rds.com:5432 ec2-user@132.5.10.11 -v
    // const bastionHost = new ec2.BastionHostLinux(this, 'BastionHostLinux', {
    //   vpc: props.vpc,
    //   subnetSelection: {
    //     subnetType: ec2.SubnetType.PRIVATE_ISOLATED
    //   },
    //   securityGroup: props.sgForIsolatedSubnet,
    //   instanceName: 'BastionHost',
    //   instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.NANO),
    //   machineImage: ec2.MachineImage.latestAmazonLinux()
    // })
    // // bastionHost.allowSshAccessFrom(ec2.Peer.ipv4('94.203.155.114/32'))
    // bastionHost.instance.instance.addPropertyOverride('KeyName', 'bastion-key-name')
  }
}
