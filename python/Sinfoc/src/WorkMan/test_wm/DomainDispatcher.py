import sys, os
import string

from wm import Dispatcher 

class DomainDispatcher(Dispatcher):

	def __init__(self):
		Dispatcher.__init__(self)

	def process(self):
		print "domain dis"
