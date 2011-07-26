import os
import sys
import sqlite3 as sqlite

class sqliteman:
	def __init__(self, db_path):
		self.db_path = db_path
		
	def get_db_path(self):
		return self.db_path
		
	def get_conn(self, time_out=0.1):
		return sqlite.connect(self.get_db_path(), timeout=time_out)
		
	
