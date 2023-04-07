<template>
  <v-container fluid>
    <!-- row의 height를 지정해야만 align을 통해 start, center, end로 배치할 수 있다. -->
    <v-row justify="center" align="center">
      <v-col class="pa-4 mb-3">
        <div class="login-box" elevation="1" outlined>
          <div class="d-flex flex-wrap justify-center">
            <v-img max-width="400" max-height="200" src="@/assets/text_logo.png"></v-img>
          </div>
          <v-form v-model="formValid">
            <v-text-field label="아이디"
            @keyup.enter="login"
            @change="reset"
            v-model="credentials.email"
            :rules="[rules.mailRequired, rules.email]"
            outlined>
            </v-text-field>

            <v-text-field label="비밀번호"
            @keyup.enter="login"
            @change="reset"
            v-model="credentials.password"
            :rules="[rules.pwRequired, rules.min]"
            :type="showPW ? 'text' : 'password'"
            outlined>
            </v-text-field>

            <v-btn class="mb-3" width="100%" outlined color="#FF9292" @click="login">
              로그인
            </v-btn>
            <v-btn class="mb-3" width="100%" outlined color="#FF9292">
              <v-icon>mdi-google</v-icon>Google 계정으로 로그인
            </v-btn>
            <v-btn class="mb-3" width="100%" outlined color="#FF9292" @click="goToJoin">
              아직 계정이 없다면? 회원가입 하세요!
            </v-btn>
          </v-form>
        </div>
      </v-col>
    </v-row>

    <!-- dialog 부분 -->
    <transition name="fade">
      <v-alert
      v-if="show"
      class="mb-0"
      style="border-bottom-left-radius: 0; border-bottom-right-radius: 0;"
      color="#FF9292"
      dark
      width="100%"
      icon="mdi-material-design"
      border="right">
      아이디 또는 비밀번호를 확인해 주세요
      </v-alert>
    </transition>
  </v-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',
  props: {},
  data: () => ({
    credentials: {
      email: '',
      password: '',
    },
    show: false,
    formValid: false,
    showPW: false,
    rules: {
      mailRequired: value => !!value || '아이디를 입력해 주세요.',
      pwRequired: value => !!value || '비밀번호를 입력해 주세요.',
      min: v => v.length >= 8 || '비밀번호는 8자 이상입니다.',
      email: v => /.+@.+/.test(v) || '유효한 이메일을 입력해 주세요'
    }
  }),
  methods: {
    goToJoin () {
      this.$router.push('Join')
    },
    login () {

      axios({
        url: process.env.VUE_APP_API_URL + '/api/meet/user/login',
        method: 'POST',
        data: {
          email: this.credentials.email,
          password: this.credentials.password
        }
      }).then(resp => {
        if (resp.status === 200) {
          console.log("로그인 성공! 로그인 반환 정보 : ", resp)
          this.$store.dispatch("setJwt", resp.data.token)
          this.$store.dispatch("setNickname", resp.data.nickName)
          this.$store.dispatch("setProfile", resp.data.profile)
          this.$store.dispatch("setEmail", resp.data.email)
          this.$store.dispatch("setRole", resp.data.role)
          // this.$store.dispatch("setAddress", resp.data.address)

          // Role에 따라 다른 곳으로 보낸다.
          if (resp.data.role === '사용자') {
            this.$router.push('/')
          }
          else {
            // 관리자 메인 페이지로 이동한다.
          }
        }
      }).catch(err => {
        this.show = true
      })
    },
    reset () {
      this.show = false
    }

  }
}
</script>

<style lang="scss" scoped>
.login-box {
  border-radius: 30px;
}
</style>
