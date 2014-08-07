require 'ostruct'
require_relative 'token_translator'
require_relative 'report_generator'

KEY_WORDS = ['IMAGE_CAPTURE:']
#PATTERN = Regexp.new('.*Order Type:(.*); FnSku:(.*); PO:(.*); ISD:(.*); Warehouse:(.*); Time:(.*); ImageToken:(.*)$')
PATTERN = Regexp.new(".*#{KEY_WORDS[0]}.*,(.*),(.*),(.*),(.*),(.*),(.*),(.*)$")

class ImageTokenScanner
  def initialize
    $counters.total = 0
    @token_translator = TokenTranslator.new
    @report_generator = ReportGenerator.new
  end

  def scan
    begin
      traverse
      @report_generator.close
    rescue Exception
      STDERR.puts "Failed due to unexpected exception."
      @report_generator.clean
      raise
    end
  end

  def traverse
    File.open($options[:logfile], "r") do |file|
      while line=file.gets
        if line.include?(KEY_WORDS[0])
          record = process_line line
          if record # process image token line record
            $counters.total+=1
            record.url = @token_translator.translate record.image_token

            puts "[INFO] Scanned new record : #{record}" if ($options[:verbose])
            if (!$options[:verbose])
              print "\rProcessed #{' ' * 20} "
              print "\rProcessed #{$counters.total} "
              STDOUT.flush
            end

            @report_generator.report record
            #sleep(0.01)
          end

        end
      end
    end
  end

  def process_line line
    if ($options[:verbose])
      #puts line
    end

    md = PATTERN.match(line)
    record = nil
    if (md)
      record = OpenStruct.new
      record.order_type = md[1].strip
      record.fnsku = md[2].strip
      record.po = md[3].strip
      record.isd = md[4].strip
      record.warehouse = md[5].strip
      record.time = md[6].strip
      record.image_token = md[7].strip
    end
    record
  end
end