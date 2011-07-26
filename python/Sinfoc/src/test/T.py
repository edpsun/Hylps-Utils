import sys
def print_char(char, count):
	for i in range(count):
		sys.stdout.write(char)
	
def print_str_center(s, count):
	c = (count - len(s)) / 2
	if(c > 0):
		print_char(' ', c)
		
	c2 = count - c - len(s)
	sys.stdout.write(s)
	
	if(c2 > 0):
		print_char(' ', c2)

def print_str_left(s, count):
	sys.stdout.write(s)
	c = count - len(s)
	if (c > 0):
		print_char(' ', c)
	
def print_test_line(test, isSameAsPrevious="false"):
	global line_num, header
	
	if(isSameAsPrevious == "false"):
		line_num += 1
		print_str_left(str(line_num), header[0][1])
	else:
		print_str_left("", header[0][1])

	for i in range(len(test)):
		sys.stdout.write(' ')
		print_str_left (test[i], header[i + 1][1])
	print ""


# print header ==============================================================
line_num = 0
header = []
header.append(('ID', 4))
header.append(('TIER', 30))
header.append(('DB', 40))
header.append(('DESC', 40))

total = 0
total += len(header) - 1
for (k, v) in header:
	total += v

print_char('-', total)
print ""

for (k, v) in header:
	print_str_center(k, v)
	if(k != "DESC"):
		sys.stdout.write('|')
print ""


# print content ==============================================================
test_list = []


file1 = open('conf')
lines = file1.readlines()
for line in lines:
	line = line.replace(",", "|")
	a = line.split('|')
	a = [ o.strip() for o in a ]
	test_list.append(a)

for i in range(len(test_list)):
	isSameAsPrevious = "false"
	if (test_list[i][0] == ""):
		isSameAsPrevious = "true"
		
	if (isSameAsPrevious=="true"):
		pass
	else:
		print_char('-', total)
		print ""
		
	print_test_line (test_list[i], isSameAsPrevious)
	
print_char('-', total)
print ""