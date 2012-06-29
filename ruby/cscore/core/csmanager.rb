# encoding: utf-8
require 'rexml/document'
require 'ostruct'
require_relative "pinyin"
require "singleton"

module CS
  def CS.get_ebk_dir
    dir = ''
    if CS.debug_mode?
      dir = File.join(File.dirname(__FILE__), "test/EBK")
    else
      dir = File.join(File.dirname(__FILE__), "conf/EBK")
    end

    $log.info('[INFO] using EBK Path. ' + dir)
    return dir
  end

  def CS.get_config_file_path
    config_file = '';
    if CS.debug_mode?
      config_file = File.join(File.dirname(__FILE__), "test/config.xml")
    else
      config_file = File.join(File.dirname(__FILE__), "conf/config.xml")
    end

    puts('[INFO] using config file. ' + config_file)
    return config_file
  end

  class ConfigManager
    include Singleton
    attr_reader :groups

    def initialize ()
      @config_file = CS.get_config_file_path
      @config_map = Hash.new
      @groups = Array.new
      @groups_map = Hash.new
      @stock_map = Hash.new

      load_config
    end

    def get_config_value(key)
      @config_map[key] if !@config_map[key].nil?
    end

    def get_config_values(key)
      @config_map[key].values if !@config_map[key].nil?
    end

    def get_first_config_value(key)
      @config_map[key].values[0] if @config_map[key] && @config_map[key].values.length > 0
    end

    def get_group(name)
      @groups_map[name]
    end

    def get_list_in_group(group)
      return [] if !@groups_map[group]
      @groups_map[group].stocks.collect() { |st| st.id }
    end

    def get_stock id
      @stock_map[id]
    end

    def get_all_stock_id_list
      a = []
      @groups.each do |grp|
        a.push(get_list_in_group(grp))
      end
      a
    end

    def get_all_stock_os_list
      a = []
      @groups.each do |grp|
        a.push(@groups_map[grp].stocks)
      end
      a.flatten
    end

    def get_stock_os_with_conditions
      a = get_all_stock_os_list.reject do |st|
        st.conditions.nil? || st.conditions.size == 0
      end
      a
    end

    def set_config_value(name,value)
      @config_map[name] = value
    end

      #----------------------------------------------------------------------------------------
    private
    def load_config
      raise "[ERROR] config file not exists. "+ @config_file if !File.exist?(@config_file)

      input = File.new(@config_file)
      doc = REXML::Document.new(input)
      root = doc.root
      @version = root.elements[1].text

      doc.elements.each("cs/config/param") { |e| parse_config_param e }
      doc.elements.each("cs/watch/group") { |e| parse_watch_group e }

    ensure
      begin
        input.close
      rescue
      end
    end

    def parse_config_param(node)
      param = OpenStruct.new
      param.name = node.elements['name'].text

      values = []
      node.elements.each("value") { |e|
        values << e.text
      }
      param.values=values
      param.desc = (node.elements['desc'] == nil ? nil :node.elements['desc'].text)
      @config_map[param.name] = param
    end

    def parse_watch_group(node)
      name = node.elements['name'].text
      desc = (node.elements['desc'] == nil ? nil :node.elements['desc'].text)

      group = OpenStruct.new
      group.name = name
      group.desc = desc

      @groups << name
      @groups_map[name] = group

      group.stocks = []
      node.elements.each("stock") do |st|
        stock = OpenStruct.new
        stock.id = st.elements['id'].text

        raise "[ERROR] Stock id in config xml file is incorrect :#{stock.id} " if stock.id.length != 8

        stock.name = st.elements['name'].text

        stock.desc = (st.elements['desc'] == nil ? nil :st.elements['desc'].text)
        stock.star = (st.elements['star'] == nil ? nil :st.elements['star'].text)
        stock.cost = (st.elements['cost'] == nil ? nil :st.elements['cost'].text)
        stock.cost_warn = (st.elements['cost_warn'] == nil ? nil :st.elements['cost_warn'].text)


        conditions = []
        st.elements.each("condition") { |e|
          conditions << e.text
        }
        stock.conditions = conditions

        group.stocks << stock
        @stock_map[stock.id] = stock
      end
    end
  end

    #-------------------------------------------------------------------------------------------------
  class STManager
    def initialize ebk
      @ebk_dir = ebk
      @st_list = Array.new
    end

    def load_data
      raise "[ERROR] #{@ebk_dir} NOT exist!" if !File.directory?(@ebk_dir)

      @ebks = Dir.entries(@ebk_dir).reject do |f|
        File.extname(f).downcase != '.ebk' || !File.file?(File.join(@ebk_dir, f))
      end
      @ebks = @ebks.collect { |f| File.join(@ebk_dir, f) }

      @ebks.each do |ebk|
        File.open(ebk) do |file|
          file.each_line do |line|
            line = line.strip
            next if !line || line.length != 7 ||line[0] == "#"

            id = "sh" + line.slice(1..-1) if line[0] == "1"
            id = "sz" + line.slice(1..-1) if line[0] == "0"

            @st_list << id
          end
        end
      end

    end

    def get_stock_list
      @st_list
    end
  end

    #-------------------------------------------------------------------------------------------------
  class STInfoHelper

    Field_Mapping = {
        'name' => [0, 'String'],
        'open' => [1, 'Float'],
        'last_day' => [2, 'Float'],
        'price' => [3, 'Float'],
        'high' => [4, 'Float'],
        'low' => [5, 'Float'],
        'buy' => [6, 'Float'],
        'sell' =>[7, 'Float'],
        'date' => [30, 'String'],
        'time' => [31, 'String'],
    }

    def initialize
      @url_pattern = "http://hq.sinajs.cn/list="
      @info_map = Hash.new
      @pinyin = PinYin.instance
    end

    def retrieve_info(list)
      data = request_str_data list
      parse_data data
      @info_map
    end

    def get_info_map
      @info_map
    end

    def request_str_data(list)
      require 'net/http'
      #require 'iconv'

      url = @url_pattern + list.join(',')
      $log.debug(url)

      proxy = ENV['http_proxy'] ? URI.parse(ENV['http_proxy']) : nil
      Net::HTTP.version_1_2

      ret_data =''
      begin
        @retry = true
        if proxy
          $log.debug("Using proxy: %s " % proxy)
          ret_data = Net::HTTP::Proxy(proxy.host, proxy.port, proxy.user, proxy.password).get(URI.parse(URI.escape(url)))
        else
          $log.debug("NOT using proxy!")
          ret_data = Net::HTTP.get(URI.parse(URI.escape(url)))
        end

      rescue TimeoutError
        if @retry then
          @retry = false
          retry
        else
        raise
        end
      end

      return ret_data.encode(Encoding.find("UTF-8"),Encoding.find("GBK")) #Iconv.conv("UTF-8", "GBK", ret_data)
    end

    def parse_data(data)
      data.split(';').each do |str|
        stock = OpenStruct.new
        md = str.scan(/.*var hq_str_(.*)="(.*)"/)
        next if md.length == 0 || md[0].length ==0

        id = md[0][0]
        infos = md[0][1].split(',')

        stock.id = id
        stock.name = infos[0]
        stock.pyname = @pinyin.to_pinyin(stock.name, separator = '.')
        stock.abbrname=@pinyin.to_pinyin_abbr(stock.name)
          #stock.infos = infos

        Field_Mapping.each do |k, vs|
          stock.new_ostruct_member(k)
          p_index = vs[0]
          p_vt = vs[1]
          p_v = infos[p_index]
          p_v = p_v.to_f.round(2) if p_vt == 'Float'
          stock.send(k+"=", p_v)
        end

        calculate(stock)
        @info_map[id] = stock
      end

    end

    private
    def calculate(stock)

      if stock.price == 0.00
        stock.increase = 0
        stock.incr_pct = 0
        stock.up = false
        stock.stop = true
      else
        increase = (stock.price - stock.last_day).round(2)
        increase_pct = (increase * 100 / stock.last_day.to_f).round(2)
        stock.increase = increase
        stock.incr_pct = increase_pct.abs
        increase > 0 ? stock.up = true : stock.up=false
        stock.stop = false
      end

        # hack the name with 3 chinese charater
      stock.name.gsub!(/ /, '') if stock.name.include? ' '
      if stock.name.length == 3
        stock.name = stock.name + "　"
      end
    end
  end

  class DataController
    #serial,name,price,ipct,increase,low,high,last_day,symbol
    ST_LINE_PATTERN = "%3s %-25s %-10s %3s %-10s %-10s %-10s %-10s %-10s %-11s %+7s"
    PATTERN_CONDITION_INSTRUCT = /.*@(.*)@(.*)/i

    def initialize
      @verbose = true

      ebk_dir = CS.get_ebk_dir
      @stm = CS::STManager.new ebk_dir
      @stm.load_data

      @st_info_helper = CS::STInfoHelper.new
      @cm = CS::ConfigManager.instance
      @alm = AssertManager.instance
    end

    def verbose= v
      @verbose = v
    end

    def get_header
      if @verbose
        sprintf(ST_LINE_PATTERN, "id", "　　name　　", :price, '*', :ipct, :increase, :low, :high, :last_day, "+/-",'')
      else
        sprintf(ST_LINE_PATTERN, "", :n, :p, '*', :ipct, :is, :l, :h, :last, "+/-",'')
      end
    end

    def refresh_data
      #data = []
      osst_data = []

        # get id list from ebk + config
      list = @stm.get_stock_list
      list.unshift(@cm.get_all_stock_id_list)
      list.flatten!.uniq!

        # download info
      info_map = @st_info_helper.retrieve_info(list)
      raise "[ERROR] Can not get stock info. Please check netowrk or proxy." if info_map.size == 0

      info_list = get_osst_info_list(info_map)

      @alm.clear_to_reuse # clear normal messages
      handle_assert_info(info_map)

        #generate data
      i = 0
      info_list.each do |sublist|
        stop_a = Array.new
        stop_osst_a = Array.new

        sublist.each do |st|
          i = i +1

          cf_st = @cm.get_stock (st.id)
          st.stock_config = cf_st if cf_st

          #str = format_st_data(i, st)

          if st.stop
            #stop_a << str
            stop_osst_a << st
          else
            #data << str
            osst_data << st
          end
        end
        #data.push(*stop_a)
        osst_data.push(*stop_osst_a)

        #data << "----"
        osst_data << OpenStruct.new('id'=>'----', 'name'=>'----')
      end
      #data.pop
      osst_data.pop
      osst_data
    end

    private
    def handle_assert_info (info_map)
      os_list = @cm.get_stock_os_with_conditions

      os_list.each do |cf_st|
        next if cf_st.skip_assert == true
        next if !cf_st.conditions || cf_st.conditions == 0

        rt_st = info_map[cf_st.id]

        cf_st.conditions.each do |c|
          instruction = nil
          condition = c
          md = PATTERN_CONDITION_INSTRUCT.match(c)
          if md
            instruction = md[1]
            condition = md[2]
          end

          ret = eval_condition(rt_st, condition)
            #$log.info("#{rt_st.id} #{rt_st.name} [#{ret}]: [#{instruction}] #{condition}")

          if ret
            @alm.add_assert(rt_st.id, rt_st.name, instruction, condition)
            rt_st.has_alert = true if instruction.nil? && rt_st
          end
        end

      end

    end

    def eval_condition stock, condition
      if stock.stop
        return false
      end

      begin
        return eval(condition)
      rescue
        $log.error("Can not execute condition. %s %s %s" % [stock.id, stock.name, condition])
      ensure

      end

      return false
    end

    def format_st_data i, os_st
      symbol = ''
      indicator=' ' # U up, D down, S stop, E equal
      if os_st.stop
        symbol = "s=="
        indicator = "S"
      elsif os_st.increase == 0
        symbol = "=="
        indicator = "E"
      else
        if os_st.increase > 0
          indicator = "U"
        elsif os_st.increase < 0
          indicator = "D"
        end
        symbol = "."* (os_st.incr_pct.to_int + 1)
      end

      if(os_st.stock_config && os_st.stock_config.cost && os_st.stop != true)
        cost = os_st.stock_config.cost.to_f
        cost = ((os_st.price - cost) * 100 /cost).round(2)
        profit_pct = cost.to_s + '%'

        if cost < -8.5 && os_st.stock_config.cost_warn != 'false'
           os_st.has_alert = true
        end
      else
        profit_pct = ''
      end

      if (@verbose || os_st.has_alert)
        symbol = os_st.up == true ? symbol.gsub('.', '↗') :symbol.gsub('.', '↘')
      end

      name = os_st.pyname
      if @verbose
        name = os_st.name
      end

      pct = sprintf("%s%s%%%s", (os_st.increase >= 0 ? '' :'-'), os_st.incr_pct, indicator)
      str = sprintf(ST_LINE_PATTERN, i, name, os_st.price, os_st.star, pct, os_st.increase, \
      os_st.low, os_st.high, os_st.last_day, symbol,profit_pct)

      cf_st = @cm.get_stock(os_st.id)

      str += "X" if cf_st && cf_st.skip_assert
      str += " ONCE" if @alm.has_once_true_assert? os_st.id

      if os_st.has_alert
        alert_line_color = @cm.get_first_config_value('alert_line_color') || "BLUE"
        str += "[%c:#{alert_line_color}]"
      end

      str
    end

    def get_osst_info_list info_map
      tmp = {}
      all_st_info_list = []

        # add stock in watch list
      @cm.groups.each do |grp|
        group_list = @cm.get_list_in_group(grp)

        group_st_info_list = Array.new
        group_list.each do |st_id|
          tmp[st_id] = ''

          raise "[ERROR] %s is not in the EBK file. " % st_id if !info_map[st_id]

          add_st_to_array(group_st_info_list, info_map[st_id])
        end

        all_st_info_list.push(group_st_info_list) if group_st_info_list.length > 0
      end

        # add the rest stock
      rest_st_info_list = Array.new
      @stm.get_stock_list.each do |st|
        next if tmp[st]
        rest_st_info_list << info_map[st] if info_map[st]
      end

      all_st_info_list.push(rest_st_info_list) if rest_st_info_list.length > 0
      all_st_info_list
    end

    def add_st_to_array (list, stock)
      stock.star = is_stock_star(stock.id)

      if stock.star
        list.unshift stock
      else
        list.push stock
      end
    end

    def is_stock_star (id)
      @cm.get_stock(id).star
    end
  end

    #-------------------------------------------------------------------------------------------------
  class AssertManager
    include Singleton

    def initialize
      @once_log = File.open("log_once_assert", "w")
      @normal_log = File.open("log_normal_assert", "w")
      @assert_map = {}
      @cm = CS::ConfigManager.instance
      start_daemon_thread
    end

    def start_daemon_thread
      @notify_thd = Thread.new(self) do |alm|
        loop do
          if Thread.current["new_assert"]
            ret = `zenity  --question --title 'Alert'  --text 'Pls. see the msg!' ; echo $?`
            if ret.include?('0')
              Thread.new() do
                alm.show_log_files
              end
            end

            Thread.current["new_assert"] = false

          else
            sleep(5)
          end

          if Thread.current["exit"]
            break
          end

        end
      end
    end

    def show_log_files
      editor = @cm.get_first_config_value('editor') || "/usr/bin/gedit"
      cmd = "#{editor}  #{@normal_log.path} #{@once_log.path} "
      IO.popen(cmd)
    end

    def clear_to_reuse
      @assert_map.each_value { |st| st.normal = nil }
    end

    def add_assert(id, name, instruction, condition)
      @assert_map[id] ||= OpenStruct.new(:id => id, :name => name)
      st = @assert_map[id]

      if (instruction && 'once'.casecmp(instruction) == 0)
        st.once_map ||= {}

        if !st.once_map.include?(condition)
          st.once_map[condition] = ''
          @once_log.printf("[True] %8s %8s %5s => %s \n", id, name, instruction, condition)
          @once_log.flush
        end
      else
        (st.normal ||= []) << condition
        @normal_log.printf("[True] %8s %8s %5s => %s\n", id, name, instruction, condition)
        @normal_log.flush

        @notify_thd["new_assert"] = true
      end
    end

    def has_once_true_assert?(id)
      if @assert_map[id] && @assert_map[id].once_map && (@assert_map[id].once_map).size > 0
        return true
      else
        return false
      end

    end
  end

end # end of module
