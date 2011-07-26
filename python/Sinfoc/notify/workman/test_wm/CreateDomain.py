import sys, os
import string

from wm import WorkUnit 
from wm_def import *

class CreateDomain(WorkUnit):

	def __init__(self):
		WorkUnit.__init__(self)

	def processImpl(self, wm_ctx):
		#print wm_ctx
		print "  => From sample operator." 
		print "  => TestParamName = ", wm_ctx.getPropsHelper().get_property('', 'TestParamName', 'NOT Defined')
		
		# deal with work
		work_name = wm_ctx.getPropsHelper().get_property(WORK_MAN, WM_PARAM_WORK, None)
		if work_name == None:
			print "  => No work name is specified."
		else:
			print "  => WorkName:", work_name
		print "  => Test OK"
		
	def before_process(self, wm_ctx=None):
		print "  => [before process] CreateDomain "
		print "  => ", wm_ctx
	
	def after_process(self, wm_ctx=None):
		print "  => [after process] CreateDomain "
		print "  => ", wm_ctx
