<template>
  <v-container fluid>
    <v-row
      justify="center"
      align="center"
      style="width: 600px; height: 750px; overflow-y: auto"
    >
      <!-- validation Observer 통해 값의 유효성을 감시한다. -->
      <validation-observer ref="observer" v-slot="{ invalid }">
        <form @submit.prevent="submit">
          <v-col class="input" cols="10">
            <h3>
              💑 안녕하세요! 모꼬지에 오신걸 환영합니다😊 <br />기본 인적 사항을
              입력해 주세요!
            </h3>
            <!-- 이메일 입력란 -->
            <ValidationProvider
              name="이메일"
              rules="required"
              v-slot="{ errors }"
            >
              <v-text-field
                class="m-0"
                label="이메일"
                v-model="joinInfo.email"
                :error-messages="errors"
                placeholder="이메일을 입력하세요."
                prepend-icon="mdi-email"
                @keypress="validEmail"
              />
            </ValidationProvider>

            <!-- 사용 가능한 이메일인 경우 -->
            <v-alert v-if="emailCheck" dense type="info">
              사용 가능한 이메일입니다.
            </v-alert>
            <!-- 중복된 이메일인 경우 -->
            <v-alert
              v-else-if="emailCheck === false"
              dense
              outlined
              type="error"
            >
              중복된 이메일입니다. 다른 이메일을 입력해 주세요.
            </v-alert>

            <!-- 로딩 스피너를 보여준다. -->
            <v-progress-circular
              v-if="emailLoading"
              indeterminate
              color="#FF9292"
              size="20"
            />

            <!-- 닉네임 -->
            <ValidationProvider
              name="닉네임"
              rules="required|max:6"
              v-slot="{ errors }"
            >
              <v-text-field
                label="닉네임"
                v-model="joinInfo.nickName"
                :error-messages="errors"
                prepend-icon="mdi-account-box-outline"
                @keyup="validNickname"
                placeholder="닉네임을 입력하세요."
              />
            </ValidationProvider>
            <!-- 닉네임 유효성 검증 결과 출력 -->

            <!-- 사용 가능한 닉네임인 경우 -->
            <v-alert v-if="nicknameCheck" dense type="info">
              사용 가능한 별명입니다.
            </v-alert>
            <!-- 중복된 닉네임인 경우 -->
            <v-alert
              v-else-if="nicknameCheck === false"
              dense
              outlined
              type="error"
            >
              중복된 별명입니다. 다른 별명을 입력해 주세요.
            </v-alert>

            <!-- 로딩 스피너를 보여준다. -->
            <v-progress-circular
              v-if="nicknameLoading"
              indeterminate
              color="#FF9292"
              size="20"
            />

            <!-- 비밀번호 -->
            <ValidationProvider
              name="비밀번호"
              rules="required|min:10|max:15"
              v-slot="{ errors }"
            >
              <v-text-field
                label="비밀번호"
                v-model="joinInfo.password"
                :error-messages="errors"
                prepend-icon="mdi-lock"
                placeholder="최소 8자, 숫자와 특수문자 결합"
                type="password"
              />
            </ValidationProvider>

            <!-- 생년월일 -->
            <ValidationProvider
              name="생년월일"
              rules="required"
              v-slot="{ errors }"
            >
              <v-menu
                v-model="birthMenu"
                :close-on-content-click="false"
                :nudge-right="40"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template v-slot:activator="{ on, attrs }" class="mb-3">
                  <v-text-field
                    v-model="joinInfo.birth"
                    label="생년월일"
                    prepend-icon="mdi-calendar"
                    :error-messages="errors"
                    placeholder="클릭하여 생년월일을 선택해 주세요."
                    readonly
                    v-bind="attrs"
                    v-on="on"
                  />
                </template>

                <v-date-picker
                  v-model="joinInfo.birth"
                  @input="birthMenu = false"
                />
              </v-menu>
            </ValidationProvider>

            <!-- 성별 입력 -->
            <ValidationProvider
              name="성별"
              rules="required"
              v-slot="{ errors }"
            >
              <v-select
                :items="genderItems"
                v-model="joinInfo.gender"
                prepend-icon="mdi-gender-male-female"
                :error-messages="errors"
                label="성별"
              />
            </ValidationProvider>
          </v-col>

          <v-col class="input" cols="10">
            <h3>
              🏠 다음으로 현재 거주중인 주소지를 입력해 주세요!<br />
              (주변에 있는 이성을 추천할 때 활용됩니다)
            </h3>
            <!-- Address 1 -->
            <ValidationProvider
              name="주소"
              rules="required"
              v-slot="{ errors }"
            >
              <v-text-field
                label="주소 입력을 위해 클릭해 주세요"
                v-model="joinInfo.address"
                :error-messages="errors"
                prepend-icon="mdi-home"
                placeholder="'주소찾기'를 클릭해 주세요"
                readonly
                @click="daumPostCode"
                style="cursor: pointer"
              />
            </ValidationProvider>

            <!-- Address 2 -->
            <ValidationProvider
              name="상세주소"
              rules="required"
              v-slot="{ errors }"
            >
              <v-text-field
                label="상세주소 입력을 위해 클릭해 주세요"
                v-model="joinInfo.extAddress"
                prepend-icon="mdi-home"
                :error-messages="errors"
                placeholder="상세 주소를 입력하세요."
              />
            </ValidationProvider>

            <!-- 주소찾기 버튼 -->
            <!-- <v-btn class="mb-2" rounded color="#FF9292" @click="daumPostCode">
              주소찾기
            </v-btn> -->
          </v-col>

          <!-- 관심사 선택하기 -->
          <v-col class="input" cols="10">
            <h3>
              🎈 다음으로 본인의 관심사를 선택해 주세요! <br />
              (최소 1개 ~ 5개까지 선택해 주세요😄)
            </h3>
            <div class="d-flex-column mx-3 mb-5">
              <span id="애니" class="hobby" @click="changeHobby('애니', '애니')"
                >애니</span
              >
              <span
                id="글쓰기"
                class="hobby"
                @click="changeHobby('글쓰기', '글쓰기')"
                >글쓰기</span
              >
              <span
                id="자기계발"
                class="hobby"
                @click="changeHobby('자기계발', '자기계발')"
                >자기계발</span
              >
              <span
                id="재테크"
                class="hobby"
                @click="changeHobby('재테크', '재테크')"
                >재테크</span
              >
              <span
                id="그림그리기"
                class="hobby"
                @click="changeHobby('그림그리기', '그림그리기')"
                >그림그리기</span
              >
              <span
                id="봉사활동"
                class="hobby"
                @click="changeHobby('봉사활동', '봉사활동')"
                >봉사활동</span
              >
              <span id="댄스" class="hobby" @click="changeHobby('댄스', '댄스')"
                >댄스</span
              >
              <span id="IT" class="hobby" @click="changeHobby('IT', 'IT')"
                >IT</span
              >
              <span id="산책" class="hobby" @click="changeHobby('산책', '산책')"
                >산책</span
              >
              <span
                id="자전거타기"
                class="hobby"
                @click="changeHobby('자전거타기', '자전거타기')"
                >자전거타기</span
              >
              <span
                id="드라이브"
                class="hobby"
                @click="changeHobby('드라이브', '드라이브')"
                >드라이브</span
              >
              <span id="쇼핑" class="hobby" @click="changeHobby('쇼핑', '쇼핑')"
                >쇼핑</span
              >
              <span id="독서" class="hobby" @click="changeHobby('독서', '독서')"
                >독서</span
              >
              <span id="패션" class="hobby" @click="changeHobby('패션', '패션')"
                >패션</span
              >
              <span id="술" class="hobby" @click="changeHobby('술', '술')"
                >술</span
              >
              <span id="노래" class="hobby" @click="changeHobby('노래', '노래')"
                >노래</span
              >
              <span id="운동" class="hobby" @click="changeHobby('운동', '운동')"
                >운동</span
              >
              <span id="요리" class="hobby" @click="changeHobby('요리', '요리')"
                >요리</span
              >
              <span
                id="악기연주"
                class="hobby"
                @click="changeHobby('악기연주', '악기연주')"
                >악기연주</span
              >
              <span id="여행" class="hobby" @click="changeHobby('여행', '여행')"
                >여행</span
              >
              <span
                id="드라마"
                class="hobby"
                @click="changeHobby('드라마', '드라마')"
                >드라마</span
              >
              <span id="영화" class="hobby" @click="changeHobby('영화', '영화')"
                >영화</span
              >
              <span
                id="사진촬영"
                class="hobby"
                @click="changeHobby('사진촬영', '사진촬영')"
                >사진촬영</span
              >
              <span id="게임" class="hobby" @click="changeHobby('게임', '게임')"
                >게임</span
              >
              <span
                id="공연관람"
                class="hobby"
                @click="changeHobby('공연관람', '공연관람')"
                >공연관람</span
              >
              <span
                id="맛집탐방"
                class="hobby"
                @click="changeHobby('맛집탐방', '맛집탐방')"
                >맛집탐방</span
              >
              <span id="음악" class="hobby" @click="changeHobby('음악', '음악')"
                >음악</span
              >
              <span
                id="외국어"
                class="hobby"
                @click="changeHobby('외국어', '외국어')"
                >외국어</span
              >
            </div>
          </v-col>

          <!-- 사용자 프로필, 일상 이미지 업로드 -->
          <v-col class="input" cols="10">
            <!-- 대표 프로필 설정하기 -->
            <h3>
              🎉이성에게 보여줄 예쁘고 멋진 대표 프로필 이미지를 선택해 주세요😄
            </h3>
            <ValidationProvider
              name="프로필이미지"
              rules="fileRequired:2000|required"
              v-slot="{ errors }"
            >
              <v-file-input
                v-model="joinInfo.imgFile"
                truncate-length="15"
                accept="image/*"
                prepend-icon="mdi-account-circle"
                :error-messages="errors"
                @change="myProfile"
                placeholder="프로필 이미지를 설정해 주세요"
                show-size
              />
            </ValidationProvider>

            <!-- 이미지 미리보기 -->
            <v-img
              v-if="isProfile"
              class="myProfile"
              lazy-src="https://picsum.photos/id/11/10/6"
              max-width="500"
              max-height="500"
              :src="joinInfo.profileURL"
            />

            <!-- 마이 프로필에 띄울 이미지 3장 업로드 -->
            <h3>
              👍마지막이에요! 이성에게 보여줄 일상적인 나만의 모습을 선택해
              주세요! (최대 7장까지 선택 가능합니다.)
            </h3>
            <ValidationProvider
              name="이미지"
              rules="fileRequired:10000|required"
              v-slot="{ errors }"
            >
              <v-file-input
                v-model="joinInfo.myImages"
                multiple
                truncate-length="15"
                :error-messages="errors"
                accept="image/*"
                prepend-icon="mdi-tooltip-image"
                @change="myImages"
                show-size
                placeholder="상대방에게 보여줄 나만의 이미지를 올려 주세요! (최대 7장)"
              />
            </ValidationProvider>

            <!-- 올린 이미지 미리 보기 -->
            <v-carousel class="carousel" v-if="isCarousel">
              <v-carousel-item
                style="width: 500px; height: auto"
                v-for="(url, i) in joinInfo.myImagesURL"
                :key="i"
                :src="url"
                reverse-transition="fade-transition"
                transition="fade-transition"
              ></v-carousel-item>
            </v-carousel>

            <!-- 관심사를 선택하지 않았을 때 출력되는 경고 페이지 -->
            <v-alert v-if="isAlert" dense outlined type="error">
              <strong>관심사</strong>를 선택해 주세요!
            </v-alert>

            <!-- 회원가입 버튼 -->
            <v-btn
              @click="join()"
              elevation="2"
              :disabled="invalid"
              width="100%"
              color="#FF9292"
            >
              회원가입
            </v-btn>
          </v-col>
        </form>
      </validation-observer>
    </v-row>
  </v-container>
</template>

<script>
import { required, email, size, max, min } from "vee-validate/dist/rules";
import { ValidationObserver, ValidationProvider, extend } from "vee-validate";
import axios from "axios";
import router from "../../router";
import * as commonFunc from "../../common/commonFunc"

extend("emailValidate", {
  ...email,
  message: "이메일을 입력해 주세요.",
});

extend("required", {
  ...required,
  message: "{_field_}을(를) 입력해 주세요",
});

extend("fileRequired", {
  ...size,
  message: '{_field_}의 최대 용량은 2MB입니다.',
})

extend("max", {
  ...max,
  message: '{_field_}의 최대 길이는 {length}자입니다.'
})

extend("min", {
  ...min,
  message: '{_field_}의 최소 길이는 {length}자입니다.'
})

export default {
  components: {
    ValidationObserver,
    ValidationProvider,
  },
  name: "Join",
  props: {},
  data: () => ({
    joinInfo: {
      email: "",
      nickName: "",
      password: "",
      address: "",
      extAddress: "",
      gender: "",
      birth: "",
      imgFile: null, // X 버튼 누르는 경우 null로 바뀐다.
      profileURL: "",
      myImages: [],
      myImagesURL: [],
      role: "사용자",
      hobby: [],
    },
    isCarousel: false, // Carousel 보이게 하는 거..
    isProfile: false, // Profile 보이게 하는 거..
    isAlert: false, // 관심사 선택 안했을 때 경고 문구...
    zcode: "",
    genderItems: ["남", "여"],
    birthMenu: false,
    timer: null,
    timer2: null,
    emailCheck: "", // 이메일 중복 확인 인자.
    emailLoading: false, // 로딩 스피너 표시 유무.
    nicknameCheck: "", // 닉네임 중복 확인 인자.
    nicknameLoading: false, // 닉네임 로딩 스피너 표시 유무.
  }),
  // watch: {
  //   emailCheck (newVal, oldVal) {
  //     this.emailCheck = newVal
  //   }
  // },
  methods: {
    daumPostCode() {
      new window.daum.Postcode({
        oncomplete: (data) => {
          // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
          // 도로명 주소의 노출 규칙에 따라 주소를 조합한다.
          // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
          let fullRoadAddr = data.roadAddress; // 도로명 주소 변수
          let extraRoadAddr = ""; // 도로명 조합형 주소 변수
          // 법정동명이 있을 경우 추가한다. (법정리는 제외)
          // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
          if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
            extraRoadAddr += data.bname;
          }
          // 건물명이 있고, 공동주택일 경우 추가한다.
          if (data.buildingName !== "" && data.apartment === "Y") {
            extraRoadAddr +=
              extraRoadAddr !== ""
                ? ", " + data.buildingName
                : data.buildingName;
          }
          // 도로명, 지번 조합형 주소가 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
          if (extraRoadAddr !== "") {
            extraRoadAddr = " (" + extraRoadAddr + ")";
          }
          // 도로명, 지번 주소의 유무에 따라 해당 조합형 주소를 추가한다.
          if (fullRoadAddr !== "") {
            fullRoadAddr += extraRoadAddr;
          }
          // 우편번호와 주소 정보를 해당 필드에 넣는다.
          this.zcode = data.zonecode;
          this.joinInfo.address = fullRoadAddr;
        },
      }).open();
    },
    changeHobby(id, hobbyName) {
      // 관심사 최대 5개로 한다.
      if (this.joinInfo.hobby.length > 5) {
        this.isAlert = true;
        this.joinInfo.hobby = []
        return
      }

      // 관심사를 선택했으므로, 경고창을 제거한다.
      this.isAlert = false;

      const show = document.querySelector("#" + id);
      // 이미 추가한 관심사인 경우
      if (show.classList.contains("selected")) {
        // 1. CSS 변경
        show.classList.remove("selected");
        // 2. hobby list 변경
        this.joinInfo.hobby.splice(this.joinInfo.hobby.indexOf(hobbyName), 1);

        // 아직 추가하지 않은 관심사인 경우
      } else {
        // 1. CSS 변경
        show.className += " selected";
        // 2. hobby List 변경
        this.joinInfo.hobby.push(hobbyName);
      }
    },
    join() {
      // 관심사를 입력했는지 확인한다.
      if (this.joinInfo.hobby.length === 0) {
        this.isAlert = true;
        return;
      }

      console.log("회원가입 요청합니다.");

      const formData = new FormData();

      formData.append("file", this.joinInfo.imgFile);
      formData.append("email", this.joinInfo.email);
      this.joinInfo.imgFile = formData;

      axios({
          url: process.env.VUE_APP_API_URL + '/api/meet/user/join',
          method: 'POST',
          headers: {
            "Content-Type": "application/json"
          },
          data: {
            email: this.joinInfo.email.trim(),
            nickname: this.joinInfo.nickName.trim(),
            password: this.joinInfo.password.trim(),
            address: this.joinInfo.address,
            extAddress: this.joinInfo.extAddress,
            gender: this.joinInfo.gender,
            birth: this.joinInfo.birth,
            role: this.joinInfo.role,
            hobby: this.joinInfo.hobby
          }
      }).then(resp => {
        if (resp.data.statusCode == 200) {
          console.log(
            "회원가입 완료! 사용자 프로필 이미지 등록합니다. ",
            this.joinInfo.imgFile
          );
          // 프로필 이미지 등록 시작합니다.
          axios({
            url: process.env.VUE_APP_API_URL + "/api/meet/gallery/myProfile",
            method: "POST",
            headers: {
              "Content-Type": "multipart/form-data",
            },
            data: this.joinInfo.imgFile,
          }).then(() => {
            console.log("프로필 이미지 등록 완료! 사용자 이미지 등록합니다. ");
            // 사용자가 지정한 이미지 파일 등록합니다.
            const formData2 = new FormData();

            // 리스트로 넘어오게 된다.
            // formData2.append("files", this.joinInfo.myImages)

            for (let i = 0; i < this.joinInfo.myImages.length; i++) {
              formData2.append("files", this.joinInfo.myImages[i]);
            }

            formData2.append("email", this.joinInfo.email);
            formData2.append("boardId", "");
            formData2.append("sort", "사용자");
            this.joinInfo.myImages = formData2;

            console.log("사용자 이미지 리스트 정보 : ", this.joinInfo.myImages);

            axios({
              url: process.env.VUE_APP_API_URL + "/api/meet/gallery/images",
              method: "POST",
              headers: {
                "Content-Type": "multipart/form-data",
              },
              data: this.joinInfo.myImages,
            }).then(
              // 회원가입, 프로필 img path 수정, 프로필 이미지 등록이 끝났으면, login 페이지로 GoGo
              router.push("/Login")
            );
          });
        }
      });
    },
    myProfile() {
      // X 버튼 누르는 경우 초기화
      if (this.joinInfo.imgFile === null) {
        this.isProfile = false;
        return
      }

      // 이미지에 특수 문자가 포함되어 있는 경우 제외
      if (commonFunc.checkMyImageLetter(this.joinInfo.imgFile)) {
        this.joinInfo.imgFile = null
        return
      }

      // 그 외의 경우
      this.joinInfo.profileURL = URL.createObjectURL(this.joinInfo.imgFile);
      this.isProfile = true;
    },
    myImages() {
      // X 버튼을 누르는 경우에는, 초기화를 한 것이므로
      if (this.joinInfo.myImages.length === 0) {
        this.isCarousel = false;
        return
      }

      // 이미지에 특수 문자가 포함되어 있는 경우 제외
      if (commonFunc.checkMyImagesLetter(this.joinInfo.myImages)) {
        this.joinInfo.myImages = []
        return
      }

      // 갯수를 제한한다.
      if (this.joinInfo.myImages.length > 7) {
        alert("이미지는 최대 7장까지 선택 가능합니다.");
        this.joinInfo.myImages = [];
        return
      }

      this.joinInfo.myImagesURL = commonFunc.makeLocalURL(this.joinInfo.myImages);
      this.isCarousel = true;
    },
    validEmail() {
      // 공백 값인 경우 제외
      if (this.joinInfo.email === "") {
        return;
      }

      // 값을 입력하기 시작하면, 중복 시 발생한 경고문구를 제거한다.
      this.emailCheck = "";
      this.emailLoading = true;

      // Timer값이 null이 아니라면, null로 만들어준다.
      if (this.timer) {
        clearTimeout(this.timer);
      }

      // axios 요청 보낸다.
      this.timer = setTimeout(() => {
        axios({
          url: process.env.VUE_APP_API_URL + "/api/meet/user/validEmail",
          method: "POST",
          data: {
            email: this.joinInfo.email,
          },
        }).then((resp) => {
          // 사용 가능
          if (resp.data.statusCode === 404) {
            console.log("사용할 수 있는 아이디입니다.");
            this.emailCheck = true;
            // Vue.nextTick(callback)
          }

          // 사용 불가능
          if (resp.data.statusCode === 200) {
            console.log("중복된 아이디입니다.");
            this.emailCheck = false;
            this.joinInfo.email = "";
          }
        });
        // 스피너 표시 초기화
        this.emailLoading = false;
      }, 2000);
    },
    validNickname() {
      // 공백 값인 경우 제외
      if (this.joinInfo.nickName === "") {
        return;
      }

      // 값을 입력하기 시작하면, 이전에 표시된 경고 또는 승인문구를 제거한다.
      this.nicknameCheck = "";
      this.nicknameLoading = true;

      // Timer값이 null이 아니라면, null로 만들어준다.
      if (this.timer2) {
        clearTimeout(this.timer2);
      }

      // axios 요청 보낸다.
      this.timer2 = setTimeout(() => {
        axios({
          url: process.env.VUE_APP_API_URL + "/api/meet/user/validNickname",
          method: "POST",
          data: {
            nickname: this.joinInfo.nickName,
          },
        }).then((resp) => {
          // 사용 가능
          if (resp.data.statusCode === 404) {
            console.log("사용할 수 있는 닉네임입니다.");
            this.nicknameCheck = true;
          }

          // 사용 불가능
          if (resp.data.statusCode === 200) {
            console.log("중복된 닉네임입니다.");
            this.nicknameCheck = false;
            this.nickName = "";
          }
        });
        // 스피너 표시 초기화
        this.nicknameLoading = false;
      }, 2000);
    },
  },
};
</script>

<style lang="scss" scoped>
.hobby {
  border: 1px solid lightgrey;
  background-color: white;
  display: inline-block;
  margin: 5px;
  padding: 10px;
  border-radius: 10px;
  -webkit-transition: 500ms;
}
.hobby:hover {
  background-color: #ff9292;
  cursor: pointer;
  -webkit-transition: 500ms;
}
.selected {
  background-color: #ff9292;
  border: 1px solid #ff9292;
}
.myProfile {
  margin: auto;
  margin-bottom: 15px;
}
form > * {
  margin: auto;
  margin-bottom: 20px;
}
.input * {
  margin-bottom: 20px;
}
</style>
