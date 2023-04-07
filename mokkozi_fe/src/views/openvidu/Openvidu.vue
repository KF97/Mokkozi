<template>
  <!-- <v-container fluid style="height: 800px; width: 600px; padding: 0px"> -->
    <div id="main-container">
      <div id="join" v-if="!session">
        <v-menu offset-y style="float:right">
          <template v-slot:activator="{ on, attrs }">
            <p style="float: right"><v-btn color="primary" v-bind="attrs" v-on="on" @click="following">초대</v-btn></p>
          </template>
          <v-list v-for="following in followings" :key="following.id" style="padding: 4px 0px">
            <v-list-item style="cursor: pointer">
              <v-btn color="#ffb4b4">{{ following.nickname }}</v-btn>
            </v-list-item>
          </v-list>
        </v-menu>
        <div id="img-div" style="text-align:center"><img src="@/assets/images/main.png" width="500px" height="500px"/></div>
        <div id="join-dialog" class="jumbotron vertical-center">
          <h1>새로운 만남을 경험하세요!</h1>
          <div class="form-group">
            <p class="text-center" style="margin-top: 1rem">
              <v-btn depressed color="primary" @click="joinSession()">입장</v-btn>
            </p>
          </div>
        </div>
      </div>
      <div v-if="session" style="500px; padding: 40px 0px 0px 0px">
        <h2 id="session-title" style="margin-top:10px">{{ mySessionId }} {{ myUserName }}님의 방</h2>
        <div id="main-video" style="position: relative;">
          <user-video :stream-manager="mainStreamManager" style="width: 680px; height: 510px"/>
          <div class="box-div">
            <user-video v-for="sub in subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub" @click.native="updateMainVideoStreamManager(sub)"/>
          </div>
          <v-icon v-if="videoState" class="video-icon" right dark @click="videoOnOff">fas fa-video</v-icon>
          <v-icon v-else class="video-icon" right dark @click="videoOnOff">fas fa-video-slash</v-icon>
          <v-icon v-if="audioState" class="audio-icon" right dark @click="audioOnOff">fas fa-microphone</v-icon>
          <v-icon v-else class="audio-icon" right dark @click="audioOnOff">fas fa-microphone-slash</v-icon>
          <!-- <v-icon class="exit-icon" right dark @click="leaveSession">fas fa-external-link-alt</v-icon> -->
          <v-icon class="exit-icon" right dark @click="leaveSession">fas fa-door-open</v-icon>
        </div>
        <div id="chat-div" style="height:200px; overflow:scroll;">
          <v-expansion-panels style="border: solid; border-color: #dcdcdc;">
          <v-expansion-panel>
            <v-expansion-panel-header class="font-weight-bold" style="font-size: 20px; height: 60px" @click="CountMessage">
              채팅
              <template v-slot:actions>
                <v-badge
                  depressed
                  color="primary"
                  :content="messageLength"
                >
                  <!-- <v-icon color="#FFB4B4">
                    fas fa-comment
                  </v-icon> -->
                </v-badge>
              </template>
            </v-expansion-panel-header>
            <v-divider></v-divider>
            <v-expansion-panel-content style="margin-top: 10px;" v-for="(message, i) in messages" :key="i">
              <div v-if="message.from == myUserName" class="my-message">
                <!-- <span class="font-weight-bold" style="margin: 0px 3px; word-break: keep-all">{{ message.from }}</span> -->
                <span class="my-message-content">{{ message.content }}</span>
                <v-avatar color="brown" size="36">
                  <img alt="Avatar" :src="myProfileImg" />
                  <!-- <span class="white--text text-h5">{{ profileImg }}</span> -->
                </v-avatar>
              </div>
              <div v-else class="your-message">
                <v-avatar color="pink" size="36">
                  <img alt="Avatar" :src="yourProfileImg" />
                  <!-- <span class="white--text text-h5">{{ profileImg }}</span> -->
                </v-avatar>
                <!-- <span class="font-weight-bold" style="margin: 0px 3px; word-break: keep-all">{{ message.from }}</span> -->
                <span class="your-message-content">{{ message.content }}</span>
              </div>
            </v-expansion-panel-content>
            <v-expansion-panel-content>
              <v-divider></v-divider>
              <div style="margin-top:10px">
                <input style="width: 586px;" class="input-box" v-model="message" type="text" placeholder="내용을 입력해주세요.." @keydown.enter="sendMessage">
                <!-- <v-icon style="width: 24px">fas fa-location-arrow</v-icon> -->
                <v-btn style="min-width: 40px; height:24px; padding: 0px 5px; color: white;" color="#FFB4B4" @click="sendMessage">전송</v-btn>
              </div>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
        </div>
      </div>
    </div>
  <!-- </v-container> -->
</template>

<script>
import axios from 'axios'
import { OpenVidu } from 'openvidu-browser'
import UserVideo from '../../components/UserVideo'
// import * as faceapi from 'face-api.js'

axios.defaults.headers.post['Content-Type'] = 'application/json'
const OPENVIDU_SERVER_URL = 'https://k5b303.p.ssafy.io:8447'
const OPENVIDU_SERVER_SECRET = 'mokkozi_secret'

export default {
  name: 'Meeting',
  components: {
    UserVideo
  },
  data () {
    return {
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      mySessionId: 'MOKKOZI2',
      myUserName: 'Participant' + Math.floor(Math.random() * 100),
      videoState: true,
      audioState: true,
      isSpeak: false,
      message: '',
      messages: [],
      messageLength: '0',
      chatOpen: false,
      followings: [], // 팔로잉 목록
      myProfileImg: '',
      yourProfileImg: ''
    }
  },
  created () {
    if (!this.$store.state.jwt) {
      alert("로그인이 필요합니다😀")
      this.$router.push({ name: 'Login' })
    }
  },
  mounted () {
    this.myUserName = this.$store.state.user.nickname
    // this.mySessionId = 'room' + Math.floor(Math.random() * 100)
  },
  methods: {
    // 유저 프로필 가져오기
    getUserProfile(nickName) {
      console.log("유저 프로필 요청이 들어오는지?!");
      axios({
        url: process.env.VUE_APP_API_URL + `/api/meet/user/getUserByNickname?nickName=${nickName}`,
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
      }).then((resp) => {
        console.log("유저 프로필 성공 : ", resp)
        console.log(resp.data.user.profile)
        if (resp.data.user.nickname === this.myUserName) {
          this.myProfileImg = resp.data.user.profile
        } else {
          this.yourProfileImg = resp.data.user.profile
        }
      }).catch((err) => {
        console.log("유저 프로필 실패 : ", err)
      })
    },
    // 팔로잉 목록 가져오기
    following() {
      console.log("팔로잉 목록");
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/following",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
      }).then((resp) => {
        console.log("팔로잉 목록 : ", resp)
        this.followings = resp.data.followers
        console.log("팔로잉 객체 " + resp.data.followers)
        this.$store.dispatch("setFollowing", resp.data.following)
      })
    },
    videoOnOff () {
      this.videoState = !this.videoState
      this.publisher.publishVideo(this.videoState)
    },
    audioOnOff () {
      this.audioState = !this.audioState
      this.publisher.publishAudio(this.audioState)
    },
    joinSession () {
      // --- Get an OpenVidu object ---
      this.OV = new OpenVidu()
      // --- Init a session ---
      this.session = this.OV.initSession()
      // --- Specify the actions when events take place in the session ---

      // 메세지를 받는 부분(채팅)
      this.session.on('signal:chat', (event) => {
        const eventData = JSON.parse(event.data)
        this.messages.push(eventData)
        console.log('메세지 내용 출력', this.messages)
        // console.log('이벤트 데이터 출력!!', eventData.from)
        this.getUserProfile(eventData.from)
        if (!this.chatOpen) {
          this.message = ''
          this.messageLength++
        }
        // 스크롤이 자동으로 맞춰서 내려가게 한다
        setTimeout(() => {
          const chatDiv = document.getElementById('chat-div')
          chatDiv.scrollTo({
            top: chatDiv.scrollHeight,
            behavior: 'smooth'
          })
        }, 50)
      })

      // On every new Stream received...
      this.session.on('streamCreated', ({ stream }) => {
        const subscriber = this.session.subscribe(stream)
        console.log('subscriber객체 들어있는 것', subscriber) // 삭제 예정
        console.log('video 크기 변경', subscriber.stream.streamManager) // 삭제 예정
        this.subscribers.push(subscriber)
      })
      // On every Stream destroyed...
      this.session.on('streamDestroyed', ({ stream }) => {
        const index = this.subscribers.indexOf(stream.streamManager, 0)
        if (index >= 0) {
          this.subscribers.splice(index, 1)
        }
      })
      // On every asynchronous exception...
      this.session.on('exception', ({ exception }) => {
        console.warn('예외 처리', exception)
      })
      // --- Connect to the session with a valid user token ---
      // 'getToken' method is simulating what your server-side should do.
      // 'token' parameter should be retrieved and returned by your own backend
      this.getToken(this.mySessionId).then(token => {
        console.log('getToken 후에 받아온 token값', token)
        this.session.connect(token, { clientData: this.myUserName })
          .then(() => {
            // --- Get your own camera stream with the desired properties ---
            const publisher = this.OV.initPublisher(undefined, {
              audioSource: undefined, // The source of audio. If undefined default microphone
              videoSource: undefined, // The source of video. If undefined default webcam
              publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
              publishVideo: true, // Whether you want to start publishing with your video enabled or not
              resolution: '600x400', // The resolution of your video
              frameRate: 30, // The frame rate of your video
              insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
              mirror: false, // Whether to mirror your local video or not
            })
            this.mainStreamManager = publisher
            this.publisher = publisher
            // --- Publish your stream ---
            this.session.publish(this.publisher)
            console.log('session.connect 성공 후 publisher', publisher) // 삭제 예정
            this.$store.dispatch("setMeeting", true)
          })
          .catch(error => {
            console.log('session.connect 실패.. There was an error connecting to the session:', error.code, error.message)
          })
      })
      window.addEventListener('beforeunload', this.leaveSession)
    },
    leaveSession () {
      // --- Leave the session by calling 'disconnect' method over the Session object ---
      if (this.session) this.session.disconnect()
      this.session = undefined
      this.mainStreamManager = undefined
      this.publisher = undefined
      this.subscribers = []
      this.messages = []
      this.OV = undefined
      this.$store.dispatch("setMeeting", false)
      window.removeEventListener('beforeunload', this.leaveSession)
    },
    updateMainVideoStreamManager (stream) {
      if (this.mainStreamManager === stream) return
      this.subscribers.splice(0, 1, this.mainStreamManager)
      this.mainStreamManager = stream
    },
    /**
     * --------------------------
     * SERVER-SIDE RESPONSIBILITY
     * --------------------------
     * These methods retrieve the mandatory user token from OpenVidu Server.
     * This behavior MUST BE IN YOUR SERVER-SIDE IN PRODUCTION (by using
     * the API REST, openvidu-java-client or openvidu-node-client):
     *   1) Initialize a Session in OpenVidu Server (POST /openvidu/api/sessions)
     *   2) Create a Connection in OpenVidu Server (POST /openvidu/api/sessions/<SESSION_ID>/connection)
     *   3) The Connection.token must be consumed in Session.connect() method
     */
    getToken (mySessionId) {
      console.log('getToken함수 실행할 때 들어가는 sessionId: ', mySessionId)
      return this.createSession(mySessionId).then(sessionId => this.createToken(sessionId))
    },
    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-session
    createSession (sessionId) {
      console.log('createSession함수 실행할 때 들어가는 sessionId: ', sessionId) // 삭제 예정
      return new Promise((resolve, reject) => {
        axios
          .post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`,
            JSON.stringify({
              customSessionId: sessionId,
            }),
            {
              auth: {
                username: 'OPENVIDUAPP',
                password: OPENVIDU_SERVER_SECRET
              }
            })
          .then(response => response.data)
          .then(data => resolve(data.id))
          .catch(error => {
            console.log(error)
            if (error.response.status === 409) {
              resolve(sessionId)
            } else {
              console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`)
              if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
                location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`)
              }
              reject(error.response)
            }
          })
      })
    },
    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-connection
    createToken (sessionId) {
      console.log('createToken함수 실행 시 들어가는 sessionId: ', sessionId) // 삭제 예정
      return new Promise((resolve, reject) => {
        axios
          .post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`, {}, {
            auth: {
              username: 'OPENVIDUAPP',
              password: OPENVIDU_SERVER_SECRET
            }
          })
          .then(response => response.data)
          .then(data => resolve(data.token))
          .catch(error => reject(error.response))
      })
    },
    // 닉네임으로 회원 프로필 가져오는 부분 -> 아직 백엔드가 안되어있음
    getuser(name) {
      axios({
        url: process.env.VUE_APP_API_URL + "/api/meet/user/getuser",
        method: "GET",
        headers: {
          Authorization: "Bearer " + this.$store.state.jwt,
        },
        params: {
          nickname: name,
        },
      }).then((resp) => {
        console.log("회원정보 확인: ", resp)
        this.profileImg = resp.data.profile
      })
    },

    // 메세지 보내는 함수(채팅)
    sendMessage () {
      if (this.message.trim() !== "") { // 공백만 있을 때 입력 불가능
        const messageData = {
          content: this.message,
          from: this.myUserName
        }
        // this.getuser(messageData.from) 아직 구현이 안되어 있음
        this.message = ''
        this.session.signal({
          data: JSON.stringify(messageData), // Any string (optional)
          to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
          type: 'chat' // The type of message (optional)
        })
      }
    },
    CountMessage () {
      this.chatOpen = !this.chatOpen
      if (this.chatOpen === true) {
        this.messageLength = '0'
      }
      console.log(this.chatOpen)
    },
  }
}
</script>

<style scoped>
   .main-container {
     display: flex;
     flex-direction: row;
     justify-content: center;
     align-items: center;
   }
  .meeting-container {
    overflow-y: scroll;
  }
  .meeting-container::-webkit-scrollbar {
    display: none;
  }
  .box-div {
    position: absolute;
    height: 136px;
    width: 204px;
    bottom: 30px;
    right: 0px;
  }
  .video-icon {
    position: absolute;
    width: 30px;
    bottom: 120px;
    left: 0px;
  }
  .audio-icon {
    position: absolute;
    width: 30px;
    bottom: 80px;
    left: 0px;
  }
  .exit-icon {
    position: absolute;
    width: 30px;
    bottom: 40px;
    left: 0px;
  }
  #chat-div::-webkit-scrollbar {
    display: none;
  }
  .my-message {
    margin-left: 300px;
    display: flex;
    flex-direction: row;
    justify-content: end;
    /* align-items: center; */
  }
  .your-message {
    width: 300px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    /* align-items: center; */
  }
  .my-message-content {
    border: solid 1px;
    border-radius: 5px;
    border-color: #FFB4B4;
    background-color: #FFB4B4;
    margin-right: 5px;
    color: white;
    padding: 5px;
    font-weight: bold;
  }
  .your-message-content {
    border: solid 1px;
    border-radius: 5px;
    border-color: #FFB4B4;
    background-color: #FFB4B4;
    margin-left: 5px;
    color: white;
    padding: 5px;
    font-weight: bold;
  }
  .input-box {
    outline-style: none;
  }
</style>
