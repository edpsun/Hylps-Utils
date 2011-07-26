# -*- coding: utf-8 -*-
class wcn_default:
	def __init__(self):
		self.newline = False
	def is_start(self, tag, attrs=None):
		return True
	
	def is_stop(self, tag, attrs=None):
		return False
	
	def is_data_accept(self, data):
		return None
	
class wcn_w:
	def __init__(self):
		self.newline = False
	def is_start(self, tag, attrs=None):
		if tag.lower() == 'strong':
			return True
		
		if tag.lower() == 'h2':
			return True
		
		if tag.lower() == 'h3':
			return False
		
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'id' and v == "logo":
					return True
		return None
	
	def is_stop(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if tag.lower() == 'div' and k.lower() == 'class' and v == "title": 
					return True
				if k.lower() == 'href' and v.startswith("/wap/"):
					return True
#				if k.lower() == 'href' and v == "../h48/":
#					return True
		return None
	
	def is_data_accept(self, data):
		return None

class wcn_sina:
	def __init__(self):
		self.newline = False
	def is_start(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'src' and v.startswith("image"):
					return True
		return None
	
	def is_stop(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'href' and v.startswith("zhishu"):
					return True
#				if k.lower() == 'href' and v == "../h48/":
#					return True
		return None
	
	def is_data_accept(self, data):
		return None


class wcn_qnr:
	def __init__(self):
		self.newline = False
	def is_start(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'class' and v == "item":
					return True
		return None
	
	def is_stop(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'class' and v == "class":
					return True
		return None
	
	def is_data_accept(self, data):
		key = u"本站"
		ii = data.find(key)
		if ii > -1:
			d = '[' + data[ii + len(key):ii + len(key) + 2] + ']'
			return [True, d]
		return None

	
class wcn_sn:

	def __init__(self):
		self.newline = True
	def is_start(self, tag, attrs=None):
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.find("myindex.php") > -1:
#					return True
#				if k.lower() == 'href' and v.find("/3g/stock/index.php") > -1:
#					return False
#				
		return None
	
	def is_stop(self, tag, attrs=None):
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("http://3g.sina.com.cn"): 
#					return True

		return None
	
	def is_data_accept(self, data):
		#print data
#		if data.startswith(u"今开盘"):
#			return [True]
#		if data.startswith(u"最高价"):
#			return [True]
#		if data.startswith(u"最低价"):
#			return [True]
#		if data.startswith(u"市盈率"):
#			return [True]
#		if data.startswith(u"市净率"):
#			return [True]

		
		if data == u"个股":
			return [False , True, False]
		
		if data.find(u"昨收盘") == 0:
			return [True , True, False]
		
		if data.find(u"更新时间") > -1:
			return [False , False, True]
		
		if data.find(u"总市值") > -1:
			return [True , False, True] 
		
		return None
	
class wcn_sn_sh:
	def __init__(self):
		self.newline = True
		
	def is_start(self, tag, attrs=None):
#		if tag=="card":
#			return True
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("myindex.php"):
#					return True
		return None
	
	def is_stop(self, tag, attrs=None):
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("sh_daily.php"): 
#					return True

		return None
	
	def is_data_accept(self, data):
		if data.find(u"名称:") > -1:
			return [True , True, False]
		
		if data.find(u"指数振幅") > -1:
			return [True , False, True]
		
		if data.find(u"更新时间") > -1:
			return [False ]
		return None

class wcn_sn_gl:
	def __init__(self):
		self.newline = True
		
	def is_start(self, tag, attrs=None):
		if tag == "timer":
			return True
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("myindex.php"):
#					return True
		return None
	
	def is_stop(self, tag, attrs=None):
		if attrs:
			for (k, v) in attrs:
				if k.lower() == 'href' and v.startswith("shareindex.php"): 
					return True

		return None
	
	def is_data_accept(self, data):
		if data.find(u'最后更新') != -1:
			return [False]
		if data.find(u'延迟至少') != -1:
			return [False]
		
		return None

def getwcn(type):
	if (type == None):
		return wcn_default()
	
	if type.startswith('weather_cn'):
		return wcn_w()
	elif type.startswith('sina_cn'):
		return wcn_sina()
	elif type.startswith('qnr'):
		return wcn_qnr()
	elif type == "sn":
		return wcn_sn()
	elif type == "sn_sh":
		return wcn_sn_sh()
	elif type == "sn_gl":
		return wcn_sn_gl()
	else:
		return wcn_default()
		
