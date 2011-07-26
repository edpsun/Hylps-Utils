import sys, os
import string

from wm import WorkUnit 
from wm_def import *
from wm_utils import *

class DomainDispatcher(WorkUnit):

	def __init__(self):
		WorkUnit.__init__(self)

	def processImpl(self, wm_ctx):
		#print wm_ctx
		print "  => From sample dispatcher" 
		print "  => TestParamNameNotDefined = ", wm_ctx.getPropsHelper().get_property('', 'TestParamNameNotDefined', 'Not found as expected.')
		print "  => TestParamName = ", wm_ctx.getPropsHelper().get_property('', 'TestParamName', 'NOT Defined')
		
		print "  => Test dispatching.  "
		print "========================"
		subtype = wm_ctx.getPropsHelper().get_property(WORK_MAN, WM_PARAM_SUB_TYPE, None)

		if subtype == None:
			print "   -> No Sub type is specified."
		else:
			print "   -> Sub Type:",subtype 
			operator = subtype
			class_o = load_class_from_module(operator,"true")
			if class_o is None:
				print "[INFO] Operator for " + subtype + " not found!"
			else:
				class_o().process(wm_ctx)
		print "========================"
		print "  => Test OK"
		
	def before_process(self,wm_ctx=None):
		print "  => [before process]"
		print "  => ",wm_ctx
	
	def after_process(self,wm_ctx=None):
		print "  => [after process]"
		print "  => ",wm_ctx
