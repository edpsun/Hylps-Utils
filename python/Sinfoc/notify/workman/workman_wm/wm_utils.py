##===============================================================================
# Delivered by BJ WLS QA
# Author Edward Sun 
#        esun@bea.com
#
# Date: Sep , 2006 
#===============================================================================

import os
import sys
import socket
import traceback 
import datetime
import urllib
import urllib2
import re
def get_hostname():
	#	unameout = os.popen("uname -n","r")
	#	hostname = unameout.read()
	#	unameout.close()
	#	return hostname.strip()
	hostname = socket.gethostname()
	if hostname == None or hostname == '':
		hostname = "localhost"
	return hostname

def cygwin_path_convertor(path):
	prefix = "/cygdrive"
	path2 = ""
	
	if path is not None and path.lower().startswith(prefix) and (len(path) > len(prefix) + 1):
		path2 += path[len(prefix) + 1] + ":"
		path2 += path[len(prefix) + 2:]
		path = path2
	return path	

def format_tuple_array(key_value_set, title='--------[=======]--------'):
	if key_value_set is None:
		key_value_set = []
		
	if isinstance(key_value_set, {}.__class__):
		key_value_set = key_value_set.items()
	str = " [-] " + "\n [-] ".join(["%s=%s" % (k, v) for k, v in key_value_set])
	
	return title + "\n" + str + "\n"
	
def load_class_from_module(class_name,verbose="false"):
	try:
		m_module = __import__(class_name)
	except ImportError, e:
		print e
#		print sys.path
		if verbose == "true":
			for i in sys.path:
				print "-->", i
		print "[INFO] Module", class_name + ".py" , " Import Error!"
		return None
	
	class_o = 'UNKNOWN';
	if hasattr(m_module, class_name):
		class_o = getattr(m_module, class_name)
	else:
		print "[WARN] The class name should be consistent with the module name!"
		return None

	return class_o

def get_proxy_info():
	proxy_str = os.getenv("http_proxy", None)
	if(proxy_str):
		#print proxy_str
		p = re.compile(r'http://(.*):(.*)') 
		proxy_info = p.search(proxy_str).groups()
		proxy_info = {'user' : '',
					'pass' : '',
					'host' : proxy_info[0],
					'port' : int(proxy_info[1]) 
					}
	else:
		proxy_info = False
	
	return proxy_info


def http_send(url, body="", exheaders="", method=False, proxy_info=False):
	headers = {
			"Connection": "Keep-Alive",
			"Cache-Control": "no-cache" 
	}
	headers.update(exheaders)
	
	if not proxy_info:
		proxy_info = get_proxy_info()
		
	if proxy_info:
		proxy_support = urllib2.ProxyHandler(
											{"http":"http://%(user)s:%(pass)s@%(host)s:%(port)d" % proxy_info})
		opener = urllib2.build_opener(proxy_support)
	else:
		opener = urllib2.build_opener()
	
	urllib2.install_opener(opener)
	
	if(body == None or len(body.strip()) == 0):
		request = urllib2.Request(url, headers=headers)
	else:
		request = urllib2.Request(url, headers=headers, data=body)
	
	try:
		conn = urllib2.urlopen(request)
	except Exception, e:
		print "[Error] Failed while http send.", e
		print "Retry......"
		
		try:
			conn = urllib2.urlopen(request)
		except Exception, e:
			print "[Error] Retry failed while http send.", e
			return None
	return conn

def append_dir_recursely_to_syspath(dir):
	absdir = os.path.abspath(dir)
	if(os.path.exists(absdir) and os.path.isdir(absdir)):
		if sys.path.count(absdir) == 0 and (dir.lower().endswith("_py") or dir.lower().endswith("_wm")):
			sys.path.append(absdir)
			print "[INFO] Append [", dir, "] to sys.path."
		
		files = os.listdir(absdir)
		for file in files:
			temp_file = os.path.join(absdir, file)
			if os.path.isdir(temp_file):
				append_dir_recursely_to_syspath(temp_file)
#			else:
#				print "-",temp_file,os.path.isdir(temp_file), file.lower().endswith("_py")
	else :
		print "[WARN] " + dir + " Can not be appended to the sys.path!"


def invoke_if_has(obj, method_name, param=None):
	if hasattr(obj, method_name):
		method = getattr(obj, method_name)
		if(param == None):
			method()
		else:
			method(param)
#	else:
#		print "No method:", method_name
 
class ZhCNTzinfo(datetime.tzinfo):
    def utcoffset(self, dt): return datetime.timedelta(hours=8)
    def dst(self, dt): return datetime.timedelta(0)
    def tzname(self, dt): return 'ZhCN'
    def olsen_name(self): return 'Asia/Shanghai'
