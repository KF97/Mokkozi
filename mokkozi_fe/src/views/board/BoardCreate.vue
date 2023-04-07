<template>
  <v-container fluid style="height: 800px;">
    <div class="background-div">
      <div class="board-div">
        <v-card
        class="board-card"
        max-width="24rem"
        height="26rem"
        >
          <v-card-title style="display:flex; justify-content:space-between; margin-bottom:1rem">
            <div>
              <v-avatar size="36px" @click="userImageClick(loginUser)">
              <img alt="Avatar" :src="this.$store.state.user.profile">
              </v-avatar>
              <span class="font-weight-bold" style="margin-left: 0.5rem" @click="userNicknameClick(loginUser)">{{this.$store.state.user.nickname}}</span>
            </div>
            <v-icon @click="backToBoardClick">fas fa-chevron-left</v-icon>
          </v-card-title>

          <!-- 이미지 선택 -->
          <v-file-input
          multiple
          accept="image/png, image/jpeg, image/bmp"
          placeholder="최대 5장까지 올릴 수 있습니다."
          prepend-icon="mdi-camera"
          @change="createImgUrl"
          v-model="boardImages" />

          <!-- 새롭게 올릴 이미지 미리 보기 -->
          <v-carousel height="300" class="carousel" v-if="isCarousel" style="margin: 1rem 0rem">
            <v-carousel-item
            v-for="(url, i) in imagesURL"
            :key="i"
            :src="url"
            reverse-transition="fade-transition"
            transition="fade-transition"
            ></v-carousel-item>
          </v-carousel>

          <!-- 선택한 이미지나 기존에 업로드한 이미지가 없는 경우 -->
          <v-alert
          v-if="isCarousel === false"
          style="margin: 1rem 0rem;"
          height="300"
          border="right"
          color="#FF9292"
          dark>
          개성있는 본인만의 사진을 올려주세요 👀
          </v-alert>
        </v-card>

        <!-- 글 작성 -->
        <v-textarea
        class="textarea"
        filled
        v-model="content"
        placeholder="내용을 입력하세요.."
        ></v-textarea>
        <v-btn
        color="#FFB4B4"
        style="float: right;"
        @click="createBoard">
        작성
        </v-btn>
      </div>
    </div>
  </v-container>
</template>

<script>
import axios from 'axios'
import * as commonFunc from '../../common/commonFunc'

export default {
  name: 'BoardCreate',
  components: {
  },
  data: () => ({
    boardImages: [],  // 업로드한 파일.
    imagesURL: [],    // 업로드한 파일들의 local URL.
    isCarousel: false,// Carousel 표시 기준 값
    content: ''       // 게시글 내용
  }),
  computed: {
    url () {
      if (!this.uploadImage) return
      return URL.createObjectURL(this.uploadImage)
    },
    loginUser () {
      return this.$store.state.userEmail
    }
  },
  methods: {
    userImageClick (userEmail) {
      this.$router.push({ name: 'Profile', params: { userEmail: userEmail} })
    },
    userNicknameClick (userEmail) {
      this.$router.push({ name: 'Profile', params: { userEmail: userEmail} })
    },
    backToBoardClick () {
      this.$router.push({ name: 'Board' })
    },
    // 게시글 작성 요청
    createBoard () {
      const formData = new FormData()
      // 파일의 경우, 하나씩 넣어야 한다.
      for (let i = 0; i < this.boardImages.length; i++) {
        formData.append('files', this.boardImages[i])
      }

      formData.append('content', this.content)

      // console.log("전송할 파일 정보는 : ", formData.get("files"))

      axios({
        url: process.env.VUE_APP_API_URL + '/api/meet/board',
        method: 'POST',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
        data: formData
      }).then(res => {
        console.log('게시물 작성', res)
        this.$router.push({ name: 'Board' })
      }).catch(err => {
        console.log('게시물 작성 실패', err)
      })
    },
    createImgUrl() {
      // X 버튼을 누르는 경우에는, 업로드한 이미지를 초기화하는 것이므로
      if (this.boardImages.length === 0) {
        return
      }

      if (commonFunc.checkMyImagesLetter(this.boardImages)) {
        this.boardImages = []
        return
      }

      // 갯수를 제한한다. (최대 5장)
      if (this.boardImages.length > 5) {
        alert("이미지는 최대 5장까지 첨부 가능합니다.")
        this.boardImages = []
        return
      }

      // 그 외에 1개 이상의 파일을 업로드한 경우에는...
      this.imagesURL = commonFunc.makeLocalURL(this.boardImages)
      this.isCarousel = true

    },
  }
}
</script>

<style scoped>
  .background-div {
    max-width: auto;
    padding: 0.5rem 5rem 0.5rem 5rem;
    text-align: center;
  }
  .board-div {
    width: 24rem;
    height: 42rem;
    display: inline-block;
    background-color: #ffe8e8;
    padding: 2rem 2rem;
    border-radius: 1rem;
  }
  .v-card__title {
    align-items: center;
    display: flex;
    font-size: 1rem;
    font-weight: 500;
    letter-spacing: 0.0125em;
    line-height: 2rem;
    word-break: break-all;
    padding: 0;
  }
  .board-card {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-content: center;
    background-color: #ffe8e8;
    padding-bottom: 1rem;
  }
  .theme--light.v-card {
    background-color: #ffe8e8;
  }
  .v-sheet.v-card:not(.v-sheet--outlined) {
    box-shadow: none;
  }
  .theme--light.v-card > .v-card__text {
    color: black;
  }
  .v-card__text {
    text-align: start;
    font-size: 0.875rem;
    font-weight: 400;
    line-height: 1.375rem;
    letter-spacing: 0.0071rem;
    padding: 0rem;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  .v-card__actions {
    display: flex;
    align-items: center;
    padding: 0.2rem;
    justify-content: flex-end;
  }
  .like-text {
    font-weight: 500;
  }
  .v-input {
    align-items: flex-start;
    display: flex;
    flex: 0;
    font-size: 16px;
    letter-spacing: normal;
    max-width: 100%;
    max-height: 2rem;
    text-align: left;
  }
  .v-text-field {
    padding-top: 0;
    margin-top: 0;
  }
  .textarea {
    min-height: 10rem;
    overflow-y: scroll;
  }
  .textarea::-webkit-scrollbar {
    display: none;
  }
</style>
