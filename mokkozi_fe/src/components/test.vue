<template>
  <div class="mt-100 layout-column align-items-center justify-content-center">
    <div class="timer-value" data-testid="timer-value">{{ time }}</div>
    <button class="large"
            @click="stopTimer"
            data-testid="stop-button">
      Stop Timer
    </button>
  </div>
</template>

<script>
export default {
  name: "Timer",
  props: {
    initial: {
     type: Number,
    }
  },
  data: () => ({
    timer: '',
    localTime: '',
  }),
  mounted () {
    // 템플릿의 렌더링 이후에 실행된다.
    this.start()
  },
  computed: {
    time: {
      get () {
        return this.initial
      },
      set (newVal) {

      }
    }
  },
  methods: {
    start () {
      this.timer = setTimeout(function tick () {
        // 숫자가 0인 경우 타이머 정지
        if (this.time === 0) {
          clearTimeout(this.timer)
        }
        else {
          this.time--
        }
        this.timer = setTimeout(tick, 1000)
      }, 1000)
    },
    stopTimer() {
      clearTimeout(this.timer)
    }

  }
}
</script>

<style scoped>
.timer-value {
  font-size: 2em;
  color: var(--heading-color);
  margin-block-start: 0.83em;
  margin-block-end: 0.83em;
  margin-inline-start: 0px;
  margin-inline-end: 0px;
  font-weight: bold;
}
</style>
