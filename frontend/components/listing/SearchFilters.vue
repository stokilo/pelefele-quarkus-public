<template>
  <div class="box search-box columns is-multiline is-variable is-1-mobile is-1-tablet is-1-desktop is-1-widescreen is-1-fullhd">
    <div v-for="(propertyType, index) in listingPropertyTypes" :key="index" class="column is-one-quarter">
      <b-radio-button
        v-model="listingSearchParamsVM.propertyType"
        :native-value="propertyType.id"
        type="is-primary is-light is-outlined"
        class="filter-radio-button-label"
      >
        <b-icon icon="check" />
        <span>{{ propertyType.value }}</span>
      </b-radio-button>
    </div>

    <div v-for="(purpose, index) in listingPurposes" :key="'target' + index" class="column is-one-quarter">
      <b-radio-button
        v-model="listingSearchParamsVM.purpose"
        :native-value="purpose.id"
        type="is-primary is-light is-outlined"
        class="filter-radio-button-label"
      >
        {{ purpose.value }}
      </b-radio-button>
    </div>

    <b-field class="column is-one-quarter">
      <b-input
        v-model="listingSearchParamsVM.minPrice"
        placeholder="Price from"
        type="number"
      />
    </b-field>

    <b-field class="column is-one-quarter">
      <b-input
        v-model="listingSearchParamsVM.maxPrice"
        placeholder="Price to"
        type="number"
      />
    </b-field>

    <b-field class="column is-one-quarter">
      <b-input
        v-model="listingSearchParamsVM.minArea"
        placeholder="Area from"
        type="number"
      />
    </b-field>

    <b-field class="column is-one-quarter">
      <b-input
        v-model="listingSearchParamsVM.maxArea"
        placeholder="Area to"
        type="number"
      />
    </b-field>

  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import Component from 'vue-class-component'
import { Prop } from 'vue-property-decorator'
import { ListingSelect } from '@backend/listing/listing'
import { ListingSearchParamsVM } from '~/store/generated'

@Component
export default class SearchFilters extends Vue {
  @Prop({ required: true }) readonly listingSearchParamsVM!: ListingSearchParamsVM
  @Prop({ required: true }) readonly listingPropertyTypes!: Array<ListingSelect>
  @Prop({ required: true }) readonly listingPurposes!: Array<ListingSelect>
}
</script>
