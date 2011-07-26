from wm_def import *
from wm_utils import *
from msgparser import MsgParser
from wcn import *

class MsgUtils:
	def __init__(self, wm_ctx):
		self.ctx = wm_ctx
		
	def get_msgs(self,plan_id):
		msg_map = {}
		lidstr = self.ctx.getPropsHelper().get_property("lid_list")
		msg_types_str = self.ctx.getPropsHelper().get_property("msg_types")

		print lidstr
		print msg_types_str
		
		msg_type_4p = self.ctx.getPropsHelper().get_property("plan_mapping", plan_id, "Unknown")
		print "[INFO] msg_type for plan:", msg_type_4p
		
		getin_url = self.ctx.getPropsHelper().get_property('getin_url')
		print "Getin URL:", getin_url 
		msg_types = msg_types_str.split()
		for msg_type in msg_types:
			if msg_type_4p != msg_type:
				continue
			url_p = self.ctx.getPropsHelper().get_property(msg_type, "url", None)
			print "[INFO] URL Pattern:", url_p
			
			lids = self.ctx.getPropsHelper().get_properties_map(msg_type + "_lid")
			
			for lid, url_id in lids.items():
				wcn_type = msg_type
				if url_id.lower().startswith('http://'):
					a = url_id.split(" ")
					if len(a) == 2:
						url = a[0]
						wcn_type = a[1]
					else:
						url = url_id
				elif url_p:
					url = url_p.replace('LID', url_id)
				else:
					url = url_id
					
				print url, wcn_type
					
				wcn = getwcn(wcn_type)
				msg = self.grab_msg(url, wcn)
				print "==================================================="
				debug = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'debug', '')
				if debug == "true":
					print msg
					print "debug mode: not send."
					continue
				print msg.encode('utf-8')
				
				
				msg_vo = MsgVO(lid, msg_type, msg)
				msg_map[lid] = msg_vo
			
			return msg_map
				
			
				#params = {'msg_id':lid, 'msg_type':msg_type, 'msg_cont':msg.encode('utf-8'), 'conf':'ft_msg_src.conf', 'op':'grab', 'type':'GetinMsg'}
				#body = urllib.urlencode(params)
				#self.send_msg(getin_url, body)
		
	def grab_msg(self, url, wcn):
		print "[INFO] get msg for ", url
		response = http_send(url)	
		data = response.read()
		
		if url.find("qnr") > -1:
			f1 = "<div id=\"weather\">"
			f2 = "<div id=\"weather-s\">"
			i1 = data.find(f1)
			i2 = data.find(f2)
			if (i1 > -1 and i2 > -1):
				data = "<html>" + data[i1 + len(f1):i2] + "</html>"
				#print data
		
		#print data.decode('utf-8')
		parser = MsgParser(wcn)
		parser.feed(data.decode('utf-8'))
		parser.close()
		msg = parser.getmsg()
		return msg

class MsgVO:
	def __init__(self, id, type, msg):
		self.id = id
		self.type = type
		self.msg = msg
		
