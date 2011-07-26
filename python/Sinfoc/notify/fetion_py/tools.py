import os
class JavaHTTP:
	def __init__(self):
		pass
	
	def http_send(self, url, body='', exheaders=''):
		print url
		cp = self.get_classpath()

		cmd="java -cp " + cp + " com.hylps.TestHTTP  '" + url +"'"
		print cmd
		
		fp = os.popen(cmd)
		msg = fp.read() 
		
		vo = VO()
		vo.set_value(msg)
		#print "[done] ",url
		return vo
	
	def get_classpath(self):
		jar_path = os.getenv('JAR_PATH', './lib')
		jars = ['hi.jar', 'commons-codec-1.2.jar', 'commons-httpclient-3.0.jar', 'commons-logging-api.jar', 'commons-logging.jar']
		return jar_path + '/' + (':' + jar_path + '/').join(jars)
		
		
class VO:
	def __init__(self):
		self.msg = ""
		pass
	
	def set_value(self, value):
		self.msg = value
	
	def read(self):
		return self.msg
	
	def info(self):
		return self.msg

#fp = os.popen("java -cp ./lib/hi.jar:./lib/commons-codec-1.2.jar:./lib/commons-httpclient-3.0.jar:./lib/commons-logging-api.jar:./lib/commons-logging.jar com.hylps.TestHTTP")
#print fp.read() 
