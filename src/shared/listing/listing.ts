import * as zod from 'zod'
import { FormSchema } from '../form'
import { ListingDTO } from '../../../frontend/store/generated'

export const ListingSelectSchema = zod.object({
  id: zod.string(),
  value: zod.string()
})

export const ListingSchema = zod.object({
  pk: zod.string(),
  sk: zod.string(),
  title: zod.string().min(5).max(255),
  location: zod.string(),
  locationPk: zod.string(),
  locationSk: zod.string(),
  type: zod.string(),
  target: zod.string(),
  area: zod.string().optional(),
  price: zod.string().optional(),
  coverFileName: zod.string().optional(),
  listingFileNames: zod.array(zod.string()).optional()
})

export const PostListingSchema = FormSchema.extend({
  listing: zod.optional(ListingSchema)
})

export const GetListingSchema = zod.object({
  listing: zod.optional(ListingSchema)
})

export const GetListingsSchema = zod.object({
  listings: zod.array(ListingSchema)
})

export type ListingSelect = zod.TypeOf<typeof ListingSelectSchema>
export type Listing = zod.TypeOf<typeof ListingSchema>
export type GetListing = zod.TypeOf<typeof GetListingSchema>
export type GetListings = zod.TypeOf<typeof GetListingsSchema>
export type PostListing = zod.TypeOf<typeof PostListingSchema>

export const PROPERTY_TYPE_APARTMENT = { id: 'APARTMENT', value: 'Apartment' }
export const PROPERTY_TYPE_HOUSE = { id: 'HOUSE', value: 'House' }
export const LISTING_PROPERTY_TYPE: Array<ListingSelect> = [
  PROPERTY_TYPE_APARTMENT, PROPERTY_TYPE_HOUSE
]

export const PURPOSE_SALE = { id: 'SALE', value: 'Sale' }
export const PURPOSE_RENTAL = { id: 'RENTAL', value: 'Rental' }
export const LISTING_PURPOSE: Array<ListingSelect> = [
  PURPOSE_SALE, PURPOSE_RENTAL
]

export const LISTING_SORT_PRICE_ASC = { id: '1', value: 'Price from low to high' }
export const LISTING_SORT_PRICE_DESC = { id: '2', value: 'Price from high to low' }
export const LISTING_SORT_AREA_ASC = { id: '3', value: 'Area from low to high' }
export const LISTING_SORT_AREA_DESC = { id: '4', value: 'Area from high to low' }
export const LISTING_SORTS: Array<ListingSelect> = [
  LISTING_SORT_PRICE_ASC, LISTING_SORT_PRICE_DESC,
  LISTING_SORT_AREA_ASC, LISTING_SORT_AREA_DESC
]

export const sort2Es = (id: string) => {
  if (id === LISTING_SORT_PRICE_ASC.id) {
    return { price: 'asc' }
  } else if (id === LISTING_SORT_PRICE_DESC.id) {
    return { price: 'desc' }
  } else if (id === LISTING_SORT_AREA_ASC.id) {
    return { area: 'asc' }
  } else if (id === LISTING_SORT_AREA_DESC.id) {
    return { area: 'desc' }
  }

  return {}
}

export const newListing = (): ListingDTO => {
  return {
    location: {
      id: '',
      location: ''
    },
    id: 0,
    propertyType: 'APARTMENT',
    purpose: 'SALE',
    assets: [],
    price: 0,
    area: 0,
    title: ''
  }
}

export const newTestListing = (): ListingDTO => {
  return {
    location: {
      id: '1',
      location: 'Krakow'
    },
    id: 0,
    propertyType: 'APARTMENT',
    purpose: 'SALE',
    assets: [],
    price: 300,
    area: 200,
    title: 'Krakow Zwierzyniecka'
  }
}

// export const newTestListingResult = (listings: Array<Listing>, currentPage: number) : ListingsSearchResults => {
//   return {
//     totalCount: 4,
//     listings: currentPage === 1 ? listings.flatMap(i => [i, i, i]) : listings.flatMap(i => [i])
//   }
// }
