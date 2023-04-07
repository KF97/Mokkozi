<template>
  <v-container fluid style="height: 800px;">
    <br>
    <v-icon style="position: fixed; color: #FFB4B4;" @click="backToBoardClick">fas fa-chevron-left</v-icon>
    <div class="background-div">
      <div class="board-div">
        <v-card class="board-card" max-width="24rem" height="42rem">
          <v-card-title
            style="
              display: flex;
              justify-content: space-between;
              margin-bottom: 0.2rem;
            "
          >
            <div>
              <v-avatar size="36px" @click="userImageClick(board.userEmail)">
                <img alt="Avatar" :src="board.profileUrl" />
              </v-avatar>
              <span
                class="font-weight-bold"
                style="margin-left: 0.5rem"
                @click="userNicknameClick(board.userEmail)"
                >{{ board.nickName }}</span
              >
            </div>
            <v-menu offset-y>
              <template v-slot:activator="{ on, attrs }">
                <v-icon color="black" dark v-bind="attrs" v-on="on"
                  >fas fa-ellipsis-h</v-icon
                >
              </template>
              <v-list>
                <v-list-item v-if="board.userEmail === loginUser">
                  <v-list-item-title>
                    <v-btn
                      style="cursor: pointer;"
                      color="#FFB4B4"
                      dark
                      @click="boardUpdateClick(board.id)">수정하기</v-btn>
                  </v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <report-board :boardId="board.id" />
                </v-list-item>
              </v-list>
            </v-menu>
          </v-card-title>
          <!-- <i class="fas fa-ellipsis-h"></i> -->
          <template slot="progress">
            <v-progress-linear
              color="deep-purple"
              height="10"
              indeterminate
            ></v-progress-linear>
          </template>

          <!-- Carousel -->
          <v-carousel height="300" class="carousel" v-if="board.galleryList.length > 1" style="margin: 1rem 0rem">
            <v-carousel-item
            v-for="(img) in board.galleryList"
            :key="img.id"
            :src="img.file_path"
            reverse-transition="fade-transition"
            transition="fade-transition"
            ></v-carousel-item>
          </v-carousel>

          <!-- 이미지가 1장인 경우에는 -->
          <v-img
          v-else-if="board.galleryList.length === 1"
          width="320px"
          height="300px"
          max-height="300px"
          position="center"
          :src="board.galleryList[0].file_path"
          style="margin-bottom: 0.2rem"
          >
          </v-img>
          <div>
            <v-card-text class="like-text">
              <i
                v-if="board.boardLike"
                class="fas fa-heart"
                style="color: red"
                @click="boardUnLike(board.id)"
              ></i>
              <i
                v-else
                class="far fa-heart"
                style="color: red"
                @click="boardLike(board.id)"
              ></i>
              {{ board.likeCount }}명이 이 글을 좋아합니다.
            </v-card-text>

            <v-card-text style="font-size:15px; margin: 4px 0px">
              {{ board.content }}
            </v-card-text>
          </div>
          <!-- 작성한 댓글이 존재한다면 -->
          <div v-for="(comment) in board.commentList" :key="comment.id"  style="display: flex; flex-direction: row;">
            <div style="width: 3rem;">
              <v-list-item-avatar @click="userImageClick(comment.email)">
                <img :src="comment.file_path">
              </v-list-item-avatar>
            </div>
            <div style="width: 17rem;">
              <v-list-item-content>
                <span class="font-weight-bold" style="text-align: start; font-size: 14px">{{ comment.nickName }}</span>
                <span class="comment-text">{{ comment.content }}</span>
              </v-list-item-content>
            </div>
          </div>

          <div v-if="board.commentList.length === 0" style="height: 15px; font-size: 12px">
            <p>아직 작성된 댓글이 없습니다 :(</p>
          </div>
        </v-card>
        <!-- 댓글 작성란 -->
        <div style="display:flex; justify-content: space-between;">
          <input
            v-model="commentContent"
            style="
            height: 24px;
            font-size: 0.875rem;
            border: none;
            width: 16rem;
            outline-style: none;
            "
            type="text"
            placeholder="댓글 달기"
            @keydown.enter="createComment(board.id)"
          />
          <v-btn
            color="#FFB4B4"
            style="padding: 0px 5px;"
            min-width="40px"
            height="24px"
            @click="createComment(board.id)"
          >
            작성
          </v-btn>
        </div>
      </div>
    </div>
  </v-container>
</template>

<script>
import axios from 'axios'
import ReportBoard from '../../components/ReportBoard'

export default ({
  name: 'BoardDetail',
  components: {
    ReportBoard
  },
  props: {
    boardId: {
      type: Number
    }
  },
  data: () => ({
    commentContent: '',
    commentList: "",
    board: [],
  }),
  mounted () {
    this.getSelectBoard(this.boardId)
  },
  computed: {
    loginUser () {
      return this.$store.state.user.email
    }
  },
  methods: {
    boardUpdateClick (boardId) {
      this.$router.push({ name: 'BoardUpdate', params: { boardId: boardId }})
    },
    boardReportClick () {
      this.$router.push({ name: 'Home' }) // 신고하는 페이지로 이동하도록 바꿔야함
    },
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
        methods: 'GET',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        }
      }).then(res => {
        console.log('게시물 불러오기 성공', res.data)
        this.board = res.data
        if (this.board.galleryList.length === 0) {
          return this.board.galleryList.push({'file_path' : 'https://mokkozi-s3.s3.ap-northeast-2.amazonaws.com/board.jpg'})
        }
      }).catch(err => {
        console.log('게시물 불러오기 실패', err)
      })
    },
    // 댓글 작성
    createComment(boardId) {
      if (this.commentContent.trim() !== "") {
        console.log("댓글 정보 - 게시글 아이디 : ", boardId, ", 댓글 내용 : ", this.commentContent)
      axios({
        url: process.env.VUE_APP_API_URL + '/api/meet/comment',
        method: 'POST',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
        data: {
          boardId: boardId,
          content: this.commentContent,
        },
      })
        .then((res) => {
          console.log("댓글 작성 성공", res);
          this.$router.go()
        })
        .catch((err) => {
          console.log("댓글 작성 실패", err);
        });
      }
    },
    // 좋아요
    boardLike (boardId) {
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/board/like?boardId=${boardId}`,
        method: 'POST',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
      }).then(res => {
        console.log('좋아요 성공', res)
        this.getSelectBoard(boardId)
      }).catch(err => {
        console.log('좋아요 실패', err)
      })
    },
    // 좋아요 취소
    boardUnLike (boardId) {
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/board/unlike?boardId=${boardId}`,
        method: 'DELETE',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        },
      }).then(res => {
        console.log('좋아요 취소 성공', res)
        this.getSelectBoard(boardId)
      }).catch(err => {
        console.log('좋아요 취소 실패', err)
      })
    },
  }
})
</script>

<style scoped>
  .background-div {
    padding: 0.5rem 5rem 0.5rem 5rem;
    text-align: center;
  }
  .board-div {
    width: 24rem;
    height: 48rem;
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
    justify-content: flex-start;
    align-content: center;
    background-color: #ffe8e8;
    overflow-y: scroll;
  }
  .board-card::-webkit-scrollbar {
    display: none;
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
  .comment-text {
    word-break:break-all;
    text-align: start;
    font-size: 12px;
  }
</style>
