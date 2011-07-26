import sys, os
import string

from wm import WorkUnit 
from wm_def import *
from wm_utils import *
from stockinfo import * 
from sqliteman import *
import time

class StockInfoDispatcher(WorkUnit):

	def __init__(self):
		WorkUnit.__init__(self)
		self.sinfoc_home = os.getenv("SINFOC_HOME", None)
		if self.sinfoc_home == None:
			print "[Error] SINFOC_HOME is not defined!"
			sys.exit(-1)
		print "[INFO] SINFOC_HOME:", self.sinfoc_home
		
		
	def processImpl(self, wm_ctx):
		
		self.ctx = wm_ctx
		
		print "[INFO] init the database"
		db_path = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'db_path', None)
		if db_path == None:
			db_path = self.ctx.getPropsHelper().get_property('', 'db_path', 'NOT_Defined')
		
		if (not db_path.startswith("/")):
			db_path = os.path.join(self.sinfoc_home, db_path)
		
		if not os.path.exists(db_path):
			print "[Error] db file not exists.", db_path
			sys.exit(-1)
		print "[INFO] db file: ", db_path  
		sqlman = sqliteman(db_path)
		
		
		print "[INFO] init the stock list."
		stock_list_file = self.ctx.getPropsHelper().get_property(COMMAND_LINE, 'stock_list', None)
		if stock_list_file == None:
			stock_list_file = self.ctx.getPropsHelper().get_property('', 'stock_list', 'NOT_Defined')
		
		if (not stock_list_file.startswith("/")):
			sfile_path = os.path.join(self.sinfoc_home, stock_list_file)
		else:
			sfile_path = stock_list_file
			
		if not os.path.exists(sfile_path):
			print "[Error] list file not exists.", sfile_path
			sys.exit(-1)
		
		stock_list = self.init_stock_list(sfile_path)
		
		
		print "[INFO] get and persist stock information"
		stg = StockGetter(wm_ctx)
		stp = StockPersister(wm_ctx, sqlman)
		
		st_list = []
		p = 0
		for code in stock_list:
			p += 1
			print "Count =>", str(p)
			try:
				st = stg.get_stock(code) 
			except:
				print "sleep 15 sec and retry..."
				time.sleep(15)
				st = stg.get_stock(code)
				
			st_list.append(st)
			st.show_info()
			time.sleep(3)
		
		stp.persist_stocks(st_list)
		
	def init_stock_list(self, file_path):
		print "[INFO] list file: ", file_path  
		file1 = open(file_path)
		lines = file1.readlines()
		list = []
		for l in lines:
			l = l.strip()
			if len(l) == 0 :
				continue;
			
			if len(l) == 7:
				list.append(l[1:])
			else:
				print "Unexpected code length error in stock list file." , l
			 
		file1.close()
		return list
		
	def before_process(self, wm_ctx=None):
		print "  => [before process]"
	
	def after_process(self, wm_ctx=None):
		print "  => [after process]"
