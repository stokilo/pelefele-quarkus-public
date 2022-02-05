import {
  AdminAddUserToGroupCommand,
  CognitoIdentityProviderClient
} from '@aws-sdk/client-cognito-identity-provider'
import {
  PostConfirmationTriggerEvent,
  PostConfirmationTriggerHandler
} from 'aws-lambda/trigger/cognito-user-pool-trigger/post-confirmation'
import { logger } from '../common/logger'

let client: CognitoIdentityProviderClient | undefined
try {
  client = new CognitoIdentityProviderClient({ region: process.env.region })
} catch (e) {
  logger.error(e)
}

export const handler: PostConfirmationTriggerHandler = async (
  event: PostConfirmationTriggerEvent
) => {
  logger.info(`Running post confirmation for user: ${event.userName}`)
  await client!.send(new AdminAddUserToGroupCommand({
    UserPoolId: event.userPoolId,
    Username: event.userName,
    GroupName: 'regular-users-group'
  }))

  return await event
}
