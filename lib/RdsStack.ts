/* eslint-disable no-new */
import { App, Stack } from '@serverless-stack/resources'
import * as rds from '@aws-cdk/aws-rds'
import * as ec2 from '@aws-cdk/aws-ec2'
import { SubnetType } from '@aws-cdk/aws-ec2'
import * as route53 from '@aws-cdk/aws-route53'
import { Duration, RemovalPolicy } from '@aws-cdk/core'
import { RetentionDays } from '@aws-cdk/aws-logs'
import { AppStackProps } from './AppStackProps'
import { constructId } from './index'

export default class RdsStack extends Stack {
  constructor (scope: App, id: string, props: AppStackProps) {
    super(scope, id, props)

    const subnetGroup = new rds.SubnetGroup(this, constructId('rds-subnet-group', props), {
      description: 'Subnet group for RDS',
      vpc: props.vpc!,
      vpcSubnets: {
        subnetType: SubnetType.PRIVATE_ISOLATED
      },
      removalPolicy: RemovalPolicy.DESTROY
    })

    const dbInstance = new rds.DatabaseInstance(this, constructId('postgres-instance', props), {
      vpc: props.vpc!,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PRIVATE_ISOLATED
      },
      databaseName: 'pelefele',
      securityGroups: [props.sgForIsolatedSubnet!],
      engine: rds.DatabaseInstanceEngine.postgres({ version: rds.PostgresEngineVersion.VER_13_4 }),
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.MICRO),
      credentials: rds.Credentials.fromSecret(props.allStagesSecrets!),
      removalPolicy: RemovalPolicy.DESTROY,
      deletionProtection: false,
      cloudwatchLogsRetention: RetentionDays.ONE_DAY,
      subnetGroup
    }
    )

    const privateHostedZone = new route53.PrivateHostedZone(this, constructId('private-hosted-zone', props), {
      zoneName: 'rds.com',
      vpc: props.vpc!
    })

    new route53.CnameRecord(this, 'reader.rds.com', {
      recordName: 'reader',
      domainName: dbInstance.dbInstanceEndpointAddress,
      ttl: Duration.seconds(300),
      zone: privateHostedZone
    })

    new route53.CnameRecord(this, 'writer.rds.com', {
      recordName: 'writer',
      domainName: dbInstance.dbInstanceEndpointAddress,
      ttl: Duration.seconds(300),
      zone: privateHostedZone
    })
  }
}
