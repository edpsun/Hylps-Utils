# encoding: utf-8
module CS
  class Report

  end

  class HtmlUtils
    HEADER = <<-eos
<html>
<head>
<title>report.html</title>
<meta   http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="res/jquery.min.js"   language="javascript" type="text/javascript"></script>
<script src="res/cs.js"           language="javascript" type="text/javascript"></script>
<meta   http-equiv="refresh"      content="10">
<link   href="res/%s"  type="text/css" rel="stylesheet">
</head>
<body>
    eos

    FOOTER ='</body></html>'
    def HtmlUtils.get_html_header(css)
      sprintf(HEADER,css)
    end

    def HtmlUtils.get_html_footer
      FOOTER
    end
  end

  class HtmlReport < Report
    def initialize (_data, _cm, _f='report.html')
      @data = _data
      @cm = _cm
      @file = _f;
      @st_data_list = ['do analyze first']
    end

    def analyze
      i = 0
      @st_data_list = []
      @st_data_list << get_header
      @data.each do |st|
        i = i+1
        @st_data_list << (format_st_data i, st )
      end
    end

    def get_header
      sprintf(ST_LINE_PATTERN,'st_header','&nbsp;','&nbsp;','st_header', 'id','&nbsp;', :name, :p, '&nbsp;', :pct, :incr, \
      :l, :h, :last, '',:ppct,:extra)
    end

    def generate
      file=File.open(@file,'w')
      file.puts(HtmlUtils.get_html_header(@cm.get_config_value('css_schema')))

      file.puts("   <table>");
      @st_data_list.each do |l|
        file.puts l
      end
      file.puts("   </table>");

      file.puts(HtmlUtils.get_html_footer)
      file.close
    end

    COLUMN_NUM = 12
    ST_LINE_PATTERN = <<"eos"
      <tr id ='%s' st_id='%s' st_name='%s' class='%s'>
        <td class='serial_id'>%s</td>
        <td class='stock_name' title='%s'>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
        <td>%s</td>
      </tr>
eos

    private
    def format_st_data i, os_st
      if os_st.name == "----"
        return "<tr class='st_separator'><td colspan='#{COLUMN_NUM}'>&nbsp</td></tr>"
      end

      if os_st.id == "message"
        return "<tr class='st_message'><td colspan='#{COLUMN_NUM}'>"+os_st.value+"</td></tr>"
      end

      symbol = ''
      indicator=' ' # U up, D down, S stop, E equal
      if os_st.stop
        symbol = "s=="
        indicator = "st_stop"
      elsif os_st.increase == 0
        symbol = "=="
        indicator = "st_stop"
      else
        if os_st.increase > 0
          indicator = "st_up"
        elsif os_st.increase < 0
          indicator = "st_down"
        end
        symbol = ". "* (os_st.incr_pct.to_int + 1)
      end

      if(os_st.stock_config && os_st.stock_config.cost && os_st.stop != true)
        cost = os_st.stock_config.cost.to_f
        cost = ((os_st.price - cost) * 100 /cost).round(2)
        profit_pct = cost.to_s + '%'

        if cost < -8.5 && os_st.stock_config.cost_warn != 'false'
          os_st.has_alert = true
        end
      else
        profit_pct = '&nbsp;'
      end

      if (@verbose || os_st.has_alert)
        symbol = os_st.up == true ? symbol.gsub('.', '↗') :symbol.gsub('.', '↘')
      end

      name = os_st.pyname
      if @verbose
        name = os_st.name
      end

      pct = sprintf("%s%s%", (os_st.increase >= 0 ? '' :'-'), os_st.incr_pct)

      cf_st = @cm.get_stock(os_st.id)

      extra = ''
      extra += "X" if cf_st && cf_st.skip_assert
      if os_st.has_alert
        alert_line_color = @cm.get_first_config_value('alert_line_color') || "BLUE"
        extra += "#{alert_line_color}"
        indicator = "st_alert"
      end
      extra='&nbsp;' if extra==""

      real_id = os_st.id[2..-1]
      str = sprintf(ST_LINE_PATTERN, i ,real_id , os_st.name, indicator, i,real_id, name, os_st.price, os_st.star, pct, os_st.increase, \
      os_st.low, os_st.high, os_st.last_day, symbol,profit_pct,extra)

      str
    end
  end
end