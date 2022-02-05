import { ROUTE_NAMES } from '@backend/routes'
import ApiCall, { ApiResponse } from '~/store/api/api-call'
import { PreSignedUrlDTO } from '~/store/generated'

export default class UploadService {
  apiCall: ApiCall = new ApiCall()

  async getSignedUploadUrls (count: number): Promise<ApiResponse<Array<PreSignedUrlDTO>>> {
    return await this.apiCall.$get<Array<PreSignedUrlDTO>>(ROUTE_NAMES.S3_SIGNED_URLS, { count })
  }

  /**
   * Upload form data directly to S3 object, upload data is POST-ed to pre-signed S3 URL
   */
  async uploadS3Post (signedUrl: string, file: File) {
    return await this.apiCall.dereference().put(signedUrl, file,
      { headers: { 'Content-Type': 'application/octet-stream' } })
  }
}
