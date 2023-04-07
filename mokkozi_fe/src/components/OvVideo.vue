<template>
  <video id="user_video" autoplay :width="videoWidth" :height="videoHeight"/>
</template>

<script>
import { Subscriber } from "openvidu-browser"


export default ({
  name: 'OvVideo',
  data: () => ({
    videoWidth: '100%',
    videoHeight: '100%',
  }),
  props: {
    streamManager: Object
  },
 watch: {
    streamManager () {
      // this.streamManager.addVideoElement(document.querySelector("#user_video"))
      this.streamManager.addVideoElement(this.$el)
    }
  },
  mounted () {
    console.log('OvVideo에서 보는 스트림매니저', this.streamManager)
    console.log('테스트 값', this.streamManager.constructor.name)
    // this.videoWidthSelect()
    this.streamManager.addVideoElement(this.$el)
  },
  methods: {
    // videoWidthSelect () {
    //   console.log('트루/폴스', this.streamManager.constructor.name === 'Subscriber')
    //   if (this.streamManager.constructor.name === 'Subscriber') {
    //     this.videoWidth = '30%'
    //     this.videoHeight = '30%'
    //   }
    // },
  },
})
</script>
