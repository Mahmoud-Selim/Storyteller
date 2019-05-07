from flask import Flask, request,render_template
import base64
import cv2
import numpy as np
import pytesseract

app = Flask(__name__)




def process_img():

    return result


@app.route("/")
def home():
    return render_template('index.html')

@app.route("/mahmoud")
def mahmoud():
    return render_template('index.html')

@app.route("/about")
def about():
    return render_template('about.html')

@app.route("/contact")
def contact():
    return render_template('contact.html')

@app.route('/upload', methods=['GET','POST'])
def upload_base64_file():
    if request.method == 'POST':
        convert_and_save(request.form.to_dict()['image'])
        result = process_img()
        return result


def convert_and_save(b64_string):
    with open("imageToSave.jpeg", "wb") as fh:
      fh.write(base64.b64decode(b64_string))
      fh.close()





