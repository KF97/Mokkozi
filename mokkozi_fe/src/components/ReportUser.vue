<template>
  <div class="text-center">
    <v-dialog
      v-model="dialog"
      width="800px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-img
          v-bind="attrs"
          v-on="on"
          max-width="24px"
          max-height="24px"
          src="@/assets/siren.png"
        ></v-img>
      </template>

      <v-card>
        <v-card-title style="display:flex; justify-content:space-between;" class="white">
          <span>사용자 신고하기</span>
          <v-btn color="white" icon>
            <v-icon style="color: #FFB4B4;" @click="exit">fas fa-times-circle</v-icon>
          </v-btn>
        </v-card-title>
        <v-divider></v-divider>
        <div style="margin: 24px; padding: 15px; display:flex; justify-content:start; align-items: center; border-style: solid; border-width: 1px;">
          <v-avatar size="150px" @click="userImageClick(board.userEmail)">
            <img alt="Avatar" :src="propImage">
          </v-avatar>
          <div style="margin-left: 20px;">
            <h3>'{{ nickname }}' 사용자 신고</h3>
            <p>이 사용자를 신고하는 이유를 선택해 주세요.</p>
          </div>
        </div>
        <p id="reason1" class="reason" @click="selectReason('reason1', '혐오성/음란한 사진')">혐오성/음란한 사진</p>
        <p id="reason2" class="reason" @click="selectReason('reason2', '사진도용')">사진도용</p>
        <p id="reason3" class="reason" @click="selectReason('reason3', '금전요구사기')">금전요구사기</p>
        <p id="reason4" class="reason" @click="selectReason('reason4', '허위 프로필 정보')">허위 프로필 정보</p>
        <p id="reason5" class="reason" @click="selectReason('reason5', '성매매/성희롱')">성매매/성희롱</p>
        <p id="reason6" class="reason" @click="selectReason('reason6', '기타')">기타</p>
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
  name: 'ReportUser',
  data: () => ({
    dialog: false,
    reason: '',
    reasonList: [['reason1', '혐오성/음란한 사진'], ['reason2', '사진도용'],
                 ['reason3', '금전요구사기'], ['reason4', '허위 프로필 정보'],
                 ['reason5', '성매매/성희롱'], ['reason6', '기타']],
    propImage: '',
    nickname: '',
    userId: null
  }),
  props: {
    userEmail: {
      type: String
    }
  },
  mounted () {
    this.getuser()
    this.reason = ''
  },
  methods: {
    getuser() {
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/getuser",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
        params: {
          toUserEmail: this.userEmail,
        },
      }).then((resp) => {
        console.log("회원정보 확인: ", resp);
        this.propImage = resp.data.profile
        this.userId = resp.data.id
        this.nickname = resp.data.nickname
      })
    },
    selectReason (id, reason) { // 하나만 선택할 수 있도록 바꿔야 한다
      const show = document.querySelector('#' + id)
      if (this.reason.length && show.classList.contains('selected')) {
        show.classList.remove('selected')
        this.reason = ''
      }
      else if (this.reason.length && !show.classList.contains('selected')) {
          this.reasonList.forEach((reasonIdx) => {
          if (reasonIdx[1] === this.reason) {
            const beforeReason = document.querySelector('#' + reasonIdx[0])
            beforeReason.classList.remove('selected')
          }
        })
        show.className += ' selected'
        this.reason = reason
      }
      else {
        // 1. CSS 변경
        show.className += ' selected'
        // 2. hobby List 변경
        this.reason = reason
      }
    },
    // 사용자 신고
    report () {
      console.log(this.userEmail)
      console.log(this.reason)
      if (this.reason.length) {
         axios({
          url: process.env.VUE_APP_API_URL + '/api/meet/report/user',
          method: 'POST',
          headers:{
            Authorization:"Bearer "+ this.$store.state.jwt
          },
          data: {
            targetId: this.userId, // 아이디 값으로 요청 보내야함
            content: this.reason
          }
        }).then(res => {
          console.log('유저 신고 성공', res)
        }).catch(err => {
          console.log('유저 신고 실패', err)
        })
      }
      this.reasonList.forEach((reasonIdx) => {
        document.querySelector('#' + reasonIdx[0]).classList.remove('selected')
      })
      alert("유저가 신고되었습니다.")
      this.dialog = false
    },
    // x 아이콘으로 닫을 때 reason 초기화
    exit () {
      this.reasonList.forEach((reasonIdx) => {
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
