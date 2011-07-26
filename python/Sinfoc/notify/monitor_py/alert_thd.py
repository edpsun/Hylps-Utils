import threading  
import time  
import os, sys

class MsgAlert(threading.Thread):  
	def __init__(self):
		threading.Thread.__init__(self)
		self.is_show = False
		self.interval = 5
		self.setDaemon(True)
	
	def set_show(self, b):
		self.is_show = b
		
	def run(self):  
		while True:
			if  self.is_show:
				if os.name == 'posix':
					os.system('zenity --info --title "ST Alert" --text "There is MSG" >/dev/null 2>&1')
				else:
					os.system("wscript.exe bin\\w_alert.vbs")
				self.is_show = False
			time.sleep(self.interval) 

class ShowImageThd(threading.Thread):  
	def __init__(self,url):
		threading.Thread.__init__(self)
		self.url = url
		self.setDaemon(True)
	
		
	def run(self):  
		os.system("chromium-browser " + self.url + " >/dev/null 2>&1 &")


class InputThd(threading.Thread):  
	def __init__(self, main_object):
		threading.Thread.__init__(self)
		self.main_object = main_object
		self.interval = 3
		self.setDaemon(True)
		
	def run(self):  
		while True:
			i = raw_input("")
			self.main_object.set_cont_type(i)
			time.sleep(self.interval)     
