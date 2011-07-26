class WMContext:
	def __init__(self, ph=None):
		self.props_helper = None
		pass
	
	def setPropsHelper(self, ph):
		self.props_helper = ph
	
	def getPropsHelper(self):
		return self.props_helper