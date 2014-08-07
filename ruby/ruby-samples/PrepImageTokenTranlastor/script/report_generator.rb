require 'csv'

HEADERS = ['Order Type','FnSku','Order Id','ISD','Warehouse','Date','Digital Token','URL']

class ReportGenerator
  def initialize
    @report_file = $options[:report_file]
    @csv = CSV.open(@report_file, mode = "w")
    @csv << HEADERS
  end

  def report(record)
    @csv << [record.order_type,record.fnsku,record.po,record.isd ,record.warehouse ,record.time,record.image_token, record.url]
  end

  def clean
    File.delete(@report_file)
  end

  def close
    @csv.close
  end

end