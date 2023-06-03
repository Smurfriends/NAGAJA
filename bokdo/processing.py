import cv2
import numpy as np
from skimage.morphology import skeletonize, thin
from matplotlib import pyplot as plt
import json
import os
import random
import math

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

def img_Contrast(img):
    # -----Converting image to LAB Color model-----------------------------------
    lab = cv2.cvtColor(img, cv2.COLOR_BGR2LAB)

    # -----Splitting the LAB image to different channels-------------------------
    l, a, b = cv2.split(lab)

    # -----Applying CLAHE to L-channel-------------------------------------------
    clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(3, 3))
    cl = clahe.apply(l)

    # -----Merge the CLAHE enhanced L-channel with the a and b channel-----------
    limg = cv2.merge((cl, a, b))

    # -----Converting image from LAB Color model to RGB model--------------------
    final = cv2.cvtColor(limg, cv2.COLOR_LAB2BGR)

    return final

def findHall(img):
    outputImg = img.copy()
    onlyContourImg = np.zeros_like(img)

    gray=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    gray=255-gray
    gray=cv2.threshold(gray,4,255,cv2.THRESH_BINARY)[1]
    gray=cv2.blur(gray,(15,1))

    # 구조화 요소 커널, 사각형 (5x5) 생성 ---①
    k = cv2.getStructuringElement(cv2.MORPH_RECT, (12,12))

    # 닫힘 연산 적용 ---③
    closing = cv2.morphologyEx(gray, cv2.MORPH_CLOSE, k)
    
    # 외곽선 검출
    contours,hierarchy = cv2.findContours(closing,cv2.RETR_TREE ,cv2.CHAIN_APPROX_NONE )
    
    for cnt, hier in zip(contours, hierarchy[0]):
        area = cv2.contourArea(cnt)
        if area>150000 and area<500000:
            cv2.drawContours(outputImg,[cnt],0,(255,0,0),cv2.FILLED)
            drawSameLevelContours(outputImg, contours, hierarchy, hier[2])

    for cnt, hier in zip(contours, hierarchy[0]):
        area = cv2.contourArea(cnt)
        if area>50000 and area<400000:
            cv2.drawContours(onlyContourImg,[cnt],0,(255,255,255),cv2.FILLED)
            drawSameLevelContours(onlyContourImg, contours, hierarchy, hier[2])

    onlyContourImg = cv2.cvtColor(onlyContourImg,cv2.COLOR_BGR2GRAY)
    
    _, onlyContourImg = cv2.threshold(onlyContourImg, 100, 255, cv2.THRESH_BINARY)

    for idxY, y in enumerate(onlyContourImg):
        for idxX, x in enumerate(y):
            if x == 255:
                onlyContourImg[idxY][idxX] = 1
    
    return onlyContourImg

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

def removeAroundPoint(image, pointList):
    for x in range(len(pointList)):
            cv2.circle(image,(pointList[x][0],pointList[x][1]),10,(0),-1)
            
    #display result
    cv2.imshow('Result',image)
    cv2.waitKey(0)    
    
    return image

def findContours(image):
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
    print("endPoints ", endPoints)
    return endPoints

def findConnectivity(endPoints, junctions):
    connections = []

    for point in endPoints:
        print("point: ", point)
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
            
        # end_point와 가장 가까운 junction을 찾는다.
        end_point = point[1]
        print("end_point: ", end_point)
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
        
    print('connections', connections)

        
    connections_index = []
    dist = []
    for start, end in connections:
        start_node = junctions.index([start[0], start[1]])
        end_node = junctions.index([end[0], end[1]])

        dist.append(euclidean_distance(start,end))
        connections_index.append((start_node, end_node))

    return connections_index, dist

def distanceTable(connections, dists):
    table = np.full((9, 9), 100000)
    np.fill_diagonal(table, 0)
    for connection, dist in zip(connections, dists):
        table[connection[0]][connection[1]] = dist
        table[connection[1]][connection[0]] = dist
    print(table)
    

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

def dumpInJsonFile(list, fileName):
    
    jsonString = json.dumps(list, cls = NpEncoder)
    

    file_path = "information/" + fileName + ".json"
    with open(file_path, 'w', encoding='utf-8') as file:
        json.dump(jsonString, file)
        
    return jsonString

if __name__ == "__main__":
    img_list = os.listdir("downloadImage/")

    print(img_list)
    for img_name in img_list:
        img = cv2.imread("downloadImage/"+img_name)

        print(img.shape)
        # img=cv2.resize(img,(1200,720))
        # img = cv2.rotate(img, cv2.ROTATE_90_CLOCKWISE)

        # 복도 부분을 찾음.
        hallImg = findHall(img)
        centerLineImg = findCenterLine(hallImg)
        junctions = findJunction(img, centerLineImg)
        finalJunction = deleteDuplicateJunctions(centerLineImg.copy(), junctions)
        deleteDuplicateJunctions(img.copy(), junctions)
        removeAroundPointImg = removeAroundPoint(centerLineImg.copy(), finalJunction)
        endPoints = findContours(removeAroundPointImg.copy())
        
        connections, dists = findConnectivity(endPoints, finalJunction)
        distanceTable(connections, dists)
        jsonFile = dumpInJsonFile(finalJunction, img_name+".junction")
        # jsonFile2 = dumpInJsonFile(arr, "id1.png.connectivity")
        print(jsonFile)
