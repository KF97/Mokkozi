module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  devServer: {
    disableHostCheck: true,
    https: true,
    host: "0.0.0.0",
    hot: true,
    port: 3000,
    open: true,
    proxy: {
      '/api/v1': {
        target: 'https://k5b303.p.ssafy.io:8443/'
      },
      '/webjars': {
        target: 'https://k5b303.p.ssafy.io:8443/'
      },
      '/group-call': {
        target: 'https://k5b303.p.ssafy.io:8443/'
      },
      '/upload': {
        target: 'https://k5b303.p.ssafy.io:8443/'
      }
    },
    historyApiFallback: true,

  },
  // css: {
  //   requireModuleExtension: false // import 시에 경로에 .module 포함 안해도 된다.
  // },
  lintOnSave: false,
  outputDir: './dist'
}
