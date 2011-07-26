'''
WorkMan
It can be used with jython or python. You can 
plug in business operator filled with your logic.

'''
import os, sys
import getopt


# Add the current py dir to sys dir
# this method is used for jython / WLST
# python can deal with it properly
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
	
#=======================================================================================
# Start to run
#=======================================================================================
def add_wm_path():
	wm_root = os.path.abspath(os.path.split(__file__)[0])
	append_dir_recursely_to_syspath(wm_root)
	append_dir_recursely_to_syspath(wm_root+'/..')
	
def main(argv):
	print "[INFO] argv=>", argv
	add_wm_path()
	#print sys.path
	import wm_core
	wm_core.main(argv)

if __name__ == "__main__":
    sys.exit(main(sys.argv))
