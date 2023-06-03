import firebase_admin
from firebase_admin import credentials, storage, auth, db
from PIL import Image
import json
import os
import ast

def infoUpdate(building_ref, map_ref, building_data, map_data):
    for building_name in building_data:
        building_id = map_data[building_name]['id']

        print(building_name + " is updating...")

        junctions = readJson("information/id"+str(building_id)+".png.junction.json")
        connectivities = readJson("information/id"+str(building_id)+".png.png.connectivity.json")
        
        # print("junctions: ", junctions)
        
        if junctions == "[]" or junctions == None:
            continue

        
        # 문자열을 배열로 변환
        junctions = ast.literal_eval(junctions)
        connectivities = ast.literal_eval(connectivities)

        x = []
        y = []

        for junction in junctions:
            # print("junction: ", junction)
            x.append(junction[0])
            y.append(junction[1])

        node_num = len(x)

        x = ', '.join(str(item) for item in x)
        y = ', '.join(str(item) for item in y)

        

        connectivity_str = ""
        for connectivity in connectivities:
            for item in connectivity:
                connectivity_str = connectivity_str + str(item) + ", "
        connectivity_str = connectivity_str.rstrip(', ')
        # print(connectivity_str)
        # 데이터 수정
        map_ref.child(building_name).update({'node': connectivity_str}) 
        # map_ref.child(building_name).child(node).update({'name': 'John Doe'})

        map_ref.child(building_name).update({'nodeNum': node_num}) 
        map_ref.child(building_name).update({'x': x})
        map_ref.child(building_name).update({'y': y})

def readJson(json_file_path):
    # 파일이 존재하지 않을 때 예외 처리
    if not os.path.exists(json_file_path):
        print(f"Error: File '{json_file_path}' does not exist.")
        return None
    
    try:
        # JSON 파일 열기
        with open(json_file_path, 'r') as json_file:
            # JSON 데이터 로드
            data = json.load(json_file)
        return data
    except json.JSONDecodeError as e:
        print(f"Error: Failed to parse JSON file '{json_file_path}': {str(e)}")
        return None

if __name__ == '__main__':

    cred = credentials.Certificate("nagaja-3bb34-firebase-adminsdk-9mgkv-0030c98f81.json")
    # print(cred)

    firebase_admin.initialize_app(cred, {
        'databaseURL': "https://nagaja-3bb34-default-rtdb.asia-southeast1.firebasedatabase.app/",
        'storageBucket': "nagaja-3bb34.appspot.com"
                                })

    # 데이터베이스의 특정 경로 참조
    building_ref = db.reference("building")
    map_ref = db.reference("map")
    building_data = building_ref.get()
    map_data = map_ref.get()

    infoUpdate(building_ref, map_ref, building_data, map_data)

    print("Information updated successfully.")