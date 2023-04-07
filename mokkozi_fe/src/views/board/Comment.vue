<template>
  <div class="background-div">
    <div class="comment-div">
      <v-card
        class="comment-card"
        max-width="24rem"
        height="42rem"
      >
        <v-card-title
          style="
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.2rem;
          "
        >
          <v-btn icon>
            <v-icon @click="backToBoardClick">fas fa-chevron-left</v-icon>
          </v-btn>
          <v-spacer></v-spacer>
            <v-toolbar-title class="font-weight-bold">댓글</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-list-item-avatar @click="userImageClick(board.userEmail)">
              <img :src="board.profileUrl">
            </v-list-item-avatar>
        </v-card-title>
        <v-divider style="margin: 0.5rem 0rem;"></v-divider>
        <div v-for="comment in conmmentList" :key="comment.id"  style="display: flex; flex-direction: row; justify-content: center">
          <div style="width: 3rem;">
            <v-list-item-avatar @click="userImageClick(comment.email)">
              <img :src="comment.file_path">
            </v-list-item-avatar>
          </div>
          <div style="width: 17rem;">
            <v-list-item-content>
              <span class="font-weight-bold" style="text-align: start;">{{ comment.nickName }}</span>
              <span class="comment-text">{{ comment.content }}</span>
            </v-list-item-content>
          </div>
        </div>
      </v-card>
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
</template>

<script>
import axios from "axios"

export default {
  name: 'Comment',
  components: {},
  props: {
    boardId: {
      type: Number
    }
  },
  data: () => ({
    commentContent: '',
    conmmentList: '',
    board: ''
  }),
  mounted () {
    this.getCommentList(this.boardId)
    this.getSelectBoard(this.boardId)
  },
  methods: {
    backToBoardClick () {
      this.$router.push({ name: 'Board' })
    },
    userImageClick(userEmail) {
      this.$router.push({ name: "Profile", params: { userEmail: userEmail } });
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
      }).catch(err => {
        console.log('게시물 불러오기 실패', err)
      })
    },
    // 댓글 불러오기
    getCommentList (boardId) {
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/comment?boardId=${boardId}`,
        methods: 'GET',
        headers:{
          Authorization:"Bearer "+ this.$store.state.jwt
        }
      }).then(res => {
        console.log('댓글 불러오기', res)
        this.conmmentList = res.data.commentList
      }).catch(err => {
        console.log('댓글 불러오기 실패', err)
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
  },
}
</script>

<style scoped>
  .background-div {
    margin-top: 50px;
    padding: 0.5rem 5rem 0.5rem 5rem;
    text-align: center;
  }
  .comment-div {
    width: 24rem;
    height: 48rem;
    display: inline-block;
    background-color: #ffe8e8;
    padding: 2rem 2rem;
    border-radius: 1rem;
    overflow-y: scroll;
  }
  .comment-div::-webkit-scrollbar {
    display: none;
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
  .comment-card {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-content: center;
    background-color: #ffe8e8;
    overflow-y: scroll;
  }
  .comment-card::-webkit-scrollbar {
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
  .v-list-item {
    align-items: center;
    display: flex;
    flex: 1 1 100%;
    letter-spacing: normal;
    min-height: 2rem;
    max-width: "24rem";
    padding: 0;
    outline: none;
    position: relative;
    text-decoration: none;
  }
  .v-list-item__content {
    align-items: center;
    align-self: center;
    display: flex;
    flex-wrap: wrap;
    flex: 0;
    overflow: hidden;
    padding: 12px;
  }
  .comment-text {
    word-break:break-all;
    text-align: start;
  }
</style>
