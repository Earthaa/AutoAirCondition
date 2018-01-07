import matplotlib.pyplot as plt
from flask import Flask,url_for,render_template,request,jsonify
from io import BytesIO
import os
import random
import time
import threading
import MySQLdb

app = Flask(__name__)
app.debug= True
@app.route('/')

def api_root():
	return  render_template('index.html')

@app.route('/add')
def addt():
	mykey = request.args.get('mykey')
	db = MySQLdb.connect(user='root',db='AIR',passwd='1',host='localhost' )
	cursor = db.cursor()
	print mykey
	sql='''
		SELECT * FROM AIR WHERE NOW='NOW'
		'''
	cursor.execute(sql)
	result = cursor.fetchall()
	send = result[0][2]
	db.close()
	return send
	

app.run(host='0.0.0.0')
	
'''
import matplotlib
matplotlib.use('Agg')
from flask import Flask
app = Flask(__name__)
from io import BytesIO
import os
import base64

x4 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
y4=[32,32,32,33,34,34,34,34,38,43]

def plotTree(s):

	fig = plt.figure(1,figsize=(12,6),facecolor='white')
	#TODO
	fig.plot(x4, y4,'r', label='broadcast')  
	fig.xticks(x4, group_labels, rotation=0)
	return fig
	
@app.route('/')
def index(name):
	fig=plotTrees(name)
	# Encode image to png in base64
	sio = BytesIO()
	fig.savefig(sio, format='png')
	data=base64.encodebytes(sio.getvalue()).decode()
	return html.format(fig)

app.run(host='0.0.0.0')

'''
