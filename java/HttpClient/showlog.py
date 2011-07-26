import os, sys
from os import path


if len(sys.argv) != 2:
    print "Usage : ", sys.argv[0], "input_file"
    exit()

input = sys.argv[1]
f = file(input)

map = {}
while True:
    line = f.readline()
    if len(line) == 0: 
        break
    
    p = line.find('top')
    if p == -1:
        continue
    
    key = line[0:p]
    map[key] = line

keys = map.keys()
keys.sort()

for k in keys:
    vs = map[k]
    print vs,

f.close() 
