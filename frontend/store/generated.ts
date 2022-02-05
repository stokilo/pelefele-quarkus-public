/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.34.976 on 2022-02-04 18:55:48.

export interface ListingDTO {
    id: number;
    title: string;
    price: number;
    propertyType: ListingPropertyType;
    purpose: ListingPurpose;
    area: number;
    location: LocationDTO;
    assets: ListingS3AssetDTO[];
}

export interface ListingS3AssetDTO {
    fileName: string;
    isCover: boolean;
}

export interface LocationDTO {
    id: string;
    location: string;
}

export interface PreSignedUrlDTO {
    url: string;
    fileName: string;
}

export interface ErrorsVM {
    errors: { [index: string]: string };
}

export interface ListingSearchParamsVM {
    locationId: string;
    propertyType: string;
    purpose: string;
    minPrice: string;
    maxPrice: string;
    minArea: string;
    maxArea: string;
    sort: string;
    pageNumber: string;
    pageSize: string;
}

export interface ListingSearchVM {
    listings: ListingDTO[];
    total: number;
    numberOfPages: number;
}

export interface LocationSearchVM {
    locations: LocationDTO[];
}

export type ListingPropertyType = "APARTMENT" | "HOUSE";

export type ListingPurpose = "SALE" | "RENTAL";
