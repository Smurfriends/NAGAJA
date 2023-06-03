
import requests
from PIL import Image
import io

# Specify the URL
url = "http://127.0.0.1:5000/predict"

# Open the image file in binary mode
with Image.open("apple.jpg") as img:
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
    print("Response Content:", response.text)
    print("class는 ", response.json().get('prediction')) # if 1, not floor. elif 0, floor.

