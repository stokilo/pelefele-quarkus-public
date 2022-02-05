/* eslint-disable @typescript-eslint/no-unused-vars */
import * as sst from '@serverless-stack/resources'
import { RetentionDays } from '@aws-cdk/aws-logs'
import BucketConfig from '../lib/BucketConfig'
import CognitoStack from '../lib/CognitoStack'
import VpcStack from './VpcStack'
import S3WebsiteStack from './S3WebsiteStack'
import { AppStackProps } from './AppStackProps'
import AllStagesSecretStack from './AllStagesSecretStack'
import AppOutputStack from './AppOutputStack'
import EcsStack from './EcsStack'
import RdsStack from './RdsStack'

export function constructName (constructNamePrefix: string, stackProps: AppStackProps) {
  const formatted = constructNamePrefix.split('-')
    .map(elem => elem.charAt(0).toUpperCase() + elem.slice(1).toLocaleLowerCase()).join('')
  const appName = stackProps.appName.charAt(0).toUpperCase() + stackProps.appName.slice(1).toLocaleLowerCase()
  const stage = stackProps.stage.charAt(0).toUpperCase() + stackProps.stage.slice(1).toLocaleLowerCase()
  return `${formatted}-${appName}-${stage}`
}

export function constructId (constructIdPrefix: string, stackProps: AppStackProps) {
  const formatted = constructIdPrefix.split('-')
    .map(elem => elem.charAt(0).toUpperCase() + elem.slice(1).toLocaleLowerCase()).join('')
  return constructName(constructIdPrefix, stackProps)
}

export default async function main (app: sst.App): Promise<void> {
  const appName = 'pelefele'

  const props: AppStackProps = {
    isDev: app.stage === 'dev',
    isStage: app.stage === 'stage',
    isProd: app.stage === 'prod',
    domainStagePrefix: `${app.stage}.`,

    cognitoAdminGroupName: 'admin-group',
    cognitoRegularUserGroupName: 'regular-users-group',
    appName,
    allStagesSecretName: `all-stages/${appName}`,
    restApiEndpointCname: app.stage === 'dev' ? 'localhost:8081' : app.stage === 'stage' ? 'stage.alb.awss.ws' : 'alb.awss.ws',
    restApiName: 'pelefele-rest-api',

    hostedZoneName: 'awss.ws',

    appOutputParameterName: `/app-output/${appName}/${app.stage}`,

    cognitoDomainPrefix: process.env.COGNITO_DOMAIN_PREFIX as string,
    cognitoRedirectSignInUrl: process.env.COGNITO_CALLBACK_URL as string,
    cognitoRedirectSignOutUrl: process.env.COGNITO_LOGOUT_URL as string,

    account: app.account,
    region: app.region,
    stage: app.stage,
    stageUpperCase: app.stage.toUpperCase(),

    bucketConfig: new BucketConfig()

  }
  await props.bucketConfig.init(props)
  app.setDefaultFunctionProps({
    memorySize: 128,
    runtime: 'nodejs14.x',
    logRetention: RetentionDays.ONE_DAY,
    environment: {
      APP_NAME: props.appName,
      REGION: props.region,
      S3_UPLOAD_BUCKET: props.bucketConfig.getS3UploadBucketName(app.stage),
      S3_IMG_BUCKET: props.bucketConfig.getS3ImgBucketName(app.stage)
    }
  })

  const allStagesSecretStack = new AllStagesSecretStack(app, 'all-stages-secret', props)
  const vpcStack = new VpcStack(app, 'vpc', props)
  const cognitoStack = new CognitoStack(app, 'cognito', props)
  if (!props.isDev) {
    const ecsStack = new EcsStack(app, 'ecs-stack', props)
    const rdsStack = new RdsStack(app, 'rds-stack', props)
  }

  const s3WebsiteStack = new S3WebsiteStack(app, 's3-website', props)
  const appOutputStack = new AppOutputStack(app, 'app-output-stack', props)
}
