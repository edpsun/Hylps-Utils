s = 'OOO s[%c:green]un PPP'

p = /\[%c:(.*)\]/
md = p.match(s)
puts md.inspect
puts md[0], md[1]

if p =~ s
  puts "OK"
  puts s.gsub(p, '')
else
  puts "fail"
end


require "ostruct"

st = OpenStruct.new
st.name = '1.7777777777777'
puts st.send('name')
puts st.send('aa')

st.name = (st.name.to_f.round(2))
puts st.name


puts 1.8888888888888.round(6)


PATTERN_CONDITION_INSTRUCT = /.*@(.*)@(.*)/i
md = PATTERN_CONDITION_INSTRUCT.match('@ONCE@ stock.price > 12.5 && stock.high < 13 ')
puts "%s  |  %s  |  %s" % [md[0], md[1], md[2]]
md = PATTERN_CONDITION_INSTRUCT.match('@onCE@ stock.price > 12.5 && stock.high < 13 ')
puts "-------------------------"
puts "%s  |  %s  |  %s" % [md[0], md[1], md[2]]

md = PATTERN_CONDITION_INSTRUCT.match(' stock.price > 12.5 && stock.high < 13 ')
puts md.nil?


#one_log = File.open("100", "w")
#two_log = File.open("200", "w")

#ret = `yad --title 'MSG!' --text 'Pls. see the msg!' ; echo $?`
#puts "<#{ret}>"
#
#if ret.include?('0')
#  puts "succ"
#else
#  puts "fail"
#end


s = 'sh111111'
puts s[2..-1]
puts "======="




