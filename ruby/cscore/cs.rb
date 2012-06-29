# encoding: utf-8
require 'logger'
require_relative 'core/cscontrol'
require_relative 'core/report'

$log = Logger.new("cs.log")
$log.level = Logger::DEBUG
$log.info("[INFO] CS running debug mode") if CS.debug_mode?

$CS_HOME=File.dirname(__FILE__)
$report = $CS_HOME +'/report.htm'
$conky_report="/tmp/conky_st.data"

## init controller
controller = CS::Controller.new
data_controller = CS::DataController.new
data_controller.verbose= controller.verbose?
cm = CS::ConfigManager.instance

## css setting
cm.set_config_value("css_schema", 'cs.css')
if ARGV.length >= 1
  css_schema = "cs-#{ARGV[0]}.css"
  cm.set_config_value("css_schema", css_schema) if File.exist? ($CS_HOME +"/res/" + css_schema)
end
puts "[INFO] CSS schema: " + cm.get_config_value('css_schema')


## show from browser
def show_more_info_from_browser (browser)
  return if browser.nil?

  cmd =sprintf(browser,File.absolute_path($report))
  puts cmd
  $log.info cmd
  Thread.new(cmd) do |cmd|
    IO.popen(cmd)
    
    while line = gets
        line = line.strip
        if line.eql?('exit') || line.eql?('e') || line.eql?('E')
            exit
        end
        IO.popen(cmd)
        
    end
  end
end

## main loop
show_1st_time = true
loop do
  sleep_time = controller.get_sleep_time
  t = Time.now
  update_time = "UT: "+t.strftime("%H:%M:%S")
  msg = update_time  +"       Interval: " + sleep_time
  begin
    if controller.need_refresh? || show_1st_time
      data_controller.verbose= controller.verbose?

      msg = " [Running]  " + msg

      data_pair = data_controller.refresh_data
      data_pair << OpenStruct.new('id'=>'message', 'value'=>msg)

      report = CS::HtmlReport.new(data_pair,cm, $report)
      report.analyze
      report.generate

      conky_report = CS::ConkyReport.new(data_pair,cm, $conky_report)
      conky_report.analyze
      conky_report.generate
      puts msg

      if(show_1st_time)
        show_more_info_from_browser cm.get_first_config_value('browser')
      end

      show_1st_time = false
    else
      puts " [Closed ] " + msg
      File.delete $conky_report if File.exist? $conky_report
    end
  rescue Exception => e
    puts e
  end
  sleep(sleep_time.to_i)
end



#
#i=0
#loop do
#  i=i +1
#  #system "zenity  --question --title \"Alert\"  --text \"cool!!!!\""
#
#  sleep(2)
#
#  if Thread.current["exit"]
#    break
#  end
#end
