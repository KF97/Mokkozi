<template>
  <div class="text-center">
    <v-dialog
      v-model="dialog"
      width="800px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="#FFB4B4"
          dark
          v-bind="attrs"
          v-on="on"
        >
          신고하기
        </v-btn>
      </template>

      <v-card>
        <v-card-title style="display:flex; justify-content:space-between;" class="white">
          <span>게시글 신고하기</span>
          <v-btn color="white" icon>
            <v-icon style="color: #FFB4B4;" @click="exit">fas fa-times-circle</v-icon>
          </v-btn>
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text style="font-size: 1rem; font-weight: bold; padding: 24px">
          사유 선택
        </v-card-text>
        <v-card-text>
          여러 사유에 해당되는 경우 대표적인 사유 1개를 선택해 주세요.
        </v-card-text>
        <p id="boardReason1" class="reason" @click="selectReason('boardReason1', '부적절한 홍보 게시글')">부적절한 홍보 게시글</p>
        <p id="boardReason2" class="reason" @click="selectReason('boardReason2', '음란성')">음란성</p>
        <p id="boardReason3" class="reason" @click="selectReason('boardReason3', '명예훼손/사생활 침해 및 저작권 침해 등')">명예훼손/사생활 침해 및 저작권 침해 등</p>
        <p id="boardReason4" class="reason" @click="selectReason('boardReason4', '혐오 발언 또는 상징')">혐오 발언 또는 상징</p>
        <p id="boardReason5" class="reason" @click="selectReason('boardReason5', '불법 또는 규제 상품 판매')">불법 또는 규제 상품 판매</p>
        <p id="boardReason6" class="reason" @click="selectReason('boardReason6', '기타')">기타</p>
        <v-divider style="margin-top: 21px"></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="#FFB4B4"
            @click="report"
          >
            신고
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ReportBoard',
  data: () => ({
    dialog: false,
    boardReason: '',
    boardReasonList: [['boardReason1', '부적절한 홍보 게시글'], ['boardReason2', '음란성'],
                 ['boardReason3', '명예훼손/사생활 침해 및 저작권 침해 등'], ['boardReason4', '혐오 발언 또는 상징'],
                 ['boardReason5', '불법 또는 규제 상품 판매'], ['boardReason6', '기타']]
  }),
  props: {
    boardId: {
      type: Number
    }
  },
  mounted () {
    console.log(this.boardId)
    this.boardReason = ''
  },
  methods: {
    selectReason (id, reason) { // 하나만 선택할 수 있도록 바꿔야 한다
      const show = document.querySelector('#' + id)
      if (this.boardReason.length && show.classList.contains('selected')) {
        show.classList.remove('selected')
        this.boardReason = ''
      }
      else if (this.boardReason.length && !show.classList.contains('selected')) {
          this.boardReasonList.forEach((reasonIdx) => {
          if (reasonIdx[1] === this.boardReason) {
            const beforeReason = document.querySelector('#' + reasonIdx[0])
            beforeReason.classList.remove('selected')
          }
        })
        show.className += ' selected'
        this.boardReason = reason
      }
      else {
        // 1. CSS 변경
        show.className += ' selected'
        // 2. hobby List 변경
        this.boardReason = reason
      }
    },
    // 게시판 신고
    report () {
      if (this.boardReason.length) {
        axios({
          url: process.env.VUE_APP_API_URL + '/api/meet/report/board',
          method: 'POST',
          headers:{
            Authorization:"Bearer "+ this.$store.state.jwt
          },
          data: {
            boardId: this.boardId,
            content: this.boardReason
          }
        }).then(res => {
          console.log('게시판 신고 성공', res)
        }).catch(err => {
          console.log('게시판 신고 실패', err)
        })
      }
      this.boardReasonList.forEach((reasonIdx) => {
        document.querySelector('#' + reasonIdx[0]).classList.remove('selected')
      })
      alert("게시글이 신고되었습니다.")
      this.dialog = false
    },
    // x 아이콘으로 닫을 때 reason 초기화
    exit () {
      this.boardReasonList.forEach((reasonIdx) => {
        document.querySelector('#' + reasonIdx[0]).classList.remove('selected')
      })
      this.dialog = false
    }
  }
}
</script>

<style scoped>
  .reason {
  border: 1px solid lightgrey;
  background-color: white;
  padding: 10px;
  border-radius: 10px;
  -webkit-transition: 500ms;
  margin: 3px 24px;
  }
  .reason:hover {
    background-color: #FF9292;
    cursor: pointer;
    -webkit-transition: 500ms;
  }
  .selected {
    background-color: #FF9292;
    border: 1px solid #FF9292;
  }
</style>
