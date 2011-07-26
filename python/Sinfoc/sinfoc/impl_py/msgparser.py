# -*- coding: utf-8 -*-
import sys
import urllib
import HTMLParser
 
class MsgParser(HTMLParser.HTMLParser):
	def __init__(self, con=None):
		HTMLParser.HTMLParser.__init__(self)
		
		self.start = False
		self.end = False
		self.msg = ''
		self.condition = con
		self.info = []
	
	def get_info(self):
		return self.info
	
	def reset(self):
		HTMLParser.HTMLParser.reset(self)
		self._level_stack = []
	
	def handle_starttag(self, tag, attrs):
		self._level_stack.append(tag)
		
		if 	self.condition != None:
			b1 = self.condition.is_start(tag, attrs)
			if b1 != None:
				self.start = b1
				
			b2 = self.condition.is_stop(tag, attrs)
			if (b2 != None):
				self.end = b2
	
		
	def handle_endtag(self, tag):
		if self._level_stack and tag == self._level_stack[ -1]:
			self._level_stack.pop()
		
		if 	self.condition != None:
			b1 = self.condition.is_start(tag)
			if b1 != None:
				self.start = b1
				
			b2 = self.condition.is_stop(tag)
			if (b2 != None):
				self.end = b2
			
	def handle_data(self, data):
		data = data.strip()
		if len(data) == 0:
			return 
		
		acc_data = False
		if 	self.condition != None:
			ret = self.condition.is_data_accept(data)
			if ret != None:
				if len(ret) == 1:
					if not ret[0]:
						return
					else:
						acc_data = True
				elif len(ret) == 2:
					if not ret[0]:
						return
					else:
						acc_data = True
						data = ret[1]
				elif len(ret) == 3:
					self.start = ret[1]
					self.end = ret[2]
					if not ret[0]:
						return
					else:
						acc_data = True
					
		if (self.start and  not self.end) or (acc_data):
			#self.info.append(data)
			for i in data:
				self.info.append(i)


class wcn_sn:
	def __init__(self):
		self.newline = True
		self.elemap = {u'股票名称':'name', u'当前价':'price', u'换手率':'exchange',
					u'涨跌额':'increase', u'市盈率':'pe', u'市净率':'pbv', u'成交额':'volumn', u'更新时间':'update_time'}
		
	def is_start(self, tag, attrs=None):
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("myindex.php"):
#					return True
		return None
	
	def is_stop(self, tag, attrs=None):
#		if attrs:
#			for (k, v) in attrs:
#				if k.lower() == 'href' and v.startswith("http://3g.sina.com.cn"): 
#					return True
		return None
	
	def is_data_accept(self, data):
		#print data
		tmp_map = []
		for key in self.elemap.keys():
			i = data.find(key + ":")
			l = len(key)
			if i > -1:
				ii = data.find(" ", i)
				
				if (ii > -1):
					tmp_map.append((key, self.elemap[key], data[i + l + 1:ii]))
				else:
					tmp_map.append((key, self.elemap[key], data[i + l + 1:]))
		
		
		k1 = data.find("(")
		k2 = data.find(")")
		if(k2 -k1 ==9 ):
			tmp_map.append((u'股票名称', self.elemap[u'股票名称'], data[:k1]))
			
		
		if 	len(tmp_map) > 0:
			return (True, tmp_map)
		
		return None
