# -*- coding: utf-8 -*-
import os, sys
from wm_utils import *
from msgparser import *
from datetime import date
from datetime import datetime


class Stock:
	def __init__(self, code):
		self.info = {}
		self.put_info('id', code, u"股票代码")
		
		
	def put_info(self, key, value, comment):
		value = value.replace(u'元', '')
		value = value.replace('%', '')
		self.info[key] = (value, comment)
		
	def get_info(self, key, default=None):
		return self.info.get(key, (default, ''))[0]
	
	def show_info(self):
		for k, v in self.info.items():
			print "  =>",v[1].encode("utf-8"), "[", k, "]", v[0].encode("utf-8")

class StockGetter:
	def __init__(self, wctx):
		self.ctx = wctx
		
	def get_stock(self, code):
		print "[INOF] get stock info: ", code
		url_p = self.ctx.getPropsHelper().get_property('', 'stock_url', 'NOT Defined')
		url = url_p.replace('SID', code)
		print url
		
		response = http_send(url)	
		data = response.read()
		
		parser = MsgParser(wcn_sn())
		parser.feed(data.decode('utf-8'))
		parser.close()
		info = parser.get_info()
		
		st = Stock(code)
		for item in info:
			st.put_info(item[1], item[2], item[0])
		
		cur_date = datetime.now(ZhCNTzinfo())
		update_t = st.get_info('update_time', None)
		if(update_t == None):
			sdate = str(cur_date.date())
			print 'Use default date value.'
		else:
			sdate = str(cur_date.year) + "-" + update_t.split(' ') [0]
		
		st.put_info('date', sdate, u'交易日期')
		
		return st
	

stock_table = "stock"
sql_delete = 'delete from ' + stock_table + ' where id = ? and date = ?'
sql_insert = 'insert into ' + stock_table + ' values (:id,:name,:date,:price,:pe, :pbv,:increase,:exchange,:volumn,:col1,:col2)'

class StockPersister:
	def __init__(self, wctx, sqlman):
		self.ctx = wctx
		self.sqlman = sqlman
		
	def persist_stocks(self, stock_list):
		conn = self.sqlman.get_conn()
		conn.isolation_level = None
		cur = conn.cursor()
		
		p = 0;
		for st in stock_list:
			p += 1
			print "Count =>", str(p)
			self.persist_stock(st, cur)

		cur.close()
		conn.close()
		
	def persist_stock(self, stock, cur):
		cur.execute(sql_delete, (stock.get_info('id', ''), stock.get_info('date', '')))
		
		smap = {}
		colnames = ('id', 'name', 'date', 'price', 'pe', 'pbv', 'increase', 'exchange', 'volumn', 'col1', 'col2')
		numcols = ('pe', 'pbv','exchange')
		for col in colnames:
			
			value = stock.get_info(col, 'No')
			print "---->",col,value
			if numcols.count(col) > 0:
				try:
					value = float(value)
				except ValueError , e :
					value = 0.0911
					print e
			smap[col] = value
		
		print "[INFO] insert into db.", stock.get_info('id', ''), stock.get_info('date', ''), stock.get_info('namle', '')
		cur.execute(sql_insert, smap)	
		
