import firebase_admin
from firebase_admin import credentials, storage, auth, db
from PIL import Image


def imageDownload(building_data, map_data):
    building_list = []
    bucket = storage.bucket()

    for item in building_data:
        building_name = building_data[item]['buildingName']
        building_list.append(building_name)
        building_id = map_data[item]['id']
        blob = bucket.get_blob("image" + str(building_id) + ".png")
        print(blob)
        if blob == None:
            blob = bucket.get_blob("image" + building_id + ".jpg")
            blob.download_to_filename('downloadImage/id'+ str(building_id) + ".jpg")
            continue
        blob.download_to_filename('downloadImage/id'+ str(building_id) + ".png")  

if __name__ == '__main__':

    cred = credentials.Certificate("nagaja-3bb34-firebase-adminsdk-9mgkv-0030c98f81.json")
    print(cred)

    firebase_admin.initialize_app(cred, {
        'databaseURL': "https://nagaja-3bb34-default-rtdb.asia-southeast1.firebasedatabase.app/",
        'storageBucket': "nagaja-3bb34.appspot.com"
                                })

    # 데이터베이스의 특정 경로 참조
    building_ref = db.reference("building")
    map_ref = db.reference("map")
    building_data = building_ref.get()
    map_data = map_ref.get()

    imageDownload(building_data, map_data)
