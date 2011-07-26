# -*- coding: utf-8 -*-
import sys, os
import string
import wm_envholder

from wm import Dispatcher 
from pyfetion import *
from wm_def import *
import codecs

class IKTQDispatcher(Dispatcher):

	def __init__(self):
		Dispatcher.__init__(self)
		self.msg_list = {}
		mode = wm_envholder.props_manager.get_property(COMMAND_LINE, 'mode')
		if(mode != None and mode == "product"):
			self.PRODUCT_MODE = True
		else:
			self.PRODUCT_MODE = False
		print "PRODUCT_MODE:", self.PRODUCT_MODE
		
	def process(self):
		wm_envholder.props_manager.list_properties()
		uids = wm_envholder.props_manager.get_properties_map("UID")
		fetion = self.getFetionInstance()
		
		for uid, lid_str in uids.items():
			self.send_sms(uid, lid_str, fetion)
		
		
	def send_sms(self, uid, lid_str, fetion):

		lids = lid_str.split("_")
		msgs = []
		for lid in lids:
			m = self.getMsg(lid)
			if (m):
				msgs.append(m)
		
		print "\nUID:", uid
		print "--------------"
		print "MSG:", msgs
		
		if not self.PRODUCT_MODE:
			print "Debug Mode. Return."
			return 
		
		rets = []
		if(uid == "X"):
			uid = None # send it to myself
		for msg in msgs:
			ret = fetion.send_sms(msg, uid, True)
			rets.append(ret)
		print "RET:", rets
		
	def getMsg(self, lid):
		msg_home = os.getenv("FS_HOME", None)
		if(not msg_home):
			print "Can not read FS_HOME. Exit."
			exit - 1
		
		lid_file_path = msg_home + "/tmp"
		lid_file = lid_file_path + "/" + lid
		
		if not os.path.exists(lid_file):
			print "Lid file not found for :" , lid_file
			return None
		#file1 = codecs.open(lid_file,'r','utf-8')
		file1 = open(lid_file)
		
		msg = file1.read()
		#mgs=msg.decode('utf-16')
		file1.close()
		return msg 

	
	def getFetionInstance(self):
		if not self.PRODUCT_MODE:
			return "Debug mode. Will not login to fetion."
		
		phone = PyFetion("13581927326","sun71716169","HTTP")
		try:
			phone.login()
		except PyFetionSupportError,e:
			print "Unknown fetion number."
			return 1
		except PyFetionAuthError,e:
			print "Error phone number"
			return 2
			
		if phone.login_ok:
			print "Login succeed!"
		else:
			print "Login failed!"
			exit - 1
		
		return phone
