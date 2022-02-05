import Amplify from '@aws-amplify/core'
import Auth from '@aws-amplify/auth'
import awsConfig from './../aws-config.json'
import '@aws-amplify/ui-vue'

console.info('api ' + awsConfig.restApiEndpointCname);
console.info(awsConfig.restApiEndpointCname.indexOf('localhost'))
Amplify.configure({
  Auth: {
    region: awsConfig.region,
    userPoolId: awsConfig.cognitoUserPoolId,
    userPoolWebClientId: awsConfig.cognitoUserPoolClientId,
    identityPoolId: awsConfig.cognitoIdentityPoolId
  },

  API: {
    endpoints: [
      {
        name: awsConfig.restApiName,
        endpoint: awsConfig.restApiEndpointCname.includes('localhost')
          ? `http://${awsConfig.restApiEndpointCname}/`
          : `https://${awsConfig.restApiEndpointCname}/`,
        region: awsConfig.region,
        custom_header: async () => {
          return {
            Authorization: `Bearer ${(await Auth.currentSession()).getIdToken().getJwtToken()}`
          }
        }
      }
    ]
  },
  oauth: {
    domain: `${awsConfig.cognitoDomainPrefix}.auth.${awsConfig.region}.amazoncognito.com`,
    scope: ['phone', 'email', 'profile', 'openid', 'aws.cognito.signin.user.admin'],
    redirectSignIn: awsConfig.cognitoRedirectSignInUrl,
    redirectSignOut: awsConfig.cognitoRedirectSignOutUrl,
    responseType: 'code'
  }
})
