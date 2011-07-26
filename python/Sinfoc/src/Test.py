connsysprops = 'v$session.program:jdbc.qa.program,v$session.process:jdbc.qa.process'
print connsysprops

kps = connsysprops.split(",")
sys_props = {}
for kv in kps:
	kpair = kv.split(":")
	if len(kpair) != 2:
		print "[WARN] not valid system property format:", kv
	else: 
		sys_props[kpair[0]] = kpair[1]
	
print sys_props
print len(sys_props)

conn_sys_props_map = sys_props
if conn_sys_props_map != None:
	print "conn_sys_props_map", conn_sys_props_map
	for (k,v) in conn_sys_props_map.items():
		print k,v

