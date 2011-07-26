'''
WorkMan
It can be used with jython or python. You can 
plug in business operator filled with your logic.

Usage:
    => java jython workman.py [options] [dynamic parameters]
    => python workman.py [options] [dynamic parameters]
Options:
    -t ..., --type=...          The operation type to be executed.
    -s ..., --subtype=...       The sub operation type related to operation type.
    -w ..., --work=...          The work name belongs to type and subtype.
    -c ..., --conf=...          The properties file to be used to get the domain parameters.
    -e ..., --extpydir=...      To add a extra directory as python src dir.
    -h,     --help              Show this help content.
dynamic parameters:
    D:XXX1=XXX1value            Command line property.
'''
import os, sys
import getopt
from propertymanager import *
from wm_def import *
from wm_utils import *
from wm_context import WMContext

# show usage.
def usage():
	print __doc__

# Parse all the parameters
def prepare(argv):
	try:
		opts, args = getopt.getopt(argv[1:], "t:s:w:c:e:h", \
								["type=", "subtype=", "work", "conf=", "extpydir=", "help"])
		print "[INFO]", opts, args
	except getopt.GetoptError, e :
		print "[ERROR]", e
		usage()
		sys.exit(-1)
		
	prop_manager = PropertyManager()
	#deal with pre-defined parameters	
	for opt, arg in opts:
		if opt in ("-h", "--help"):
			usage()
			sys.exit()
		elif opt in ("-t", "--type"):
			prop_manager.set_property(WORK_MAN, WM_PARAM_TYPE, arg)
		elif opt in ("-s", "--subtype"):
			prop_manager.set_property(WORK_MAN, WM_PARAM_SUB_TYPE, arg)
		elif opt in ("-w", "--work"):
			prop_manager.set_property(WORK_MAN, WM_PARAM_WORK, arg)
		elif opt in ("-c", "--conf"):
			prop_manager.set_property(WORK_MAN, WM_PARAM_CONFIG_FILE, arg)
			prop_manager.read_prop_file(arg)
		elif opt in ("-e", "--extpydir"):
			prop_manager.set_property(WORK_MAN, WM_PARAM_EXT_PY_DIR, arg)
		
	#deal with dynamic parameters	
	for pair in args:
		if pair.startswith('D:'):
			pair = pair[2:]
			pair.strip()
			vo = pair.split("=")
			
			if len(vo) == 1  and len(vo[0]) > 0 :
				prop_manager.set_property(COMMAND_LINE, vo[0], '')
			elif len(vo) == 2:
				prop_manager.set_property(COMMAND_LINE, vo[0], vo[1])


	__operation = prop_manager.get_property(WORK_MAN, WM_PARAM_TYPE , '')
	if(__operation == '' or __operation is None):
		print "*[Error]* WM need type to be specified!"
		usage()
		sys.exit()	
	
	__extpydir = prop_manager.get_property(WORK_MAN, WM_PARAM_EXT_PY_DIR , '')
	if __extpydir != '':
		current = os.path.abspath(__extpydir)
		if(os.path.exists(current)):
			append_dir_recursely_to_syspath(current)
	return prop_manager
	
def process(wm_ctx):
	type = wm_ctx.getPropsHelper().get_property(WORK_MAN, WM_PARAM_TYPE)
	dispatcher = type + get_dispatcher_suffix()
	class_o = load_class_from_module(dispatcher)
	
	if class_o == None:
		dispatcher = type
		class_o = load_class_from_module(dispatcher,"true")
	
	print "[INFO] Dispatcher Name:", dispatcher
	if class_o is None:
		print "[INFO] Dispatcher for " + type + " not found. Use default."
		from wm import WorkUnit
		WorkUnit().process(wm_ctx)
	else:
		class_o().process(wm_ctx)
	
#=======================================================================================
# Start to run
#=======================================================================================
def main(argv):
	reload(sys)
	sys.setdefaultencoding('utf-8') 
	wm_ctx = WMContext()
	wm_ctx.setPropsHelper(prepare(argv))
	process(wm_ctx)

if __name__ == "__main__":
    sys.exit(main(sys.argv))
