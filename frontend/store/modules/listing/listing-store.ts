import { createModule, mutation, action, getter } from 'vuex-class-component'
import {
  LISTING_PURPOSE, LISTING_PROPERTY_TYPE, ListingSelect,
  newListing,
  newTestListing
} from '@backend/listing/listing'
import { ROUTE_NAMES } from '@backend/routes'
import ApiCall, { ApiResponse } from '~/store/api/api-call'
import UploadService from '~/store/api/app/upload-service'
import { ListingDTO, LocationDTO, ListingSearchVM } from '~/store/generated'

export const VuexModule = createModule({
  namespaced: 'listing',
  strict: false,
  target: 'nuxt'
})

export class ListingStore extends VuexModule {
  apiCall: ApiCall = new ApiCall()
  uploadService: UploadService = new UploadService()

  listing: ListingDTO = process.env.isDevMode ? newTestListing() : newListing()
  coverFile: File = new File([], '')
  listingFiles: File[] = []

  listings: Array<ListingDTO> = []
  tableColumns: Array<any> = [
    {
      field: 'title',
      label: 'Title'
    },
    {
      field: 'price',
      label: 'Price'
    }
  ]

  coverPreviewUrl: string = ''
  listingPreviewUrls: Array<string> = []

  listingPropertyTypes: Array<ListingSelect> = LISTING_PROPERTY_TYPE
  listingPurposes: Array<ListingSelect> = LISTING_PURPOSE

  @getter
  areFieldsFilled (): boolean {
    return true
  }

  @mutation deleteDropFile (index: number) {
    this.listingFiles.splice(index, 1)
    this.listingPreviewUrls.splice(index, 1)
  }

  @mutation mutateListing (mutatedListing: ListingDTO) {
    this.listing = mutatedListing
  }

  @mutation mutateClearAssets () {
    this.listing.assets = []
  }

  @mutation mutateCoverFileName (coverFileName: string) {
    this.listing.assets.push({
      fileName: coverFileName,
      isCover: true
    })
  }

  @mutation mutateListingFileNames (listingFileNames: Array<string>) {
    listingFileNames.forEach((elem) => {
      this.listing.assets.push({
        fileName: elem,
        isCover: false
      })
    })
  }

  @mutation mutateListings (listings: ListingDTO[]) {
    this.listings = listings
  }

  @mutation
  onFileUpdateCover (file: File) {
    this.coverPreviewUrl = URL.createObjectURL(file)
  }

  @mutation
  onFileUpdateListing (files: File[]) {
    this.listingPreviewUrls = []
    for (let i = 0; i < files.length; i++) {
      const f = files[i]
      const url = URL.createObjectURL(f)
      this.listingPreviewUrls.push(url)
    }
  }

  @mutation
  onLocationSelect (selectedLocation: LocationDTO) {
    this.listing.location = selectedLocation
  }

  @action
  async loadListings () {
    const response = await this.apiCall.$get<ListingSearchVM>(
      ROUTE_NAMES.LISTINGS, {
        pageSize: 10,
        pageNumber: 0
      })

    if (response.success) {
      this.mutateListings(response.data!.listings)
    }
  }

  @action
  async onSaveListing (): Promise<ApiResponse<ListingDTO>> {
    let count = this.coverFile.name.length ? 1 : 0
    count = this.listingFiles ? count + this.listingFiles.length : count

    if (count > 0) {
      const response = await this.uploadService.getSignedUploadUrls(count)
      if (response.success) {
        await this.mutateClearAssets()
        const signedUrls = response.data!
        await this.uploadService.uploadS3Post(signedUrls[0].url, this.coverFile)
        await this.mutateCoverFileName(signedUrls[0].fileName)

        if (signedUrls.length > 1) {
          const remaining = signedUrls.splice(1)
          for (let i = 0; i < remaining.length; i++) {
            await this.uploadService.uploadS3Post(remaining[i].url, this.listingFiles[i])
          }
          const remainingFileNames = remaining.map(r => r.fileName)
          await this.mutateListingFileNames(remainingFileNames)
        }
      }
    }

    return await this.apiCall.$post<ListingDTO>(ROUTE_NAMES.LISTINGS, this.listing)
  }
}
