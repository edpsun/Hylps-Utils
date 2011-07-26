# -*- coding: utf-8 -*-

import sys, os
import string

from wm import WorkUnit 
from wm_def import *
from wm_utils import *
import time
from msgutils import *


from PyFetion import *
import time
import sys
import exceptions


class NotifyDispatcher(WorkUnit):
	def __init__(self):
		WorkUnit.__init__(self)
		
	def processImpl(self, wm_ctx):
		self.ctx = wm_ctx
		print "[INFO] start..."
		
		self.plan_id = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'plan_id')
		self.mode = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'mode' , "")
		sub_list = self.ctx.getPropsHelper().get_properties(self.plan_id)
		
		print "=> plan_id  : ", self.plan_id
		print "=> mode     : ", self.mode
		print "=> sub_list : ", sub_list
		if self.mode != "product":
			print "=> DEBUG mode. Not Send."
		
		msg_utils = MsgUtils(wm_ctx)
		msg_map = msg_utils.get_msgs(self.plan_id)
		
		if self.mode == "product":
			self.phone = self.prepare_phone()
			if (self.phone == None):# retry
				self.phone = self.prepare_phone()
				
			if (self.phone == None):
				print "Can not get Phone object! Exit! Send failure!"
				sys.exit(-1)
		else:
			self.phone=None

		last_time = time.time()
		for no, lidstr in sub_list:
			lids = lidstr.split(" ")
			desc = self.ctx.getPropsHelper().get_property('UID_DESC', no)
			print "======================================"
			print "  =>", desc, no, lidstr
			for lid in lids:
				msg1 = msg_map[lid].msg
				print "  =>", msg1.encode('utf-8')
				rtn = self.send_sms(no, msg1, self.phone)
				if(rtn):
					print "  =>", "SUCCESS"
				else:
					print "  =>", "FAILED"
				
				if self.mode == "product":
					time.sleep(30)
					
				if time.time() - last_time  > 300:
					print "send heart beat..."
					last_time = time.time()
					self.phone.alive()
					time.sleep(5)
		
	def send_sms(self, num, msg, phone):
		if self.mode != "product":
			print "  => DEBUG mode. Not Send.",
			return True
		
		if num == "X":
			num = None

		try:
			if(phone == None):
				phone = self.prepare_phone()
			
			if(phone == None):
				print "  =>  Can not init fetion!",
				return False
					
			phone.send_sms(msg.encode('utf-8'), num, True)
			return True
		except Exception, e:
			print e
			return False
		
	def printl(self, msg):
	    msg = str(msg)
	    try:
	        print(msg.decode('utf-8'))
	    except exceptions.UnicodeEncodeError:
	        print(msg)
	        
	def prepare_phone(self):
		num = self.ctx.getPropsHelper().get_property('ftid', None)
		passwd = self.ctx.getPropsHelper().get_property('ftps', None)
		
		#phone = PyFetion(mobile_no,passwd,"TCP",debug="FILE")
		phone = PyFetion(num, passwd, "HTTP", debug="FILE")
		try:
			ret = phone.login(FetionHidden)
		except PyFetionSupportError, e:
			self.printl("手机号未开通飞信")
			return 1
		except PyFetionAuthError, e:
			self.printl("手机号密码错误")
			return 1
		except PyFetionSocketError, e:
			self.printl(e.msg)
			self.printl("网络通信出错 请检查网络连接")
			return 1
	
		if ret:
			self.printl("登录成功")
			return phone
		else:
			self.printl("登录失败")
			return None
		
	def before_process(self, wm_ctx=None):
		print "  => [before process]"
	
	def after_process(self, wm_ctx=None):
		print "  => [after process]"
