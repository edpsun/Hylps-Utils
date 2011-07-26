#!/usr/bin/env python
"""Text Widget/Automatic scrolling

This example demonstrates how to use the gravity of
GtkTextMarks to keep a text view scrolled to the bottom
while appending text."""

import gobject
import gtk
import sys
import time  
import thread


def update_msg(tv, t=""):
	buf = tv.get_buffer()
	mark = buf.get_mark("scroll")
	itr = buf.get_iter_at_mark(mark)
	buf.set_text(t)

def show_alert():
	gtk.main()
	
def show_widget(widget):
	widget.show()
	
class MsgAlert(gtk.Window):
	def __init__(self, parent=None):
		# Create the toplevel window
		gobject.threads_init()
		gtk.Window.__init__(self)
		try:
			self.set_screen(parent.get_screen())
		except AttributeError:
			self.connect('destroy', lambda * w: gtk.main_quit())

		self.set_title(self.__class__.__name__)
		self.set_default_size(500, 200)
		self.set_border_width(0)
		self.set_deletable(False)

		main_box = gtk.VBox(False, 6)
		self.add(main_box)
		
		vbox = gtk.VBox(False, 0)
		main_box.add(vbox)
		self.textview = self.create_text_view(vbox)
		
		vbox2 = gtk.HBox(False, 0)
		main_box.pack_start(vbox2, False, True)
		main_box.add(vbox2)

		bclose = button = gtk.Button(stock=gtk.STOCK_CLOSE)
		bclose.connect_object("clicked", sys.exit, 0)	
		vbox2.pack_start(bclose, False, True)
		bclose.show()
		
		button = button = gtk.Button(stock=gtk.STOCK_OK)
		button.connect_object("clicked", gtk.Widget.hide, self)	
		vbox2.pack_start(button, True, True)
		button.show()
		
		
		
		vbox.show()
		vbox2.show()
		main_box.show()
		#self.show()
		
	def create_text_view(self, hbox):
		swindow = gtk.ScrolledWindow()
		hbox.pack_start(swindow)
		textview = gtk.TextView()
		swindow.add(textview)
		swindow.show()
		
		buf = textview.get_buffer()
		itr = buf.get_end_iter()
		buf.create_mark("scroll", itr, True)
		buf.insert(itr, "")
		textview.show()
		return textview
	
	def get_text_viewer(self):
		return self.textview

	


#def main():
#	alert = MsgAlert()
#	tv = alert.get_text_viewer()
#	thread.start_new_thread(show_alert, ())
#	
#	time.sleep(5)
#	
#	gobject.idle_add(update_msg, tv, "hello_pig!")
#	
#	time.sleep(5)
#	print 'hi'
#
#if __name__ == '__main__':
#	main()
#	print "can end"
#	time.sleep(15)
