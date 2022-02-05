<template>
  <section v-if="isVisible" class="column is-half">
    <b-carousel
      :autoplay="false"
      with-carousel-list
      :indicator="false"
      :overlay="gallery"
      :progress="true"
      icon-size="is-large"
      @click="switchGallery(true)"
    >
      <b-carousel-item>
        <figure class="image">
          <img :src="record.assets.filter(e=>e.isCover)[0].fileName | cdnUrl" alt="">
        </figure>
      </b-carousel-item>

      <b-carousel-item v-for="(item, i) in record.assets" :key="i">
        <figure class="image">
          <img :src="item.fileName | cdnUrl" alt="">
        </figure>
      </b-carousel-item>
      <span v-if="gallery" class="modal-close is-large" @click="switchGallery(false)" />
      <template #list="props">
        <b-carousel-list
          v-model="props.active"
          :data="previewItems"
          v-bind="al"
          as-indicator
          @switch="props.switch($event, false)"
        />
      </template>
      <template #overlay>
        <div class="has-text-centered has-text-white">
          {{ record.title }}
        </div>
      </template>
    </b-carousel>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import Component from 'vue-class-component'
import { Prop } from 'vue-property-decorator'
import cdnUrl from '~/utils/assets'
import { ListingDTO, ListingS3AssetDTO } from '~/store/generated'

@Component
export default class ListingItem extends Vue {
  @Prop({ required: true }) readonly record!: ListingDTO

  gallery: boolean = false
  al: object = {
    hasGrayscale: true,
    itemsToShow: 6,
    breakpoints: {
      768: {
        hasGrayscale: false,
        itemsToShow: 6
      },
      960: {
        hasGrayscale: true,
        itemsToShow: 6
      }
    }
  }

  get previewItems () {
    if (this.record && this.record && this.record.assets) {
      const images: Array<object> = this.record.assets.map((e: ListingS3AssetDTO) => {
        return { image: cdnUrl(e.fileName) }
      })
      return images
    }

    return []
  }

  get isVisible () {
    return this.record && this.record.id
  }

  switchGallery (isGalleryActive: boolean) {
    this.gallery = isGalleryActive
    if (isGalleryActive) {
      document.documentElement.classList.add('is-clipped')
    } else {
      document.documentElement.classList.remove('is-clipped')
    }
  }
}
</script>
