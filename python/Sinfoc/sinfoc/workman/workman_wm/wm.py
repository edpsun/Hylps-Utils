import sys, os
import string
from wm_utils import invoke_if_has 

class WorkUnit:
	def __init__(self):
		pass

	def process(self, ctx=None):
		self._before_process(ctx)
		self.processImpl(ctx)
		self._after_process(ctx)
		
	
	def _before_process(self,ctx=None):
		invoke_if_has(self,'before_process',ctx)
	
	def _after_process(self,ctx=None):
		invoke_if_has(self,'after_process',ctx)
		
	def processImpl(self, ctx=None):
		print "[INFO] Default processImpl() in Dispather. Pls. overwrite it."


