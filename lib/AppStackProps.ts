import { UserPool, UserPoolClient } from '@aws-cdk/aws-cognito'
import { StackProps } from '@serverless-stack/resources'
import * as ec2 from '@aws-cdk/aws-ec2'
import * as SSM from '@aws-cdk/aws-ssm'
import * as cognito from '@aws-cdk/aws-cognito'
import * as SM from '@aws-cdk/aws-secretsmanager'
import { ApplicationLoadBalancedFargateService } from '@aws-cdk/aws-ecs-patterns'
import BucketConfig from '../lib/BucketConfig'

export declare type AppStackProps = StackProps & {

  loadBalancerFargateService?: ApplicationLoadBalancedFargateService,

  appName: string

  allStagesSecretName: string
  allStagesSecrets?: SM.ISecret

  restApiEndpointCname: string
  restApiName: string

  account: string
  region: string
  stage: string
  stageUpperCase: string

  isDev: boolean
  isProd: boolean
  isStage: boolean
  domainStagePrefix: string

  hostedZoneName: string

  appOutputParameter?: SSM.StringParameter
  appOutputParameterName: string
  bucketConfig: BucketConfig

  vpc?: ec2.Vpc
  sgForIsolatedSubnet?: ec2.SecurityGroup
  sgForPublicSubnet?: ec2.SecurityGroup

  cognitoDomainPrefix: string
  cognitoRedirectSignInUrl: string
  cognitoRedirectSignOutUrl: string
  cognitoUserPool?: UserPool
  cognitoUserPoolClient?: UserPoolClient
  cognitoCfnIdentityPool?: cognito.CfnIdentityPool;

  cognitoAdminGroupName: string,
  cognitoRegularUserGroupName: string
  uploadBucketName?: string
}
