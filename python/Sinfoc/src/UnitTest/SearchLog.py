
def processLine(line):
    global skip_c , skip_s ,currentT
    if line.find('[testlogic] testClientSide:') >-1:
        skip_c+=1
        currentT='testClientSide'
    if line.find('[testlogic] testServerSide:') >-1:
        skip_s+=1
        currentT='testServerSide'

    if line.find('50% of')>-1:
        processData (line,0)
    if line.find('100% of',1)>-1:
        processData (line,1)

    if line.find('] FAILURE') > -1 or line.find('] SUCCESS') > -1:
        line=line.replace("tests.unit.jdbc.jdbc40.", "")
        line=line.replace("[testlogic] ", "[case] ")
        i = line.find('took ')
        line=line[0:i] +"\n"

    if(currentT == 'testServerSide'):
        outfile_s.write(line)
    elif (currentT == 'testClientSide'):
        outfile_c.write(line)

def processData(line,isNeedCac):
    stra = line.split()
    i = stra.index('Pass:')
    a=stra[i:]
    p=100*(float(a[1]))/(int(a[1])+int(a[3])+int(a[5])+int(a[7]))
    p=round(p,2)
    a.append(' ('+str(p)+"%)")
    s1 = " ".join(a)

    global tempvalue, currentT
    if(isNeedCac==0):
        tempvalue=a
        outfile.write('['+currentT+']\n')
        outfile.write(s1+"\n\n")
    else:
        global tempvalue2
        p=100*(float(a[1])-float(tempvalue[1]))/(int(a[1])-int(tempvalue[1]) +int(a[3])-int(tempvalue[3]) +int(a[5])-int(tempvalue[5]) +int(a[7])-int(tempvalue[7]))
        p=round(p,2)
        tempvalue2[1]= str(int(a[1])-int(tempvalue[1]))
        tempvalue2[3]= str(int(a[3])-int(tempvalue[3]))
        tempvalue2[5]= str(int(a[5])-int(tempvalue[5]))
        tempvalue2[7]= str(int(a[7])-int(tempvalue[7]))
        tempvalue2.append(' ('+str(p)+"%)")
        s2 = " ".join(tempvalue2)
        outfile.write('['+currentT+']\n')
        outfile.write(s2+"\n\n")
        outfile.write('[Total]\n')
        outfile.write(s1+"\n\n")


configFile='log_all'
log_s='log_s.txt'
log_c='log_c.txt'
outfileName='report.txt'

skip_c=0
skip_s=0
currentT='aa'
tempvalue=['Pass:', '0', 'Fail:', '0', 'Skip:', '0', 'Error:', '0']
tempvalue2=['Pass:', '0', 'Fail:', '0', 'Skip:', '0', 'Error:', '0']

file1 = open(configFile)
outfile = open(outfileName, 'w')
outfile_s = open(log_s, 'w')
outfile_c = open(log_c, 'w')

outfile.write("Test Report.\n================================\n")

lines = file1.readlines()
for line in lines:
    processLine(line)


file1.close()
outfile.close()
outfile_s.close()
outfile_c.close()

