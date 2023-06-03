from flask import Flask, request, jsonify
import torch
import torch.nn as nn
import torchvision.transforms as transforms
from PIL import Image
from flask_ngrok import run_with_ngrok

# Define the API
app = Flask(__name__)

# CNN 모델 정의
class CNN(nn.Module):
    def __init__(self):
        super(CNN, self).__init__()

        self.features = nn.Sequential(
            nn.Conv2d(1, 16, kernel_size=3, stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2)
        )

        self.classifier = nn.Sequential(
            nn.Linear(16 * 128 * 128, 256),
            nn.ReLU(),
            nn.Linear(256, 2),
            # nn.Softmax(dim=1)
            nn.Sigmoid()
        )

    def forward(self, x):
        x = self.features(x)
        x = x.view(x.size(0), -1)
        x = self.classifier(x)
        # print('x: ', x)
        return x

# Load the saved model
model = CNN()
model.load_state_dict(torch.load('model_imagesize_256.pth'))
model.eval()

# Define the image transformations
transform = transforms.Compose([
    transforms.Resize((256, 256)),  # Resize the image
    transforms.Grayscale(num_output_channels=1),  # Convert to grayscale
    transforms.ToTensor(),
    transforms.Normalize((0.5,), (0.5,))  # Normalize the tensor with mean and standard deviation
])

# Define the API route
@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        print("no image")
        return jsonify({'error': 'No image found'})

    # Load the image from the request
    image = request.files['image']
    print("image is none? : ", image )
    img = Image.open(image)
    img = transform(img).unsqueeze(0)

    # Make predictions
    with torch.no_grad():
        outputs = model(img)
        _, predicted = torch.max(outputs.data, 1)
        prediction = predicted.item()
        print('prediction: ', prediction)

    # Return the prediction result
    return jsonify({'prediction': prediction})

# Run the Flask app
if __name__ == '__main__':
    # run_with_ngrok(app)
    app.run()

