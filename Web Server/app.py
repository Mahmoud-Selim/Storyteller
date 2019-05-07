from flask import Flask, request,render_template
import base64
import cv2
import numpy as np
import pytesseract

app = Flask(__name__)




def process_img():
	##(1) Reading the image in BGR
    img = cv2.imread('imageToSave.jpeg')
    #img = cv2.imread("test1.jpg")
    ##(2) converting to hsv color space
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    ##(3) spliting the channels
    h,s,v = cv2.split(hsv)

    ##(4) applying thresholding to threshold the S channel using "THRESH_BINARY_INV"
    th, threshed = cv2.threshold(s, 60, 255, cv2.THRESH_BINARY_INV)

    ##(5) find all the external contours on the threshed S
    #_, cnts, _ = cv2.findContours(threshed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cv2.findContours(threshed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]

    ##(6) coping the original image
    #canvas  = img.copy()

    ##(7) drawing the contour
    #cv2.drawContours(canvas, cnts, -1, (0,255,0), 1)

    ##(8) sorting and choosing the largest contour to detect the paper correctly
    cnts = sorted(cnts, key = cv2.contourArea)
    cnt = cnts[-1]
    ##(9)
    peri=cv2.arcLength(cnt,True)
    approx=cv2.approxPolyDP(cnt,0.02*peri,True)
    ##(10) rect - > returns 2 points + anagel of rotation
    rect = cv2.minAreaRect(approx)
    #print(rect)

    ##(11) transforing 2  points + angel into 4 points of rotated item
    box = cv2.boxPoints(rect)
    box = np.int0(box)

    ##(12) drwaing contour on original image commented because i dont want red contour in final image
    #img = cv2.drawContours(img,[box],0,(0,0,255),2)

    ##(13)
    x,y,w,h = cv2.boundingRect(approx)
    #print('xxxxx', x, y, w, h)

    ##(14) applying the width and height to get the detected image
    New_image=img[ y:y+h ,x:x+w ]
    #cv2.imwrite("output1.jpg",New_image)

    ##(15) converting the image into gray scale
    img=cv2.cvtColor(New_image, cv2.COLOR_BGR2GRAY)

    ##(16) applying resizing to the image for better processing
    r=900.0 / img.shape[1]
    dim=(900, int(img.shape[0] * r))
    img=cv2.resize(img, dim, interpolation = cv2.INTER_AREA)

    ##(17) applying bluring
    img = cv2.blur(img, (1,1),1)

    ##(18) applying thersholding to convert it into a binary image
    thresh = img <183
    img2 = np.ones_like(img) * 255
    img2[thresh] = 0
    #cv2.imwrite("out.jpg",img2)

    # Apply dilation and erosion to remove some noise
    kernel = np.ones((1, 1), np.uint8)
    img2 = cv2.dilate(img2, kernel, iterations=1)
    cv2.imwrite("dilated.jpg",img2)
    img2 = cv2.erode(img2, kernel, iterations=1)
    cv2.imwrite("eroded.jpg",img2)

    # Recognize text with tesseract for python
    #img2 = cv2.blur(img2, (1,1),1)
    result = pytesseract.image_to_string(img2,lang='eng')
 


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





