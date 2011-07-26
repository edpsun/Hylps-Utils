import os, sys
import re
#print sys.argv


file_name = sys.argv[1]
#file_name = '/export/weblogic/dev/src1034/wls/qa/tests/functional/jdbc/c2t/rac/multires/RacClient_3ManagedServers_MPClusterTest.java'
f1 = file(file_name)
f1_output = ''
while True:
	line = f1.readline()
	if len(line) == 0: # Zero length indicates EOF
		break
	
	if line.find("void dotest") > -1:
		line = line.replace('private void ', '')
		line = line.replace('throws Exception {', '')
		
		print line.strip(),",",
		
	if line.find("Tx1.setProperty(") > -1:
		line = line.replace('Tx1.setProperty(', '')
		line = line.replace('"', '')
		line = line.replace(');', '')
		print line.strip()
		
	
	
f1.close()
