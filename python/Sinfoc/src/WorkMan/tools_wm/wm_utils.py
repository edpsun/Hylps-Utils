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
	
def load_class_from_module(class_name):
	try:
		m_module = __import__(class_name)
	except ImportError:
		print "[INFO] Module", class_name + ".py" , "not found."
		return None
	
	class_o = 'UNKNOWN';
	if hasattr(m_module, class_name):
		class_o = getattr(m_module, class_name)
	else:
		print "[WARN] The class name should be consistent with the module name!"
		return None

	return class_o
    	