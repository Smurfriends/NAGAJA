initialize_app(cred, {
#     'storageBucket': # gs://가 없는 경로 - 붙이면 에러남
# })
# bucket = storage.bucket()

# app = FastAPI()

# @app.post("/image")
# def post_image():
#     # 이미지 생성
#     img: Image.Image = text_to_image(content)
#     # 이미지 저장
#     blob = bucket.blob('image/test.png') # 이미지 경로 설정
#     bs = io.BytesIO()
#     img.save(bs, "png")
#     blob.upload_from_string(bs.getvalue(), content_type="image/png") # 이미지 저장

#     return True
