<template>
    <v-footer class="top" color="#FFB4B4" padless="padless" elevation="4">
        <v-row justify="space-around" align="center" no-gutters="no-gutters">
            <v-btn
            v-if="!this.$store.state.meeting"
            dark
            dense
            color="white"
            text="text"
            rounded="rounded"
            class="my-2 btn">
            <router-link to="/board" style="text-decoration: none; color:white; font-size:18px;">게시판</router-link></v-btn>
            <v-btn
              v-else
              dark
              dense
              color="white"
              text="text"
              rounded="rounded"
              class="my-2 btn"
              @click="exitAlert">게시판
            </v-btn>
            <v-btn
                v-if="!this.$store.state.meeting"
                dark
                dense
                color="white"
                text="text"
                rounded="rounded"
                class="my-2 btn">
            <router-link to="/meeting" style="text-decoration: none; color:white; font-size:18px;">랜선미팅</router-link></v-btn>
            <v-btn
                v-else
                dark
                dense
                color="white"
                text="text"
                rounded="rounded"
                class="my-2 btn"
                @click="exitAlert">랜선미팅
            </v-btn>
        </v-row>
    </v-footer>
</template>
<script>
export default {
  data: () => ({
  }),
  computed: {
    isLogin () {
      const isLogin = this.$store.state.jwt
      return isLogin
    }
  },
  methods: {
    logout () {
      this.$store.dispatch("logout");
    },
    exitAlert() {
      alert("화면에 나가기 버튼을 클릭해주세요!")
    }
  },
}
</script>

<style>
.top {
    position: fixed;
    left: auto;
    top: 0;
    width: 700px;
    z-index: 10;
    }
</style>
