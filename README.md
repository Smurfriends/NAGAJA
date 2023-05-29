# NAGAJA
Mobile Programming Team Project


[bokdo 폴더 밑에 있는 폴더 및 파일 설명]
(1) 폴더
1) data
: 얘는 도면 분류 모델 학습시키는 데이터들 들어있는 폴더

2) downloadImage 
: firebaseImageDownload.py 실행했을 때 firebase 스토리지에서 가져온 이미지들 저장하는 폴더

3) information 
: processing.py 실행했을 때, 이미지들의 좌표, 인접 행렬 등을 저장하는 폴더


(2) 파일
1) dataset_preprocessing.py
: 도면 분류 모델을 만들기 전, 데이터셋을 구축하고 전처리하는 코드

2) buildRecognitionFloorPlanModel.py
: 도면인지 아닌지 분류하는 binary classification model을 만드는 코드
만들어진 모델은 model_imagesize_256.pth 파일에 저장됨.

3) isItFloorPlan.py
만들어진 모델을 api로 사용할 수 있는 flask 서버를 여는 코드

4) firebaseImageDownload.py
: firebase 스토리지에서 이미지를 가져오는 코드. 
이미지 이름은 데이터베이스에 저장된 id를 가져와 "id"+id+".png" 형식으로 저장됨. (downloadImage 에 저장)
 
5) processing.py
: 도면을 인식하는 핵심 코드
도면을 전처리한 후, 도면에서 복도의 노드를 찾고, 노드끼리의 연결성을 찾음.
찾은 결과를 json파일에 저장함.
각 노드는 이미지이름+"junction.json" 파일에, 연결성은 이미지이름+"connectivity.json" 파일에 저장함.

(3) 사용법
1) 모델을 만들고 사용하고 싶다.
	- step 1) python dataset_preprocessing.py(학습 데이터 전처리)
	- step 2) python buildRecognitionFloorPlanModel.py(모델 학습 및 저장)
	- step 3) python isItFloorPlan.py을 해서 서버를 연다.(api 사용할 준비)
	- step 4) test.py에서 이미지만 테스트해보고 싶은 것으로 수정해서 python test.py한다. (api사용해서 예측값 얻음)

2) 파이어베이스에 올라온 도면들을 인식해 노드와 연결성 정보를 업데이트 하고 싶다.
	- step 1) python firebaseImageDownload.py(이미 설명함)
	- step 2) python processing.py(이미 설명함)
	- step 3) python firebaseInfoUpdate.py(이미 설명함)
