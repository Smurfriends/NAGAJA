from datasets import load_dataset
from PIL import Image
import os
import shutil

def resize_image(image, size):
    return image.resize(size)

def example_usage():
    tiny_imagenet = load_dataset('Maysee/tiny-imagenet', split='train')

    #  매 50번째 데이터 저장
    every_50th_data = []
    for i in range(0, len(tiny_imagenet), 50):
        every_50th_data.append(tiny_imagenet[i])
    
    # 저장된 데이터 출력
    for idx, data in enumerate(every_50th_data):
        print(data)
        
        # 이미지 크기 변경
        data = resize_image(data['image'], (256, 256))

        
        output_path = "not_floor_plan_data\\"
        data.save(output_path + str(idx) + ".jpg")


def copy_images(idx, source_folder, destination_folder):
    # 대상 폴더가 존재하지 않으면 생성
    if not os.path.exists(destination_folder):
        os.makedirs(destination_folder)

    print("folder: ", os.listdir(source_folder))
    # 소스 폴더 내의 이미지 파일들을 대상 폴더로 복사
    for filename in os.listdir(source_folder):
        # 파일의 전체 경로
        source_file = os.path.join(source_folder, filename)
        
        # 이미지 파일인 경우에만 복사
        if filename == "F1_original.png":
             # 이미지 로드
            image = Image.open(source_file)

            # 이미지 사이즈 조절
            resized_image = resize_image(image, (256, 256))

            # 새로운 파일 이름 설정
            new_filename = "image" + str(idx) + ".png"
            destination_file = os.path.join(destination_folder, new_filename)

            # 이미지 저장
            resized_image.save(destination_file)
            print('있음')



def get_files_and_folders(path):
    # 폴더 내의 모든 파일과 폴더의 이름을 저장할 리스트
    files_and_folders = []

    # 폴더 내의 모든 파일과 폴더의 이름을 가져오기
    for item in os.listdir(path):
        # 전체 경로를 포함한 파일\\폴더 이름
        # item_path = os.path.join(path, item)
        files_and_folders.append(item)

    return files_and_folders


def save_floor_plan_image(path, items, image_name, destination_folder):
    print('path: ', path)
    for idx, item in enumerate(items):
        item_files_and_folders = get_files_and_folders(path+"\\"+item)
        print('item: ', item)
        if image_name not in item_files_and_folders:
            print("이미지가 존재하지 않습니다:", path,"\\", item,"\\",image_name)
        else:
            copy_images(idx,path+"\\"+item, destination_folder)




if __name__ == '__main__':
    # floor plan 아닌 이미지 저장
    example_usage()

    # floor plan 이미지 저장
    path = r"floor_image_data\cubicasa5k\high_quality"
    # path2 = r"floor_image_data\cubicasa5k\colorful"

    # 해당 폴더 내의 파일과 폴더 이름 가져오기
    items = get_files_and_folders(path)
    # print(items)
    image_name = "F1_original.png"
    destination_folder = "floor_plan_data"

    save_floor_plan_image(path, items, image_name, destination_folder)

