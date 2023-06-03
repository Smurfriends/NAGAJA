import cv2
import numpy as np
from skimage.morphology import skeletonize, thin
from matplotlib import pyplot as plt
import json
import os
import random
import math
from tensorflow import keras
import requests
from PIL import Image
import io

# 같은 계층에 있는 컨투어들을 지우는 함수
def drawSameLevelContours(img, contours, hierarchy, contourIndex):
    if contourIndex == -1:
        return
    
    cv2.drawContours(img,[contours[contourIndex]],0,(0,0,0),cv2.FILLED)
    
    nextIndex = hierarchy[0][contourIndex][0]
    while nextIndex != -1:
        cv2.drawContours(img,[contours[nextIndex]],0,(0,0,0),cv2.FILLED)
        nextIndex = hierarchy[0][nextIndex][0]

    previousIndex = hierarchy[0][contourIndex][0]
    while previousIndex != -1:
        cv2.drawContours(img,[contours[previousIndex]],0,(0,0,0),cv2.FILLED)
        previousIndex = hierarchy[0][previousIndex][0]

# 복도를 검출하는 함수
def findHall(img):
    outputImg = img.copy()
    onlyContourImg = np.zeros_like(img)

    gray=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    gray=255-gray
    gray=cv2.threshold(gray,90,255,cv2.THRESH_BINARY)[1]
    gray=cv2.blur(gray,(7,1))

    k = cv2.getStructuringElement(cv2.MORPH_RECT, (12,12))

    # 닫힘 연산 적용 ---③
    closing = cv2.morphologyEx(gray, cv2.MORPH_CLOSE, k)
    cv2.imshow('closing', closing)
    cv2.waitKey(0)

    # 외곽선 검출
    contours,hierarchy = cv2.findContours(closing,cv2.RETR_TREE ,cv2.CHAIN_APPROX_NONE )
    # Contour 검출
    contours2, _ = cv2.findContours(closing, cv2.RETR_LIST, cv2.CHAIN_APPROX_NONE)

    # 전체 Contour의 넓이 계산
    total_area = 0
    image_result = outputImg.copy()

    for cnt, hier in zip(contours, hierarchy[0]):
        if hier[3] == -1:
            continue
        area = cv2.contourArea(cnt)
        if area>50000 and area<400000:
            cv2.drawContours(onlyContourImg,[cnt],0,(255,255,255),cv2.FILLED)
            drawSameLevelContours(onlyContourImg, contours, hierarchy, hier[2])

    onlyContourImg = cv2.cvtColor(onlyContourImg,cv2.COLOR_BGR2GRAY)
    
    _, onlyContourImg = cv2.threshold(onlyContourImg, 100, 255, cv2.THRESH_BINARY)

    # 이건 이 다음단계에서 이미지 내에 0과 1만 있어야 해서 255를 1로 강제 변환시켜주는 부분
    for idxY, y in enumerate(onlyContourImg):
        for idxX, x in enumerate(y):
            if x == 255:
                onlyContourImg[idxY][idxX] = 1
    cv2.imshow('contourimg', onlyContourImg)
    cv2.waitKey(0)
    return onlyContourImg

# 복도 중심선을 검출하는 함수
def findCenterLine(image):
   
    skeleton = skeletonize(image)
    thinned = thin(image)
    thinned_partial = thin(image, max_num_iter=25)

    fig, axes = plt.subplots(2, 2, figsize=(8, 8), sharex=True, sharey=True)
    ax = axes.ravel()

    ax[0].imshow(image, cmap=plt.cm.gray)
    ax[0].set_title('original')
    ax[0].axis('off')

    ax[1].imshow(skeleton, cmap=plt.cm.gray)
    ax[1].set_title('skeleton')
    ax[1].axis('off')

    ax[2].imshow(thinned, cmap=plt.cm.gray)
    ax[2].set_title('thinned')
    ax[2].axis('off')

    ax[3].imshow(thinned_partial, cmap=plt.cm.gray)
    ax[3].set_title('partially thinned')
    ax[3].axis('off')

    fig.tight_layout()
    # plt.show()
    thinned = binaryToGray(thinned)

    return thinned

# 바이너리 이미지를 그레이 스케일로 변환하는 함수
def binaryToGray(image):
    output_image = np.zeros_like(image, dtype=np.uint8)  # Create output image

    for idxY, y in enumerate(image):
        for idxX, x in enumerate(y):
            if x == True:
                output_image[idxY][idxX] = 255
            else:
                output_image[idxY][idxX] = 0

    image = output_image
    return image

# 복도의 꺾이는 부분 좌표를 검출하는 함수
def findJunction(originalImage, image):
    # make a copy to display result
    im_or = image.copy()
    # convert image to larger datatyoe
    image.astype(np.int32)
    # create kernel 
    kernel = np.ones((7,7))
    kernel[2:5,2:5] = 0
    #apply kernel
    res = cv2.filter2D(image,3,kernel)
    # filter results
    loc = np.where(res > 2000)
    #draw circles on found locations
    for x in range(len(loc[0])):
            cv2.circle(im_or,(loc[1][x],loc[0][x]),10,(127),5)
    
    # display result
    cv2.imshow('Result',im_or)
    cv2.waitKey(0)    
    
    return loc

# 찾은 점들 중 가까이 있는 것들은 하나만 남기고 지우는 함수
def deleteDuplicateJunctions(image, loc):
    pointList = []
    for x, y in zip(loc[1], loc[0]):
        pointList.append([x,y])

    # x값을 기준으로 sort
    # x값이 20보다 가까이 있다면,
        # y값이 20보다 가까이 있는 경우
    pointList.sort()
    pop_list = []
    for idx, point in enumerate(pointList):
        if idx + 1 > len(pointList) - 1:
            break
        if abs(point[0] - pointList[idx + 1][0]) < 20:
            if abs(point[1] - pointList[idx + 1][1]) < 20:
                pop_list.append(idx+1)
    pop_list.reverse()
    for pop_point in pop_list:
        pointList.pop(pop_point)
    
    # y값을 기준으로 sort
    # y값이 20보다 가까이 있다면,
        # x값이 20보다 가까이 있는 경우
    pointList.sort(key = lambda x: (x[1], x[0]))
    pop_list = []
    for idx, point in enumerate(pointList):
        if idx + 1 > len(pointList) - 1:
            break
        if abs(point[1] - pointList[idx + 1][1]) < 20:
            if abs(point[0] - pointList[idx + 1][0]) < 20:
                pop_list.append(idx+1)
    pop_list.reverse()
    for pop_point in pop_list:
        pointList.pop(pop_point)

    #draw circles on found locations
    for x in range(len(pointList)):
            cv2.circle(image,(pointList[x][0],pointList[x][1]),10,(127),5)
    
    #display result
    cv2.imshow('Result',image)
    cv2.waitKey(0)    

    return pointList

# 점들 주변 픽셀을 지우는 함수
def removeAroundPoint(image, pointList):
    for x in range(len(pointList)):
            cv2.circle(image,(pointList[x][0],pointList[x][1]),5,(0),-1)
            
    #display result
    cv2.imshow('Result',image)
    cv2.waitKey(0)    
    
    return image

# 선들의 양끝점을 구하는 함수
def findEndpoints(image):
    outputImg = image.copy()
    outputImg = cv2.cvtColor(outputImg, cv2.COLOR_GRAY2BGR)

    # 외곽선 검출
    contours, _ = cv2.findContours(image,cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    

    endPoints = []
    for cnt in contours:
        random_r = random.randint(0, 255)
        random_g = random.randint(0, 255)
        random_b = random.randint(0, 255)
        cv2.drawContours(outputImg,[cnt],0,(random_r,random_g,random_b),cv2.FILLED)
        
        onlyContourImg = np.zeros_like(img)
        
        cv2.drawContours(onlyContourImg,[cnt],-1,(255,255,255),cv2.FILLED)
        
        onlyContourImg = cv2.cvtColor(onlyContourImg,cv2.COLOR_BGR2GRAY)
        
        _, onlyContourImg = cv2.threshold(onlyContourImg, 100, 255, cv2.THRESH_BINARY)


        # Find row and column locations that are non-zero
        (rows,cols) = np.nonzero(onlyContourImg)

        # Initialize empty list of co-ordinates
        skel_coords = []

        # For each non-zero pixel...
        for (r,c) in zip(rows,cols):

            # Extract an 8-connected neighbourhood
            (col_neigh,row_neigh) = np.meshgrid(np.array([c-1,c,c+1]), np.array([r-1,r,r+1]))

            # Cast to int to index into image
            col_neigh = col_neigh.astype('int')
            row_neigh = row_neigh.astype('int')

            # Convert into a single 1D array and check for non-zero locations
            pix_neighbourhood = onlyContourImg[row_neigh,col_neigh].ravel() != 0
            
            # If the number of non-zero locations equals 2, add this to 
            # our list of co-ordinates
            if np.sum(pix_neighbourhood) == 2:
                skel_coords.append((c,r))
        if len(skel_coords) == 0:
            continue

        start_point = skel_coords[0]
        end_point = skel_coords[1]
        endPoints.append((start_point, end_point))
    
    cv2.imshow('Result',outputImg)
    cv2.waitKey(0)    
    # print("endPoints ", endPoints)
    return endPoints

# junction들끼리의 연결성을 찾는 함수
def findConnectivity(endPoints, junctions):
    connections = []

    for point in endPoints:
        # print("point: ", point)
        start_junction = (-1, -1)
        end_junction = (-1, -1)

        # 먼저 start_point와 가장 가까운 junction을 찾는다.
        start_point = point[0]
        
        best_distance = 1000000
        best_junction = (-1, -1)
        for junction in junctions:
            distance = euclidean_distance(start_point, junction)
            if best_distance > distance:
                best_distance = distance
                best_junction = junction
        if best_distance < 12:
            start_junction = best_junction
            
        # end_point와 가장 가까운 노드를 찾는다.
        end_point = point[1]
        # print("end_point: ", end_point)
        best_distance = 1000000
        best_junction = (-1, -1)
        for junction in junctions:
            distance = euclidean_distance(end_point, junction)
            if best_distance > distance:
                best_distance = distance
                best_junction = junction

        if best_distance < 12:
            end_junction = best_junction

        if start_junction == (-1, -1) or end_junction == (-1, -1):
            continue
        else:
            connections.append((start_junction, end_junction))
        
    # print('connections', connections)

        
    connections_index = []
    dist = []
    for start, end in connections:
        start_node = junctions.index([start[0], start[1]])
        end_node = junctions.index([end[0], end[1]])

        dist.append(euclidean_distance(start,end))
        connections_index.append((start_node, end_node))

    return connections_index, dist

# 인접행렬 구하는 함수
def distanceTable(nodeNum, connections, dists):
    table = np.full((nodeNum, nodeNum), 100000)
    np.fill_diagonal(table, 0)
    for connection, dist in zip(connections, dists):
        table[connection[0]][connection[1]] = dist
        table[connection[1]][connection[0]] = dist
    
    print(table)
    
    return table
    

def euclidean_distance(point1, point2):
    x1, y1 = point1
    x2, y2 = point2
    distance = math.sqrt((x2 - x1)**2 + (y2 - y1)**2)
    return distance

class NpEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        if isinstance(obj, np.floating):
            return float(obj)
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return super(NpEncoder, self).default(obj)

# 데이터를 json file로 저장
def dumpInJsonFile(list, fileName):
    
    jsonString = json.dumps(list, cls = NpEncoder)
    

    file_path = "information/" + fileName + ".json"
    with open(file_path, 'w', encoding='utf-8') as file:
        json.dump(jsonString, file)
        
    return jsonString

# 도면인지 아닌지 분류하는 모델 사용하는 함수
def validImageCheck(imageName):
    
    # Specify the URL
    url = "http://127.0.0.1:5000/predict"

    # Open the image file in binary mode
    with Image.open("downloadImage/" + imageName) as img:
    # with Image.open("data/train/floor/image15.png") as img:
        
        # Convert the image to RGB format
        rgb_img = img.convert("RGB")

        # Create a temporary file in memory to send the image
        temp_file = io.BytesIO()
        rgb_img.save(temp_file, format="JPEG")

        # Seek to the beginning of the file
        temp_file.seek(0)

        # Create the payload with the image file
        payload = {"image": temp_file}

        # Send the POST request
        response = requests.post(url, files=payload)

        # Print the response content
        # print("Response Content:", response.text)
        output = response.json().get('prediction') # if 1, not floor. elif 0, floor.
        
        if output == 1:
            return False
        elif output == 0:
            return True

if __name__ == "__main__":
    
    # 사용할 이미지를 downloadImage 폴더에 넣으세여
    img_list = os.listdir("downloadImage/")
    print(img_list)

    for img_name in img_list:

        # 이건 도면인지 아닌지 체크하는 CNN모델 쓰는 부분
        # if validImageCheck(img_name) == False:
        #     print("not valid image")            
        #     continue

        img = cv2.imread("downloadImage/"+img_name)

        print(img.shape)
        # img=cv2.resize(img,(1200,720))
        # img = cv2.rotate(img, cv2.ROTATE_90_CLOCKWISE)

        # 복도 부분을 찾음.
        hallImg = findHall(img)
        cv2.imshow('hallImg', hallImg)
        cv2.waitKey(0)

        # 복도의 중심 선을 찾음.(skeletonize해서)
        centerLineImg = findCenterLine(hallImg)
        cv2.imshow('centerLineImg', centerLineImg)
        cv2.waitKey(0)

        # 복도에서 꺾이는 부분의 점의 좌표를 검출
        junctions = findJunction(img, centerLineImg)

        # 중복되는 점들을 제거 -> 최종적으로 복도에 꺾이는 점을 노드로 해서 검출
        finalJunction = deleteDuplicateJunctions(centerLineImg.copy(), junctions)
        deleteDuplicateJunctions(img.copy(), junctions)
        # finalJunctionDivide2 = finalJunction/2
        print("finalJunction: ", finalJunction)
        # 여기서부터는 노드끼리의 연결성을 찾기 위한 코드
        # 복도에 꺾이는 점 주변을 10픽셀정도를 지워버림 
        # -> 그럼 이어져있던 복도 선이 끊어져서 보이겠지?
        removeAroundPointImg = removeAroundPoint(centerLineImg.copy(), finalJunction)
        
        # 각 선의 양 끝점이 있을텐데, 그 끝점의 좌표를 가지고 와. 
        endPoints = findEndpoints(removeAroundPointImg.copy())
        # endPointDivide2 = endPoints/2
        # 그 끝점의 좌표들을 아까 찾은 노드들과 대응시켜(끝점에서 가장 가까운 노드에다가)
        # ex) 끝점이 (144, 299)이면 -> 가장 가까운 노드인 노드1(137, 296)에 대응 
        # 그렇게 해서 "노드1과 노드2는 연결되어있다" 이런식으로 노드의 연결성을 찾아내
        connections, dists = findConnectivity(endPoints, finalJunction)

        # 찾아낸 노드들의 연결성과 거리들을 바탕으로 인접행렬을 만든다.
        table = distanceTable(len(finalJunction), connections, dists)

        # 결과 저장
        # jsonFile에는 노드 정보를 저장
        # jsonFile2에는 노드끼리의 연결성 정보를 저장
        jsonFile = dumpInJsonFile(finalJunction, img_name+".junction")
        jsonFile2 = dumpInJsonFile(table, img_name+".png.connectivity")
