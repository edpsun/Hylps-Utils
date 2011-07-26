# -*- coding: utf-8 -*-

import os
import sqlite3 as sqlite   # 包含sqlite3模块

def get_db_path():
	return "/home/esun/Desktop/sinfoc.db"

class HH:  # 单元测试第一步:　由TestCase派生类
	def CheckDMLdoesAutoCommitBefore(self):
		self.con2 = sqlite.connect(get_db_path(), timeout=0.1) # 连接数据库
		self.con2.isolation_level = None
		self.cur2 = self.con2.cursor() # 获取游标
		self.cur2.execute("select * from stock")
		res = self.cur2.fetchall()
		
		all = []
		for i in res:
			#print i[0], i[2], i[7]
			all.append([i[0], i[2], i[7]])
		self.cur2.close()
		
		print len(all)
		
		self.cur2 = self.con2.cursor() # 获取游标
		iii = 0
		for i in all:
			iii += 1
			print iii, '==========='
			ex = float(i[2].replace('%', ''))
			t = (ex, i[0], i[1])
			print t
			self.cur2.execute("update stock set exchange= ? where id = ? and date = ?", t)

a = HH()
a.CheckDMLdoesAutoCommitBefore()
