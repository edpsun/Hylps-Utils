import threading  
import time  
class timer(threading.Thread):  
	def __init__(self, no, interval):
		threading.Thread.__init__(self)
		self.no = no
		self.interval = interval   
		self.is_exit = False
	
	def set_exit(self, b=False):
		self.is_exit = b
		
	def run(self):  
		while True and not self.is_exit:   
			print 'Thread Object (%d), Time:%s' % (self.no, time.ctime())   
			time.sleep(self.interval)   
	
def test():
	threadone = timer(1, 1)
	threadtwo = timer(2, 3)
	threadone.start()
	threadtwo.start()
	
	print threadone.isAlive()
	print threadone.is_alive()
	
	time.sleep(5)
	threadone.set_exit(True)
	time.sleep(5)
	print threadone.isAlive()
	print threadone.is_alive()
	
	time.sleep(15)
	threadtwo.set_exit(True)
	time.sleep(5)
	print threadtwo.isAlive()
	print threadtwo.is_alive()
if __name__ == '__main__':
	test()


