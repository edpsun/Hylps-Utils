# -*- coding: utf-8 -*-
import sys, os
import string

from wm import WorkUnit 
from wm_def import *
from wm_utils import *
import time
import datetime
from pinyin import Hanzi2Pinyin
import alert_thd

if os.name == 'nt':
	import WConio
	WConio.textattr(WConio.CYAN)
elif os.name == 'posix':
	from termcolor import colored 


cont_type_stock = 's'
cont_type_history = 'h'
cont_type_exit = 'x'
cont_type_show_k_daily = 'kd'
cont_type_show_k_min = 'km'
cont_type_show_verbose = 'vb'
cont_types = {cont_type_show_k_daily:'show_k_daily', cont_type_show_k_min:"show_k_minute", cont_type_stock:'stock', cont_type_history:'history', cont_type_show_verbose:'verbose', cont_type_exit:'exit'}

class MonitorDispatcher(WorkUnit):

	def __init__(self):
		WorkUnit.__init__(self)
		self.msgs = []
		self.st_info_list = []
		self.id_code_map = {}
		self.alert = alert_thd.MsgAlert()
		self.alert.start()
		
		self.inputthd = alert_thd.InputThd(self)
		self.inputthd.start()
		self.cont_type = (cont_type_stock)
		self.last_cont_type = cont_type_stock
		
		self.exit_sleep = False
		self.history_file = file('history.tmp', 'w+')
		
		self.flag = None 
		self.vb = True
		
		now = datetime.datetime.now()
		d_start = now.replace(hour=9, minute=30, second=0 , microsecond=0)
		
		td = datetime.timedelta(hours=2)
		td3 = datetime.timedelta(minutes=2)
		self.morning = (d_start , d_start + td + td3)
		
		td2 = datetime.timedelta(minutes=90)
		self.noon = (d_start + td2 + td, d_start + td2 + td + td + td3)
		
		self.hpconvertor = Hanzi2Pinyin()
	
	def set_cont_type(self, type):
		type = type.replace('\r', '')
		
		if (len(type.strip()) <= 0):
			return 
		if type.find(' ') > -1 and len(type.strip()) > 0:
			tmp_cont = type.split(" ")
		else:
			tmp_cont = []
			tmp_cont.append(type)
		
			
		if cont_types.keys().count(tmp_cont[0]) > 0:
			if tmp_cont[0] != self.cont_type[0]:
				self.exit_sleep = True
			
			self.last_cont_type = self.cont_type
			self.cont_type = tmp_cont
		else:
			print "Unkown CMD.", tmp_cont[0], " Valid:", cont_types.keys()
		
		
	def processImpl(self, wm_ctx):
		self.ctx = wm_ctx
		self.verbose = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'verbose', None)
		show_cont_vb = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'VB', "true")
		if(show_cont_vb == 'false'):
			self.vb = False
						
		self.skip_time_check = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'STC', None)
		
		sleep_t = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'SLPT', None)
		
		if(sleep_t == None):		
			self.sleep_time = self.ctx.getPropsHelper().get_property('', 'sleep', '120')
		else:
			self.sleep_time = int(sleep_t)
		
		if self.verbose:
			print "[%s - %s] \n[%s - %s] " % (self.morning[0], self.morning[1] , self.noon[1], self.noon[1])
		
		while(True):
			now = datetime.datetime.now()			
			is_in_trade_time = self.if_in_trade_time(now)

			if self.cont_type[0] == cont_type_stock:
				if not is_in_trade_time and self.last_cont_type != self.cont_type:
					self.show_monitor_info(False)
					self.last_cont_type = self.cont_type
					self.print_cmd_list()
				if is_in_trade_time: 
					self.show_monitor_info(True)
					self.print_cmd_list()
			elif self.cont_type[0] == cont_type_history:
				self.clean_screen()
				print "[", now, ']'
				print "Show Alert MSG history"
				if is_in_trade_time:
					self.prepare_stock_info()
					self.write_hitory()
				self.show_history()
				self.print_cmd_list()
			
			elif self.cont_type[0] == cont_type_show_k_daily :
				si = None
				if len(self.cont_type[0]) == 2:
					try:
						si = int(self.cont_type[1])
					except:
						print "Error: only index supported."
						
				self.show_st_k_image(cont_type_show_k_daily, si)
				self.cont_type = self.last_cont_type
			elif self.cont_type[0] == cont_type_show_k_min:
				si = None
				if len(self.cont_type[0]) == 2:
					try:
						si = int(self.cont_type[1])
					except:
						print "Error: only index supported."
						
				self.show_st_k_image(cont_type_show_k_min, si)
				self.cont_type = self.last_cont_type
			elif self.cont_type[0] == cont_type_show_verbose:
				self.vb = not self.vb
				self.show_monitor_info(False)
				self.print_cmd_list()
				
				self.last_cont_type = cont_type_stock
				self.cont_type = cont_type_stock
			elif self.cont_type[0] == cont_type_exit:
				sys.exit(0)
			else:
				print "Unknown cmd:", self.cont_type[0], " sleep ", self.sleep_time
			
			self.sleep(self.sleep_time)
	
	def show_st_k_image(self, type, si):
		if si == None:
			return
		u = 'http://image.sinajs.cn/newchart/%s/n/%s.gif'
		
		if self.id_code_map.keys().count(str(si)) > 0:
			code = self.id_code_map[str(si)]
		else:
			print "Show Index not found :", si
			return
		
		if type == cont_type_show_k_daily :
			url = u % ('daily', code)
		elif type == cont_type_show_k_min:
			url = u % ('min', code)
		else:
			print "Type unsupported!", type
			return 
		
		#print url
		thd = alert_thd.ShowImageThd(url)
		thd.start()
	
	def show_monitor_info(self, write_history=False):
		self.prepare_stock_info()
		
		self.clean_screen()
		now = datetime.datetime.now()
		print "[", now, '] Sleep:', self.sleep_time, "CMD:", self.cont_type
		print "Show ST INFO.."
		
		self.print_st_info_header()
		self.show_stock_info()
		if write_history:
			self.write_hitory()
		
	def sleep(self, interval):
		p = 0
		step = 2
		while (p <= int(interval)):
			if(self.exit_sleep):
				self.exit_sleep = False
				break
			
			p += step
			time.sleep(step)
	
	def if_in_trade_time(self, now):
		if not self.skip_time_check:
			if((self.morning[0] <= now <= self.morning[1]) or (self.noon[0] <= now <= self.noon[1])):
				return True
			else:
				if self.flag == None:
					self.flag = ""
					return True
				else:
					print "\rNow Stopped:", now,
					return False
		else:
			return True
				
	def print_cmd_list(self):
		msg = "msg_history-[h] stock-[s] exit-[x]:"
		if self.is_os_posix():
			msg = colored(msg, 'cyan')
			print "\r", msg
		else:
			self.window_color_out(WConio.CYAN, "\r" + msg)
			
	def clean_screen(self):
		if not self.verbose:
			if os.name == 'posix':
				os.system('clear')
			else:
				os.system('cmd /c cls')
	
	
	def prepare_stock_info(self):
		self.clean_tmp_msgs()
		self.retrieve_stock_info('top')
		self.retrieve_stock_info('own')
		self.retrieve_stock_info('st')
		
	def retrieve_stock_info(self, type):
		start_index = len(self.id_code_map) + 1
		
		st_list = self.ctx.getPropsHelper().get_properties(type)
		sub_list = []
		
		download_list=[]
		for (code, name) in st_list:
			download_list.append(code)
		
		st_info_map = self.get_st_info(download_list)
		
		for (code, name) in st_list:
			if len(code) != 8:
				print "Error: Invalid stock id %s.  Should be [szxxxxxx|shzzzzzz]" % code
				continue 
			
			st_info = st_info_map.get(code,None)
			if(st_info == None):
				continue
			
			if st_info[3] == '0.00':
				continue
			
			st_map = {'show_index':str(start_index), 'price':st_info[3], 'high':st_info[4], 'low':st_info[5], 'last_day':st_info[2], 'time':st_info[31]}
			st_map['code'] = code

			self.id_code_map[str(start_index)] = code
			
			start_index += 1
			
			
			n = st_info[0].decode('gbk').replace(" ", "")
			if(len(n) == 3):
				n += "　"
			st_map['name'] = n
			
			sub_list.append(st_map)
			
			element_list = self.ctx.getPropsHelper().get_properties(code)
							
			for (index, condition) in element_list:
				if(condition.find("skip") > -1):
					continue
				
				if(index.find("by_index") > -1):						
					if(index.find("_")):
						index = index[index.find("_") - 1]
					ii = int(index)
					
					bool_str = st_info[ii] + condition
					br = eval(bool_str)
					
					exp = "[%s] " % index + self.translate_index(index) + ":" + bool_str
					
					if self.verbose:
						print "[%s]" % index, self.translate_index(index), bool_str,
						print br
	
				else:
					bool_str = condition % st_map
					br = eval(bool_str)
					
					if  self.verbose:
						print "Expression:%30s [%s]" % (condition, br)
							
					exp = condition % {'price':"(P)" + st_info[3], 'high':"(H)" + st_info[4], 'low':"(L)" + st_info[5]}
					
				if br:
					ms = st_map.copy()
					ms['exp'] = exp
					self.msgs.append(ms)
					if(index.find("once") > -1):
						self.ctx.getPropsHelper().set_property(code, index, 'skip')
		
		self.st_info_list.append(sub_list)
	def write_hitory(self):
		if len(self.msgs) == 0:
			return
		
		self.history_file.seek(0, os.SEEK_END)
		self.history_file.write ("---------------------------\n")

		for info in self.msgs:
			msg = "[%(time)s] %(name)6s [%(price)-6s] [%(low)6s-%(high)-6s]  EXP:%(exp)-50s\n" % info
			self.history_file.write(msg)
		
		self.history_file.flush()
	
	def show_history(self):
		self.history_file.seek(0)
		lines = self.history_file.readlines()
		for line in lines:
			print line.decode('utf-8'),
	
	def clean_tmp_msgs(self):
		self.msgs = []
		self.st_info_list = []
		self.id_code_map = {}
		
	def show_stock_info(self):
		self.show_stock_watch()
		print "========================================================================================="
		self.show_txt_warning()
		self.show_warning()	
		
	
	def print_st_info_header(self):
		if(not self.vb):
			return;
		header = {'increase': 'Increase', 's_s': '+/-', 'code': 'Code',
				'name': u'名　　称', 'price': 'Price', 'last_day': 'Last Day',
				'high': 'High', 'low': 'Low', 'incr_percent': 'iPctg', 'show_index':'ID'}
		print "----------------------------------------------------------------------------------------"
		print "%(show_index)2s %(name)-6s     %(price)-10s %(incr_percent)-10s %(increase)-10s %(low)-10s %(high)-10s %(last_day)-10s %(s_s)-11s" % header
		#print "----------------------------------------------------------------------------------------"
	
	def show_stock_watch(self):
		for sub_list in self.st_info_list:
			if(self.vb):
				print "----------------------------------------------------------------------------------------"
			else:
				print "----------------------------------------------------------------------------------------"
			for st_info in sub_list:
				increase = float(st_info['price']) - float(st_info['last_day']) 
				
				incr_percent = increase * 100 / float(st_info['last_day']) 
				st_info['increase'] = '%4.2f' % increase
				st_info['incr_percent'] = '%3.2f%%' % incr_percent
				
				s_num = abs(int(incr_percent))
				s_c = ""
				s_s = ""
				if increase > 0:
					s_c = "↗"
					if(not self.vb):
						s_c = '.'
				elif increase < 0:
					s_c = "↘"
					if(not self.vb):
						s_c = '.'
				else:
					s_c = "=="
					
				for i in range(-1, s_num):
					s_s += s_c
					
				st_info['s_s'] = s_s
				
				#print st_info
				iswatch = self.ctx.getPropsHelper().get_property('flag-' + st_info['code'], 'watch', 'false')
				
				if (iswatch != 'false'):
					st_info['watch_flag'] = "=> " 
				else:
					st_info['watch_flag'] = "   " 
				
				#print "%(name)-6s [%(price)5s] %(s_s)-11s [%(increase)4.2f(%(incr_percent)3.2f%%)] [%(low)+6s-%(high)-6s]" % st_info
				if(self.vb):
					msg = "%(show_index)2s %(name)-6s %(watch_flag)3s %(price)-10s %(incr_percent)-10s %(increase)-10s %(low)-10s %(high)-10s %(last_day)-10s %(s_s)-11s" % st_info
				else:
					name_py = self.hpc(st_info['name'])
					st_info['name_py'] = name_py
					msg = "%(show_index)2s %(name_py)-25s %(watch_flag)3s %(price)-10s %(increase)-10s %(incr_percent)-10s %(low)-10s %(high)-10s %(s_s)-11s" % st_info
					pass
				if self.is_os_posix():
					if (increase > 0):
						msg = colored(msg, 'red')
					elif(increase == 0):
						msg = colored(msg, 'magenta')
					else:
						msg = colored(msg, 'green')
					print msg
				else:
					if (increase > 0):
						c = WConio.LIGHTRED
					elif(increase == 0):
						c = WConio.CYAN
					else:
						c = WConio.LIGHTGREEN
						
					self.window_color_out(c, msg)
			
			
					
	def show_txt_warning(self):
		if len(self.msgs) == 0:
			return
		
		for info in self.msgs:
			
			if(self.vb):
				msg = "[%(time)s] %(name)6s [%(price)-6s] [%(low)6s-%(high)-6s]  EXP:%(exp)-50s" % info
			else:
				name_py = self.hpc(info['name'])
				info['name_py'] = name_py	
				msg = "[%(time)s] %(name_py)-30s [%(price)-6s] [%(low)6s-%(high)-6s]  EXP:%(exp)-50s" % info
			if self.is_os_posix():
				print colored(msg, 'yellow')
			else:
				self.window_color_out(WConio.YELLOW, msg)
			
	def translate_index(self, iis):
		if iis == '3':
			s = 'Price'
		elif iis == '4':
			s = 'High'
		elif iis == '5':
			s = "Low"
		else:
			s = iis
		return s 		
	
	def show_warning(self):	
		if len(self.msgs) == 0:
			return
#			
#		msg = ""	
#		for info in self.msgs:
#			s = "[MSG] -> %(name)6s  '[%(price)s] [%(low)s-%(high)s]  EXP:%(exp)s" % info
#			msg += s + "\n"
	
		#gobject.idle_add(update_msg, self.tv, msg)
		#gobject.idle_add(show_widget, self.alert)

		
		self.alert.set_show(True)
		
		
		#os.system('d1 ./bin/alert.sh '+cmd )
		
		#os.system("firefox")
	def get_st_info(self, st):
		l = ','.join(st)
		url_pattern = "http://hq.sinajs.cn/list="
		url = url_pattern + l
		
		#print url
		response = http_send(url)	
		data = response.read()
		str_array = data.split(';')
		map = {}
		kw='hq_str_'
		for s in str_array:
			s=s.replace('\n', '')
			s=s.replace('"', '')
			k1=data.find(kw);
			k2=data.find('=');
			key = s[k1 +len(kw):k2]
			
			i1 = data.find('=')
			value = s[i1 + 1:].split(',')
			if(len(key) < 3):
				continue
			map[key] = value
			
		return map 
		
				
	def before_process(self, wm_ctx=None):
		print "  => [before process]"
	
	def after_process(self, wm_ctx=None):
		print "  => [after process]"

	def is_os_posix(self):
		return os.name == "posix"
	
	def window_color_out(self, c, txt):
		WConio.textattr(c)
		print txt
	
	def hpc(self, value):
#		ps = self.hpconvertor.convert(value)
#		rt = ''
#		i = 0
#		for p in ps:
#			if i <= 1:
#				rt = rt + p.upper() + "-"
#				i = i + 1
#			else:
#				rt = rt + p[0].upper()
#		return rt
		return '-'.join(self.hpconvertor.convert(value))
