/* eslint-disable @typescript-eslint/no-unused-vars */
import * as sst from '@serverless-stack/resources'
import * as SSM from '@aws-cdk/aws-ssm'
import { AppStackProps } from './AppStackProps'
import { constructId } from './index'

export default class AppOutputStack extends sst.Stack {
  constructor (scope: sst.App, id: string, props: AppStackProps) {
    super(scope, id, props)

    const ssmValue = {
      appName: props.appName,
      region: props.region,
      restApiEndpointCname: props.restApiEndpointCname,
      restApiName: props.restApiName,
      cognitoUserPoolId: props.cognitoUserPool!.userPoolId,
      cognitoUserPoolClientId: props.cognitoUserPoolClient!.userPoolClientId,
      cognitoDomainPrefix: props.cognitoDomainPrefix,
      cognitoRedirectSignInUrl: props.cognitoRedirectSignInUrl,
      cognitoRedirectSignOutUrl: props.cognitoRedirectSignOutUrl,
      cognitoIdentityPoolId: props.cognitoCfnIdentityPool!.ref,
      cognitoAdminGroupName: props.cognitoAdminGroupName,
      cognitoRegularUserGroupName: props.cognitoRegularUserGroupName,
      uploadBucketName: props.uploadBucketName
    }

    props.appOutputParameter = new SSM.StringParameter(this, constructId('app-output-parameter', props), {
      parameterName: props.appOutputParameterName,
      description: `Parameter store for application: ${props.appName} and stage: ${props.stage}`,
      stringValue: JSON.stringify(ssmValue)
    })

    if (!props.isDev) {
      props.appOutputParameter.grantRead(props.loadBalancerFargateService!.taskDefinition.taskRole)
    }
  }
}
