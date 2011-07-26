import os, sys
import re
#print sys.argv


file_name = sys.argv[1]
#file_name = '/export/work/temp/voa/tmp/VOA_Standard_1.html'
f1 = file(file_name)
f1_output = ''
while True:
	line = f1.readline()
	if len(line) == 0: # Zero length indicates EOF
		break
	
	if line.find('id="list"') > -1:
		#print line,
		pattern1 = re.compile('(/VOA_Standard_English.*html).*_blank>(.*)</a>.*\((.*)\)')
		
		a = line.split("</li>")
		for i in a:
			matched = pattern1.search(i) 
			if matched:
				id = matched.groups()[0]
				d = matched.groups()[2].strip()
				name = matched.groups()[1].strip()
				if len(name) > 50:
					name = name[0:49]
				

				print id + " " + d + " " + name.replace(" ", "_")
	#f1_output = file(sub_logpath, 'w')
	#f1_output.write(line)
	
f1.close()
