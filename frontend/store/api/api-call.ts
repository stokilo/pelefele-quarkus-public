import { ZodType } from 'zod/lib/types'
import { API } from '@aws-amplify/api'
import awsConfig from './../../aws-config.json'
import { $axios, $i18n, $log } from '~/utils/api'
import { ErrorsVM } from '~/store/generated'

interface ApiCallConfig {
  headers: Record<string, string>
  timeout: number
}

export interface ApiResponse<T> {
  data?: T,
  errorsVM?: ErrorsVM,
  success: boolean
}

/**
 * Generic base service to make API calls. It provides high level function to invoke WS and process result.
 */
export default class ApiCall {
  POST_CONFIG: ApiCallConfig = {
    headers: { 'Content-Type': 'application/json' },
    timeout: 15_000
  }

  getRequestConfig (): Object {
    const config = { ...this.POST_CONFIG }
    config.headers['X-Language'] = $i18n.locale
    return config
  }

  async $get<T> (routePath: string, params: object = {}): Promise<ApiResponse<T>> {
    const response: ApiResponse<T> = {
      success: false
    }
    try {
      response.data = await API.get(awsConfig.restApiName, routePath,
        { ...this.getRequestConfig(), queryStringParameters: params })
      response.success = true
    } catch (err) {
      $log.error(err)
      response.errorsVM = this.extractErrorsVM(err)
      response.success = false
    }
    return response
  }

  async $post<T> (routePath: string, model: T): Promise<ApiResponse<T>> {
    const response: ApiResponse<T> = {
      success: false
    }
    try {
      response.data = await API.post(awsConfig.restApiName, routePath,
        {
          ...this.getRequestConfig(),
          body: model
        })
      response.success = true
    } catch (err) {
      $log.error(err)
      response.errorsVM = this.extractErrorsVM(err)
      response.success = false
    }
    return response
  }

  extractErrorsVM (err: unknown): any {
    // @ts-ignore
    if (err.response) {
      // @ts-ignore
      const response = err.response
      if (response.status && response.status === 400 && response.data) {
        const status = response.status
        if (status === 400 && response.data) {
          return this.renameKeys(response.data.errors)
        }
      }
    }
    return undefined
  }

  renameKeys (obj: ErrorsVM) {
    const keyValues = Object.keys(obj).map((key) => {
      console.info(key)
      const newKey = key.replace('create.pListingDTO.', '')
      console.info(newKey)
      // @ts-ignore
      return { [newKey]: obj[key] }
    })
    return Object.assign({}, ...keyValues)
  }

  dereference () {
    return $axios
  }
}
