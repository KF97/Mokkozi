<template>
  <v-container fluid style="height: 800px;">
    <div class="background-div">
      <div class="board-div">
        <v-card
        class="board-card"
        max-width="24rem"
        height="24rem"
        >
          <v-card-title style="display:flex; justify-content:space-between">
            <div>
              <v-avatar size="36px" @click="userImageClick(board.userEmail)">
              <img alt="Avatar" src="@/assets/logo.png">
              </v-avatar>
              <span class="font-weight-bold" style="margin-left: 0.5rem" @click="userNicknameClick(board.userEmail)">{{ board.nickName }}</span>
            </div>
            <v-icon @click="backToBoardClick">fas fa-chevron-left</v-icon>
          </v-card-title>

          <!-- 이미지 선택 -->
          <v-file-input
          class="my-3"

          multiple
          accept="image/png, image/jpeg, image/bmp"
          placeholder="새롭게 올릴 이미지를 선택해 주세요. ( 게시글 당 최대 첨부 이미지는 5장입니다.)"
          prepend-icon="mdi-camera"
          @change="createImgUrl"
          v-model="newImages" />

          <!-- 기존에 올린 이미지 미리 보기 -->
          <v-carousel height="300" class="carousel" v-if="isCarousel" style="margin: 1rem 0rem">
            <v-carousel-item
            v-for="(image, index) in boardImages"
            :key="index"
            :src="image.file_path"
            reverse-transition="fade-transition"
            transition="fade-transition"
            >
            <v-btn class="mt-3" @click="deleteImg(index)">삭제</v-btn>
            </v-carousel-item>

            <!-- 새롭게 올린 이미지 미리 보기 -->
            <v-carousel-item
            v-for="(url, index) in newImagesURL"
            :key="index + boardImages.length"
            :src="url"
            reverse-transition="fade-transition"
            transition="fade-transition"
            >
            </v-carousel-item>
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

        <!-- 수정할 글 내용 -->
        <v-textarea
          class="textarea"
          filled
          name="input-7-4"
          :value="content"
          v-model="content"
          placeholder="내용을 입력하세요.."
        ></v-textarea>

        <!-- 수정, 삭제 버튼 -->
        <div style="float: right;">
          <v-btn
            class="me-3"
            color="#FFB4B4"
            @click="boardUpdate(boardId)">
            수정
          </v-btn>
          <v-btn
            color="#FFB4B4"
            @click="boardDelete(boardId)">
            삭제
          </v-btn>
        </div>

      </div>
    </div>
  </v-container>
</template>

<script>
import axios from 'axios'
import * as commonFunc from '../../common/commonFunc'

export default {
  name: 'BoardUpdae',
  components: {
  },
  props: {
    boardId: {
      type: Number
    }
  },
  data: () => ({
    rules: [
      value => !value || value.size < 2000000 || 'Avatar size should be less than 2 MB!'
    ],
    boardImages: [], // 기존에 업로드한 이미지 목록
    newImages: [],   // 추가할 이미지 목록
    newImagesURL: [],// 추가할 이미지 로컬 URL
    deleteImages: [],// 삭제할 이미지 목록
    isCarousel: false,
    content: '',
    board: {}
  }),
  created () {
    this.getSelectBoard(this.boardId)
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
    // 게시물 불러오기
    getSelectBoard (boardId) {
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/board/${boardId}`,
        method: 'GET',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        }
      }).then(res => {
        console.log('게시글 수정 페이지 : 정보는', res)
        this.board = res.data
        this.content = res.data.content
        this.boardImages = res.data.galleryList


        if (res.data.galleryList.length > 0) {
          this.isCarousel = true
        }
      }).catch(err => {
        console.log('게시물 불러오기 실패', err)
      })
    },
    // 게시물 수정
    boardUpdate (boardId) { // 이미지 업데이트 부분 필요
      // 이미지도 함께 수정해야 하므로, formDate를 활용한다.
      const formData = new FormData()

      // 새로운 파일이 하나라도 있으면 아래의 과정을 거친다 (없으면 Null을 보낸다.)
      for (let i=0; i < this.newImages.length; i++) {
        formData.append("newFiles", this.newImages[i])  // 파일 형식
      }

      console.log("NewFiles  : ", formData.get("newFiles"))

      formData.append("id", this.boardId)
      formData.append("deleteFilesIndex", this.deleteImages)  // String 배열 형태
      formData.append("content", this.content)

      axios({
        url: process.env.VUE_APP_API_URL + '/api/meet/board',
        method: 'PUT',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
        data: formData
      }).then(res => {
        console.log('게시물 수정 성공', res)
        this.$router.push({ name: 'BoardDetail', params: { boardId: boardId }})
      }).catch(err => {
        console.log('게시물 수정 실패', err)
      })
    },
    // 게시물 삭제
    boardDelete (boardId) {
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/board?boardId=${boardId}`,
        method: 'DELETE',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
      }).then(res => {
        console.log('게시물 삭제 성공', res)
        this.$router.push({ name: 'Board' })
      }).catch(err => {
        console.log('게시물 삭제 실패', err)
      })
    },
    createImgUrl() {
      // X 버튼을 눌렀을 때, 기존 이미지 + 새로운 이미지가 아예 없으면 isCarousel = false
      if (this.newImages.length + this.boardImages.length === 0) {
        this.isCarousel = false
        return
      }

      // 갯수를 제한한다. (최대 5장)
      if (this.newImages.length + this.boardImages.length > 5) {
        alert("게시글 첨부 이미지는 최대 5장까지 첨부 가능합니다.")
        this.newImages = []
        return
      }

      // 특수문자 검사
      if (commonFunc.checkMyImagesLetter(this.newImages)) {
        this.newImages = []
        return
      }

      // 그 외에 1개 이상의 파일을 업로드한 경우에는...
      else {
        this.newImagesURL = commonFunc.makeLocalURL(this.newImages)
        this.isCarousel = true
      }
    },
    deleteImg(index) {
      const deleteImg = confirm("현재 이미지를 지우시겠습니까?")
      if (deleteImg) {
        // 삭제할 목록 추가
        this.deleteImages.push(this.boardImages[index].id)
        // Carousel에 더이상 표시 안하기 위해
        this.boardImages.splice(index, 1)

        // 만약 새로 업로드할 이미지와 기존에 존재하는 이미지가 더이상 존재하지 않으면
        if (this.boardImages.length + this.newImages.length === 0) {
          this.isCarousel = false
        }

      }
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
    height: 40rem;
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
