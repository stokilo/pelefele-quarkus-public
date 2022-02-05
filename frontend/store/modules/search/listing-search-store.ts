import { createModule, action, mutation } from 'vuex-class-component'
import { ROUTE_NAMES } from '@backend/routes'
import {
  LISTING_SORTS,
  LISTING_PURPOSE,
  LISTING_PROPERTY_TYPE,
  ListingSelect
} from '@backend/listing/listing'
import ApiCall from '~/store/api/api-call'
import { ListingDTO, ListingPropertyType, ListingSearchParamsVM, ListingSearchVM, LocationDTO } from '~/store/generated'

export const VuexModule = createModule({
  namespaced: 'listingSearch',
  strict: false,
  target: 'nuxt'
})

export class ListingSearchStore extends VuexModule {
  apiCall: ApiCall = new ApiCall()

  listingPropertyTypes: Array<ListingSelect> = LISTING_PROPERTY_TYPE
  listingPurposes: Array<ListingSelect> = LISTING_PURPOSE
  listingSorts: Array<ListingSelect> = LISTING_SORTS

  selectedLocation: LocationDTO = {
    id: '',
    location: ''
  }

  listingSearchParamsVM: ListingSearchParamsVM = {
    locationId: '',
    propertyType: 'APARTMENT',
    purpose: 'RENTAL',
    minArea: '',
    maxArea: '',
    minPrice: '',
    maxPrice: '',
    sort: '',
    pageSize: '10',
    pageNumber: '0'
  }

  listingsFound: Array<ListingDTO> = []
  pagingTotalCount: number = 0
  pagingCurrentPage: number = 1

  get isSearchDisabled () {
    return !this.listingSearchParamsVM.locationId.length
  }

  /**
   * Converts [1, 2, 3, 4, 5, 6, 7 ] to [[1, 2, 3], [4, 5, 6], [7]] but instead of numbers we have Listing instances
   */
  get listingsGroupedBy3 (): Array<Array<ListingDTO>> {
    return this.listingsFound.reduce<Array<Array<ListingDTO>>>(
      function (previousValue: Array<Array<ListingDTO>>, currentValue: ListingDTO, currentIndex: number, array: Array<ListingDTO>) {
        if (currentValue && currentIndex % 3 === 0) {
          previousValue.push(array.slice(currentIndex, currentIndex + 3))
        }
        return previousValue
      }, [])
  }

  @mutation resetPaging () {
    this.pagingTotalCount = 0
    this.pagingCurrentPage = 1
    this.listingSearchParamsVM.pageNumber = '0'
    this.listingSearchParamsVM.pageSize = '10'
  }

  /**
   * Es limitation for deep paging force us to round up max result count to 10000
   */
  @mutation mutateListingData (mutatedData: ListingSearchVM) {
    this.listingsFound = mutatedData.listings ? mutatedData.listings as Array<ListingDTO> : []
    this.pagingTotalCount = mutatedData.total
  }

  @mutation
  resetLocationSearch () {
    this.listingSearchParamsVM.locationId = ''
  }

  @mutation
  onLocationSelect (selectedLocation: LocationDTO) {
    if (selectedLocation) {
      this.listingSearchParamsVM.locationId = selectedLocation.id
    }
  }

  @action
  async onSearch () {
    console.info('this.listingSearchParamsVM.pageNumber ' + this.listingSearchParamsVM.pageNumber)
    console.info('this.pagingCurrentPage  ' + this.pagingCurrentPage)
    this.listingSearchParamsVM.pageNumber = (this.pagingCurrentPage - 1).toString()
    console.info('this.listingSearchParamsVM.pageNumber ' + this.listingSearchParamsVM.pageNumber)
    const response = await this.apiCall.$get<ListingSearchVM>(ROUTE_NAMES.LISTING_SEARCH, this.listingSearchParamsVM)
    if (response.success) {
      await this.mutateListingData(response.data!)
    }
  }
}
