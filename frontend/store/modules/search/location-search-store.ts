import { createModule, action, mutation } from 'vuex-class-component'
import { ROUTE_NAMES } from '@backend/routes'
import ApiCall from '~/store/api/api-call'
import { LocationDTO, LocationSearchVM } from '~/store/generated'

export const VuexModule = createModule({
  namespaced: 'locationSearch',
  strict: false,
  target: 'nuxt'
})

export class LocationSearchStore extends VuexModule {
  apiCall: ApiCall = new ApiCall()
  locationData: Array<LocationDTO> = []
  isFetching: boolean = false

  @mutation mutateLocationData (mutatedData: Array<LocationDTO>) {
    this.locationData = mutatedData
  }

  @action
  async onSearch (term: string) {
    if (process.env.isMockMode) {
      return this.mutateLocationData([
        { id: '1', location: 'Krakow' }
      ])
    }
    const response = await this.apiCall.$get<LocationSearchVM>(ROUTE_NAMES.LOCATION_SEARCH, { term })

    if (response.success) {
      await this.mutateLocationData(response.data!.locations)
    }
  }
}
