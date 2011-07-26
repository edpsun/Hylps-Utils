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
	
	def getmsg(self):
		return self.msg
	
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
		if self._level_stack and tag == self._level_stack[ - 1]:
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
		
		acc_data=False
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
			if self.condition and self.condition.newline:
				self.msg += data +"\n"
			else:
				self.msg += data +" "
			