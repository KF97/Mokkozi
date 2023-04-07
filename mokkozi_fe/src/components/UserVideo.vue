<template>
  <div v-if="streamManager">
    <ov-video :stream-manager="streamManager"/>
    <div style="text-align: center;" class="font-weight-bold"><p>{{ clientData }}</p></div>
  </div>
</template>

<script>
import OvVideo from './OvVideo'
// import * as faceapi from 'face-api.js'
export default {
  name: 'UserVideo',

  components: {
    OvVideo
  },

  props: {
    streamManager: Object
  },
    created(){
//     //모델 로드
//           Promise.all([
//   faceapi.nets.ssdMobilenetv1.loadFromUri("/weights"),
//   faceapi.nets.faceLandmark68Net.loadFromUri("/weights"),
//   faceapi.nets.faceRecognitionNet.loadFromUri("/weights"),
// ]).then();
  },
  methods: {
    getConnectionData () {
      const { connection } = this.streamManager.stream
      return JSON.parse(connection.data)
    }
  },
  computed: {
    clientData () {
      const { clientData } = this.getConnectionData()
      return clientData
    },
  },
}
</script>
