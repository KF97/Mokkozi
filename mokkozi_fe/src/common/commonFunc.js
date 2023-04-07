// image 리스트를 받아 local URL을 생성합니다. (미리보기 기능 위해)
export function makeLocalURL(files) {
  console.log("commonFunc.makeLocalURL 3 : 함수 실행")
  let result = []

  for (let i=0; i < files.length; i++) {
    const previewURL = URL.createObjectURL(files[i])
    result.push(previewURL)
  }
  return result
}

// 단일 파일 이름 검사
export function checkMyImageLetter(myImages) {
  console.log("파일 이름 유효성 검사합니다. : ", myImages.name)

  if (myImages.name.includes("(") || myImages.name.includes(")") ||
    myImages.name.includes("\\") || myImages.name.includes("/") ||
    myImages.name.includes(":") || myImages.name.includes(";") ||
    myImages.name.includes("*") || myImages.name.includes("?") ||
    myImages.name.includes('"') || myImages.name.includes("'") ||
    myImages.name.includes("<") || myImages.name.includes(">") ||
    myImages.name.includes("|") || myImages.name.includes(" ")) {
    alert("파일 이름에 사용할 수 없는 특수문자가 포함되어 있습니다.")

    return true
  }
  return false

}


// 여러 파일 이름 검사
export function checkMyImagesLetter(myImages) {
  console.log("파일 이름 유효성 검사합니다. : ", myImages.name)

  for (let i = 0; i < myImages.length; i++) {
    if (myImages[i].name.includes("(") || myImages[i].name.includes(")") ||
      myImages[i].name.includes("\\") || myImages[i].name.includes("/") ||
      myImages[i].name.includes(":") || myImages[i].name.includes(";") ||
      myImages[i].name.includes("*") || myImages[i].name.includes("?") ||
      myImages[i].name.includes('"') || myImages[i].name.includes("'") ||
      myImages[i].name.includes("<") || myImages[i].name.includes(">") ||
      myImages[i].name.includes("|") || myImages[i].name.includes(" ")) {
      alert("파일 이름에 사용할 수 없는 특수문자가 포함되어 있습니다.")
      return true
    }
    return false
  }
}
