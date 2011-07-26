#===============================================================================
# PropertyManager class definition
#===============================================================================
import sys, os
import string
import re
import wm_utils
from wm_def import *
import time

class PropertyManager:
	"""
	PropertyManager, to deal with properties
	"""
	def __init__(self, config_file=None):
		self.env_props_map = {}
		self.read_env()		
		
		self.property_map = {}
		
		self.conf_file = None
		if config_file != None:
			self.read_prop_file(config_file)
		
		self.pattern = re.compile('(\${)(.*)(})(.*)')
	
	def print_map(self):
		print self.property_map

	# Read Properties from Env
	def read_env(self):
		for k, v in os.environ.items():
			self.env_props_map[k] = v
			
	# Show all properties 
	def list_env_properties(self , type=''):
		print wm_utils.format_tuple_array(self.env_props_map, "------[Env Properties]-----")
		
	def list_properties(self , type=''):
		if type == '':
			print wm_utils.format_tuple_array(self.property_map, "------[Properties]-----")
		else:
			self.list_properties_by_types([type])
	
	def list_properties_by_types(self , types):	
		for type in types:
			ret = self.get_properties_map(type)
			print wm_utils.format_tuple_array(ret, "--------[" + type + "]-------")

	def set_property(self, prefix , name , value):
		key = prefix + ":" + name
		self.property_map[key] = [prefix , name, value]

	def get_property(self, prefix=None , name=None, defaultValue=None):
		self.check_reload_conf_file()
		
		if name == None:
			name = prefix
			prefix = ''
		key = prefix + ":" + name
		v = self.property_map.get(key, defaultValue)
		if v == None:
			return defaultValue
		if len(v) == 3:
			return v[2]
		else:
			return v

	def get_env_property(self, name, defaultValue=None):
		return self.env_props_map.get(name, defaultValue)

	def get_properties_map(self, prefix1, prefix2='', keep_full_name='true'):
		'''
		get_properties_map(self, prefix1, prefix2='', keep_full_name='true')
		'''
		self.check_reload_conf_file()
		
		if prefix2 is None or prefix2 == '':
			prefix = prefix1 + ":"
		else:	
			prefix = prefix1 + ":" + prefix2 + "."
		
		ret = {}
		for k, v in self.property_map.items():             
			if k.startswith(prefix):
				if keep_full_name == 'true':
					ret[v[1]] = v[2]
				else:
					delimiter = "."
					str = v[1].split(delimiter, 1)
					
					if len(str) == 0:
						vname = ''
					else:
						vname = str[len(str) - 1]
					ret[vname] = v[2]
		return ret

	def get_properties(self, prefix1, prefix2='', keep_full_name='true'):
		'''
		get_properties(self, prefix1, prefix2='', keep_full_name='true'):
		'''
		return self.get_properties_map(prefix1, prefix2, keep_full_name).items()

	def check_reload_conf_file(self):
		if self.conf_file is None:
			return 
		
		time1 = time.ctime(os.stat(self.conf_file).st_mtime)
		if(self.conf_file_time != time1):
			print "[INFO] reload conf file:",self.conf_file
			self.read_prop_file(self.conf_file)
		
#=============================================================================================
#=============================================================================================
#=============================================================================================
#=============================================================================================

	# Read Properties from Configure file
	def read_prop_file(self , config_file):
		if config_file is None:
			return
		
		temp_config_file = ""
		filePath1 = os.path.abspath(config_file)
		if not os.path.exists(config_file):
			temp_config_file = os.path.split(sys.argv[0])[0] + "/" + config_file
			filePath2 = os.path.abspath(temp_config_file)
			if not os.path.exists(temp_config_file):
				print "[WARN] property file not found! "
				print "   [-]FilePath1:", filePath1
				print "   [-]FilePath2:", filePath2
				return
			else:
				print "[INFO] Loading property file. Path:", filePath2
				config_file = tempConfigFile
		else:
			print "[INFO] Loading property file. Path:", filePath1
		
		self.conf_file = config_file
		self.conf_file_time = time.ctime(os.stat(self.conf_file).st_mtime)
		
		file1 = open(config_file)
		
		lines = file1.readlines()
		prefix = "import "
		appendedPrefix = ""
		for propPair in lines:
			if propPair.startswith(prefix):
				str = propPair.split(" ")
				
				if len(str) >= 2:
					importfile = str[1].strip()
					if len(str) == 3:
						appendedPrefix = str[2].strip()
					if len(importfile) > 0:
						(currentdir, filename) = os.path.split(config_file) 
						self.readImportFileProperties(importfile, currentdir, appendedPrefix)
						continue
			
			pair = self.parseProperties(propPair)

			if len(pair) == 3:
				self.property_map[pair[0] + ":" + pair[1]] = pair
			else:
				pass
		file1.close()

	# Read Import Properties from Configure file
	def readImportFileProperties(self , configFile, currentdir, appendedPrefix):
		if configFile is None:
			return
			 
		if not os.path.exists(configFile):
			if not os.path.exists(currentdir + "/" + configFile):
				filePath = os.path.abspath(configFile)
				print "[WARN] Import properties file not found! Name:", configFile
				return
			else:
				configFile = currentdir + "/" + configFile
		print "[Import file]", configFile

		file1 = open(configFile)
		lines = file1.readlines()
		for propPair in lines:
			propPair = propPair.strip()
			if appendedPrefix is not None and len(appendedPrefix) > 0 and not propPair.startswith('#') and len(propPair) > 0 :
				propPair = appendedPrefix + ":" + propPair
			pair = self.parseProperties(propPair)

			if len(pair) == 3:
				self.property_map[pair[0] + ":" + pair[1]] = pair
			else:
				pass
		file1.close()
		
	# Parser Property from property str
	# The str starting with '#' will be ignored. 
	# 'ss'     =>['ss','']
	# 'ss=a'   =>['ss','a']
	# 'pp:ss=b'=>['pp','ss','a']
	def parseProperties(self, propPair):
		propPair = propPair.strip()
		if propPair.startswith('#') or len(propPair) == 0:
			return []
		
		pair = string.split(propPair, "=", 1)
		if(len(pair) == 1):
			pair = [pair[0], '']

		if pair[0].find(":") != -1:
			pair2 = string.split(pair[0], ":", 1)
			pair2.append(self.parseEnvPlaceHolder(pair[1]))
			pair = pair2
		else:
			pair2 = ['', pair[0], self.parseEnvPlaceHolder(pair[1])]
			pair = pair2

		pair = map(lambda x: x.strip(), pair) 
		return pair		

	def parseEnvPlaceHolder(self, str):
		ret = str
		if(str.startswith('${')):
			search = self.pattern.search(str)                                                                                                   
			sgroup = ''                                                                                                                      
			if search:                                                                                                                     
		 		sgroup = search.groups()                                                                                                                                                                              
	 			envValue = os.getenv(sgroup[1])

		 		if(envValue != None):                                                                        
		 			if len(sgroup) == 4:                                                                                                 
		 				ret = envValue + sgroup[3]
		 			else:
		 				ret = envValue
#		 		else:
#		 			ret="UNDEFINED"                                                                            
		return ret
	
if __name__ == "__main__":
	prop_manager = PropertyManager()
	
	prop_manager.set_property(WORK_MAN, "TYPE", "domain")
	
	prop_manager.set_property(WORK_MAN, "STYPE", "gen")
	
	prop_manager.set_property(COMMAND_LINE, "a", "av")
	prop_manager.set_property(COMMAND_LINE, "b", "bv")
	prop_manager.set_property(COMMAND_LINE, "pp.qq1", "vv1.vv")
	prop_manager.set_property(COMMAND_LINE, "pp.qq2", "vv2.vv")
	prop_manager.set_property(COMMAND_LINE, "pp.qq3", "vv3.vv")
	prop_manager.set_property(COMMAND_LINE, "pp.qq4", "vv4.vv")
	
	print '------1'
	prop_manager.list_properties()
	prop_manager.list_properties(WORK_MAN)
	prop_manager.list_properties(COMMAND_LINE)
	
	print '------2'
	print prop_manager.get_property(WORK_MAN, 'TYPE')
	print prop_manager.get_property(WORK_MAN, 'TYPE1', "default is epected!")
	
	print prop_manager.get_env_property('HOME', "!")
	print prop_manager.get_env_property('TYPE1', "default is epected!")
	
	print '------3'
	print prop_manager.get_properties(WORK_MAN)
	print prop_manager.get_properties(COMMAND_LINE, 'pp')
	print prop_manager.get_properties(COMMAND_LINE, 'pp', 'false')
	print prop_manager.get_properties(COMMAND_LINE)
	
	print '------4'
	print prop_manager.get_properties_map(WORK_MAN)
	print prop_manager.get_properties_map(COMMAND_LINE, 'pp')
	print prop_manager.get_properties_map(COMMAND_LINE, 'pp', 'false')
	print prop_manager.get_properties_map(COMMAND_LINE)
