############################################################################
# Prep Image Token Scan Script.
# This script is used to translate the image tokens scanned from log file
# into corresponding url. Currently it only supports Beta/Prod for NA realm.
# If there is anything wrong, feel free to contact me sunp@amazon.com.
# Author: Edward Sun
############################################################################

require 'optparse'
require 'ostruct'

unless Kernel.respond_to?(:require_relative)
  module Kernel
    def require_relative(path)
      require File.join(File.dirname(caller[0]), path.to_str)
    end
  end
end

require_relative 'image_token_scanner'


# parse options
$options = {}
_opts = nil
support_domains = 'test|prod'
option_parser = OptionParser.new do |opts|
  _opts = opts
  opts.banner = "Prep Image Token Translator."

  $options[:verbose] = false
  opts.on('-v', '--verbose', 'Turn on verbose output') do
    $options[:verbose] = true
  end

  opts.on('-l log file', '--logfile log file', 'Log file path to be scanned') do |value|
    $options[:logfile] = value
  end

  $options[:domain] = 'test'
  opts.on("-d #{support_domains}", "--domain #{support_domains}", 'Available domains are test for Devo and prod for Prod') do |value|
    $options[:domain] = value
  end

  opts.on('-h', '--help', 'Display this usage') do
    puts opts
    exit 0
  end
end.parse!

$options[:realm] = 'NA'

# validate the input
if (!$options[:logfile])
  puts _opts
  puts '[ERROR] No log file is specified!'
  exit -1
end

if (!support_domains.include?($options[:domain]))
  puts _opts
  puts "[ERROR] Invalid domain is specified: #{$options[:domain]}. Supported domains: #{support_domains} "
  exit -1
end

if (!File.exists? $options[:logfile])
  puts "[ERROR] Log file specified is not existing : #{$options[:logfile]} ."
  exit -1
end

$host_config =  { 'NA/test' => 'cas-integ-na.integ.amazon.com', 'NA/prod' => 'cas-prod-na.amazon.com' }
$options[:host] = $host_config["#{$options[:realm]}/#{$options[:domain]}"]
$options[:report_file] = Time.now.strftime("report-%Y-%m-%d_%H-%M-%S.csv")
$counters = OpenStruct.new


puts "[INFO] Domain      : #{$options[:domain]}"
puts "[INFO] Realm       : #{$options[:realm]}"
puts "[INFO] Logfile     : #{$options[:logfile]}"
puts "[INFO] Service Host: https://#{$options[:host]}"
puts "*************************************************************************"

# start to work
scanner = ImageTokenScanner.new()
scanner.scan

puts "\n[INFO] Log file has been scanned."
puts "*************************************************************************"
puts "  - Total       : #{$counters.total}"
puts "  - Failure(s)  : #{$counters.failures}"
puts "  - Report File : #{$options[:report_file]}"