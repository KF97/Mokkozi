<template>
  <v-bottom-navigation
    color="white"
    grow
    class="footer justify-space-around"
    background-color="#FFB4B4"
  >
    <div v-if="!this.$store.state.meeting">
      <router-link to="/" style="text-decoration: none; color:white">
        <v-btn dark
                    dense
                    color="white"
                    text="text"
                    rounded="rounded"
                    class="my-2">Home
          <v-icon>mdi-history</v-icon>
        </v-btn>
      </router-link>
    </div>
    <div v-else>
      <v-btn dark
                  @click="exitAlert"
                  dense
                  color="white"
                  text="text"
                  rounded="rounded"
                  class="my-2">Home
        <v-icon>mdi-history</v-icon>
      </v-btn>
    </div>


    <router-link v-if="this.$store.state.jwt === ''" to="/login" style="text-decoration: none; color:white">
      <v-btn dark
                  dense
                  color="white"
                  text="text"
                  rounded="rounded"
                  class="my-2">Login
        <v-icon>mdi-heart</v-icon>
      </v-btn>
    </router-link>

    <div v-else>
      <router-link v-if="!this.$store.state.meeting" to="" style="text-decoration: none; color:white">
        <v-btn dark
                    style="text-decoration: none; color:white"
                    dense
                    color="white"
                    text="text"
                    rounded="rounded"
                    @click="logout()"
                    class="my-2">Logout
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </router-link>
      <div v-else>
        <v-btn dark
                    style="text-decoration: none; color:white"
                    dense
                    color="white"
                    text="text"
                    rounded="rounded"
                    @click="exitAlert"
                    class="my-2">Logout
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </div>
    </div>

    <div v-if="this.$store.state.jwt !== ''">
      <router-link v-if="!this.$store.state.meeting" :to="{ name: 'Profile', params: { userEmail: email }}" style="text-decoration: none; color:white">
        <v-btn dark
                    dense
                    color="white"
                    text="text"
                    rounded="rounded"
                    class="my-2">
          <span>Mypage</span>
          <v-icon>mdi-map-marker</v-icon>
        </v-btn>
      </router-link>
      <div>
        <v-btn dark
                    @click="exitAlert"
                    dense
                    color="white"
                    text="text"
                    rounded="rounded"
                    class="my-2">
          <span>Mypage</span>
          <v-icon>mdi-map-marker</v-icon>
        </v-btn>
      </div>
    </div>
  </v-bottom-navigation>
</template>
<script>
export default {
  name: 'Foot',
  data: () => ({
  }),
  methods: {
    logout() {
      this.$store.dispatch("setJwt", '')
      this.$store.dispatch("setNickname", '')
      this.$store.dispatch("setProfile", '')
      this.$store.dispatch("setEmail", '')
      this.$store.dispatch("setAddress", '')
      this.$store.dispatch("setRole", '')
      this.$store.dispatch("setMeeting", false)
      this.$router.push({ name: 'Login' })
    },
    exitAlert() {
      alert("화면에 나가기 버튼을 클릭해주세요!")
    }
  },
  computed: {
    email() {
      const email = this.$store.state.user.email
      return email
    }
  }
}
</script>
<style scoped>
.footer {
    position: fixed;
    top: auto;
    bottom: 0px;
    left: auto;
    width: 700px;
    z-index: 10;
    elevation: level;
}

.footer-text {
  text-decoration: none;
  color: white;
  font-weight: bold;
  margin-left : 0px;
}

.footer-btn {
  margin-left: 60px;
}
</style>
