import threading
import time
import socket
import bluetooth
import sys
import MySQLdb
import random

print("looking for nearby devices...")
nearby_devices = bluetooth.discover_devices(lookup_names = True)
print("found %d devices" % len(nearby_devices))

def what_services(addr,name):
    print("%s-%s" % (addr,name))
    for services in bluetooth.find_service(address = addr):
        print("Name: %s" %(services["name"]))
        print("Description: %s" %(services["description"]))
        print("Protocol: %s" %(services["protocol"]))
        print("Provider: %s" %(services["provider"]))
        print("Port: %s" %(services["port"]))
        print("Service id: %s" %(services["service-id"]))
        print("")

serverMACAddress,name = nearby_devices[0]
for addr, name in nearby_devices:
    print("%s-%s" % (addr,name))
    if(name=='BT04-A'):
    	serverMACAddress,name = addr,name

port = 1
sb = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
sb.connect((serverMACAddress, port))
T1,T2 = "12.4","12.4"
db = MySQLdb.connect(user='root',db='AIR',passwd='1',host='localhost' )

cursor = db.cursor()

cursor.execute("DROP TABLE IF EXISTS AIR")

sql = """CREATE TABLE AIR(
         NOW  CHAR(10),
         TIM  CHAR(50),
         TEM  CHAR(20),  
         HUM  CHAR(20))
      """

cursor.execute(sql)

cursor.execute('insert into AIR values("%s","%s","%s","%s")' % \
            					 ('NOW',time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())),T1,T2))
db.commit()

class UThread (threading.Thread):  
    def __init__(self, U):
        threading.Thread.__init__(self)
        self.user = U
    def run(self):
    	global T2
    	while(True):
			self.user.send(T2+'_'+T1+'\n')
			print(T2)
			time.sleep(2)

class myThread (threading.Thread):
    def __init__(self, threadID, name,):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
    def run(self):                 
		print "Starting " + self.name
		s = socket.socket()  
		try:
			host = '0.0.0.0' 
			port = 2014      
			s.bind((host, port))
			s.listen(10)
			print host
			print 'can bind'
			while True:
				sock,addr=s.accept()
				U=UThread(sock)
				U.start()
		except:
			print sys.exc_info()[0],sys.exc_info()[1]
			print 'wwwwwwwwwwaaaaaaaaaaaaa'
			s.close()
		else:
			s.close()


class myThread2 (threading.Thread):
    def __init__(self, threadID, name,):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
    def run(self):
		global T1
		global T2                 
		print "Starting " + self.name
		'''
		while True:
			T1=str(random.randint(10,30))
			T2=str(random.randint(10,30))
			cursor.execute('insert into AIR values("%s","%s","%s","%s")' % \
            					 ('HIS',time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())),T1,T2))
			cursor.execute('update AIR set TIM="%s",TEM="%s",HUM="%s" where NOW = "NOW" ' % \
            					 (time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())),T1,T2))
			db.commit()
			time.sleep(2)
		'''
		s = socket.socket()        
		try:
			host = '192.168.43.100' 
			port = 8080
			print 'want' 
			s.connect((host, port))
			print 'can bind222'
			while True:
				T=s.recv(1024)
				T1=T[2:6]
				T2=T[12:16]
				if( T1=='' or  T2=='' ):
					continue
				global sb
				if(float(T2)>23):
					sb.send('A')
				else:
					sb.send('C')
				print T
				print T1+'  '+T2
				try:
					cursor.execute('insert into AIR values("%s","%s","%s","%s")' % \
            					 ('HIS',time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())),T2,T1))
					cursor.execute('update AIR set TIM="%s",TEM="%s",HUM="%s" where NOW = "NOW" ' % \
            					 (time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())),T2,T1))
					db.commit()
				except:
					print sys.exc_info()[0],sys.exc_info()[1]
					print 'bug'
				else:
					continue
		except:
			print sys.exc_info()[0],sys.exc_info()[1]
			print 'wa'
			s.close()
			db.close()
		else:
			s.close()
			
MT=myThread(1,'test')
MT2=myThread2(2,'test2')	
MT.start()  	
MT2.start()

    	
