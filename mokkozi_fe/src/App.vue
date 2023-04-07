<template>
  <v-app class="target">
    <v-main class="font-test" style="background-color: #ffe8e8">
      <v-container class="com">
        <v-row
          no-gutters
          justify-sm="center"
          justify-md="center"
          align="center"
        >
          <!-- 소개 페이지 -->
          <v-col
            cols="5"
            class="d-none d-sm-none d-md-none d-lg-flex logo-page"
          >
            <!-- 창 크기에 따라 전시 or 비전시 -->
            <div class="pa-5 d-flex flex-wrap flex-column mt-0 pt-0">
              <!-- 로고 이미지 -->
              <div class="mb-10">
                <router-link to="/">
                  <v-img
                    src="@/assets/logo.png"
                    max-height="200"
                    max-width="200"
                    alt="로고 이미지"
                  ></v-img>
                </router-link>
              </div>
              <!-- 메인 이미지 -->
              <div class="mb-10">
                <v-img
                  class="main-img"
                  src="@/assets/images/main.png"
                  alt="메인 이미지"
                >
                </v-img>
              </div>
              <!-- 서비스 소개 문구 -->
              <div style="text-align: center">
                <h3>새로운 AR 소개팅 서비스를 체험해 보세요</h3>
                <br />
                <p>
                  쉽고 빠르게, 그리고 부담없이 마음에 드는 이성을 찾고
                  싶다면?<br />
                  지금 바로 모꼬지를 통해 새로운 만남을 경험해 보세요
                </p>
              </div>
            </div>
          </v-col>
          <!-- 매번 바뀌는 페이지 -->
          <v-col class="d-flex flex-row justify-center" cols="7">
            <top></top>
            <main>
              <router-view />
            </main>
            <foot></foot>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default {
  components: {},
  name: "App",
  data: () => ({
    drawer: false,
  }),
};
</script>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700&display=swap");
.com {
  height: 100%;
  background-color: white;
  position: relative;
}
.main-img {
  border-radius: 30px;
}
.logo-page * {
  margin: auto;
}
.font-test {
  font-family: "Noto Sans KR", sans-serif;
}
</style>
