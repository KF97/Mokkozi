<template>
  <v-container class="board-container">
    <div>
      <v-card class="board-card" max-height="50rem">
        <div class="profile" style="height: 100%; position: relative">
          <report-user
            :userEmail="this.$route.params.userEmail"
            class="report-icon"
          />
          <!-- 배경 이미지 부분 -->
          <img class="bg-img" :src="defaultImage" alt="커버사진" />
          <!-- 사용자 프로필 이미지 부분 -->
          <div class="user-img-frame" style="overflow: hidden">
            <div class="sub-frame">
              <img
                class="user-img"
                :src="propImage"
                alt=""
                width="100px"
                height="100px"
                style="border-radius: 100px; object-fit: cover"
              />
            </div>
          </div>
          <!-- 사용자 프로필 이미지 수정 버튼 -->
          <div class="user-img-mod-frame">
            <div class="mod-sub-frame">
              <label for="ex_file">
                <img :src="camera" alt="카메라 아이콘"
              /></label>
              <input type="file" id="ex_file" />
            </div>
          </div>
          <div class="user-info">
            <div>이름 : {{ this.nickname }}</div>
            <div>이메일 : {{ this.email }}</div>
          </div>
          <div class="user-follow">
            <v-btn
              @click="follow"
              dark="dark"
              dense="dense"
              color="#FFB4B4"
              class="my-2 font-weight-black"
            >
              팔로우
            </v-btn>
          </div>
          <!-- <div class="user-follow">
                            <v-btn @click="unfollow" dark="dark" dense="dense" color="#FFB4B4" class="my-2 font-weight-black">
                               팔로우 취소
                            </v-btn>
                        </div> -->
          <!-- 인적 사항 부분 -->
          <div>
            <v-card
              style="border-radius: 10px"
              class="mt-16 ml-10 mr-10"
              elevation="2"
            >
              <v-container>
                <v-layout
                  row="row"
                  wrap="wrap"
                  style="text-align: center; color: #ffb4b4"
                >
                  <v-flex>
                    <v-dialog width="365">
                      <template v-slot:activator="{ on, attrs }">
                        <div
                          @click="follower"
                          color="red lighten-2"
                          dark="dark"
                          v-bind="attrs"
                          v-on="on"
                        >
                          <div class="font-weight-black">팔로워</div>
                          <div class="font-weight-medium">{{ num2 }}</div>
                        </div>
                      </template>
                      <v-card>
                        <div class="follow-title">
                          <div>팔로워</div>
                        </div>
                        <div v-for="(item, idx) in followers" :key="idx">
                          <v-card-text
                            style="border-bottom: 1px solid gainsboro"
                          >
                            <div>
                              <v-avatar size="55px">
                                <img
                                  alt="Avatar"
                                  width="50px"
                                  :src="item.profileUrl"
                                />
                              </v-avatar>
                              {{ item.nickname }}
                            </div>
                          </v-card-text>
                        </div>
                      </v-card>
                    </v-dialog>
                  </v-flex>
                  <v-flex>
                    <v-dialog width="365">
                      <template v-slot:activator="{ on, attrs }">
                        <div
                          @click="following"
                          color="red lighten-2"
                          dark="dark"
                          v-bind="attrs"
                          v-on="on"
                        >
                          <div class="font-weight-black">팔로잉</div>
                          <div class="font-weight-medium">{{ num1 }}</div>
                        </div>
                      </template>
                      <v-card>
                        <div class="follow-title">
                          <div>팔로잉</div>
                        </div>
                        <div v-for="(item, idx) in followings" :key="idx">
                          <v-card-text
                            style="border-bottom: 1px solid gainsboro"
                          >
                            <div>
                              <v-avatar size="55px">
                                <img
                                  alt="Avatar"
                                  width="50px"
                                  :src="item.profileUrl"
                                />
                              </v-avatar>
                              {{ item.nickname }}
                            </div>
                          </v-card-text>
                        </div>
                      </v-card>
                    </v-dialog>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card>

            <v-divider class="mt-6 mb-3"></v-divider>

            <span style="text-align: left">
              <div class="mt-6 ml-10 mr-10">
                <div class="mb-1">저는 이런 사람입니다</div>
                <span
                  class="text-center"
                  v-for="(item, idx) in interests"
                  :key="idx"
                >
                  <v-chip class="mr-2" :color="colors[idx]" text-color="white">
                    {{ item.interest }}
                  </v-chip>
                </span>
                <div class="mt-5 mb-1">저는 이런 사람을 만나고 싶어요</div>
                <div>
                  <v-chip class="mr-2" color="purple" text-color="white">
                    자전거타기
                  </v-chip>
                  <v-chip class="mr-2" color="pink" text-color="white">
                    맛집탐방
                  </v-chip>
                  <v-chip class="mr-2" color="grey" text-color="white">
                    운동
                  </v-chip>
                </div>
              </div>

              <v-divider class="mt-6 mb-3"></v-divider>
              <div style="text-align: center">게시물</div>
              <div>
                <v-row>
                  <v-col
                    v-for="n in this.images"
                    :key="n"
                    class="d-flex child-flex mt-3"
                    cols="4"
                  >
                    <v-img
                      :src="n.file_path"
                      aspect-ratio="1"
                      class="grey lighten-2"
                    >
                      <template v-slot:placeholder>
                        <v-row
                          class="fill-height ma-0"
                          align="center"
                          justify="center"
                        >
                        </v-row>
                      </template>
                    </v-img>
                  </v-col>
                </v-row>
              </div>
            </span>
            <div></div>
          </div>
        </div>
        <!-- end of 사용자 프로필 -->
      </v-card>
    </div>
  </v-container>
</template>

<script>
import defaultImage from "@/assets/images/커버.png";
import camera from "../assets/images/camera.png";
import ReportUser from "./ReportUser";
import axios from "axios";
// import { mapGetters } from "vuex";

export default {
  name: "File",
  created() {
    this.getuser();
    this.follower();
    this.following();
    this.profile_image();
  },
  computed: {},
  components: {
    ReportUser,
  },
  data: () => ({
    defaultImage: defaultImage,
    propImage: "",
    camera: camera,
    profile: "",
    email: "",
    nickname: "",
    followers: [],
    followings: [],
    images: [],
    interests: [],
    num1: "",
    num2: "",
    colors: ["primary", "orange", "pink", "green", "red"],
  }),
  methods: {
    getuser() {
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/getuser",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
        params: {
          toUserEmail: this.$route.params.userEmail,
        },
      }).then((resp) => {
        console.log("회원정보 확인: ", resp);
        this.propImage = resp.data.user.profile;
        this.nickname = resp.data.user.nickname;
        this.email = resp.data.user.email;
        this.interests = resp.data.userInterestDto;
      });
    },

    follow() {
      console.log(this.$route.params.userEmail);
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/follow",
        method: "POST",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
        params: {
          toUserEmail: this.$route.params.userEmail,
        },
      }).then((resp) => {
        console.log("팔로우 가즈아: ", resp);
        console.log((this.followings.length += 1));
      });
    },

    unfollow() {
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/unfollow",
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
        params: {
          followId: this.$route.params.userEmail,
        },
      }).then((resp) => {
        console.log("언팔로우 가즈아: ", resp);
      });
    },

    follower() {
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/followers",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
      }).then((resp) => {
        console.log("팔로워 수 : ", resp);
        this.followers = resp.data.followers;
        console.log(resp.data.followers.length);
        this.num2 = resp.data.followers.length;
        this.$store.dispatch("setFollowers", resp.data.followers);
      });
    },
    following() {
      console.log("팔로잉 목록");
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/following",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
      }).then((resp) => {
        console.log("팔로잉 목록 : ", resp);
        this.followings = resp.data.followers;
        this.$store.state.follow = resp.data.followers.length;
        this.num1 = resp.data.followers.length;
        this.$store.dispatch("setFollowing", resp.data.following);
      });
    },
    profile_image() {
      console.log("프로필 피드사진 목록");
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/gallery/findByEmail",
        method: "POST",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
      }).then((resp) => {
        console.log("사진 목록 : ", resp.data.galleryList);
        this.images = resp.data.galleryList;
      });
    },
  },
};
</script>
<style scoped>
.board-container {
  overflow-y: scroll;
}
.board-container::-webkit-scrollbar {
  display: none;
}
.background-div {
  padding: 0.5rem 5rem 0.5rem 5rem;
  text-align: center;
}
.board-div {
  width: 24rem;
  height: 32rem;
  display: inline-block;
  background-color: #ffe8e8;
  padding: 2rem 2rem;
  border-radius: 1rem;
}
.v-card-title {
  text-align: center;
}
.report-icon {
  position: absolute;
  bottom: auto;
  top: 40px;
  left: auto;
  right: 40px;
  cursor: pointer;
}
</style>
