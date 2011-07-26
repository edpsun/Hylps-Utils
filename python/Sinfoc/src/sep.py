import os, sys
print sys.argv

#===========================
def get_log_filename(line):
	start = line.find("<")
	end = line.find(">")
	
	fn = line[start + 1:end].replace(' ', '_').replace(',', '')
	return fn
	
	
if len(sys.argv) == 2:
	log_file = sys.argv[1]
else:
	print "Please give the server log path."
	sys.exit(-1)
	

if not os.path.exists(log_file):
	print "[Error] File not exists."
	sys.exit(-1)

#=============================================================
fs = os.path.split(log_file)

log_name = fs[1]
if fs[0] == '':
	log_file_path = "."
else:
	log_file_path = fs[0]

log_basename = log_name.split('.')[0]
log_sub_dir = os.path.join(log_file_path, log_basename)

print "[INFO] Log Name      :", log_name
print "[INFO] Log Path      :", log_file_path
print "[INFO] Log Sub  Path :", log_sub_dir
if not os.path.exists(log_sub_dir):
	os.mkdir(log_sub_dir)

#=============================================================
f_log = file(log_file)
f_output_log = None
while True:
	line = f_log.readline()
	if len(line) == 0: # Zero length indicates EOF
		break
	
	if line.find('000377> <Starting W') > -1:
		sub_logname = log_basename + "_" + get_log_filename(line)
		print " =>log file: " + sub_logname
		
		if f_output_log != None:
			f_output_log.close()
		
		sub_logpath = os.path.join(log_sub_dir, sub_logname)
		f_output_log = file(sub_logpath, 'w')
	
	f_output_log.write(line)

f_log.close()	
if f_output_log != None:
	f_output_log.close()
