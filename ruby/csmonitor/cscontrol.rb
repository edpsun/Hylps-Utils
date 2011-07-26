# encoding: utf-8
require_relative "csmanager"

module CS
  def CS.to_boolean(string)
    return true if string== true || string =~ (/(true|t|yes|y|1)$/i)
    return false if string== false || string.nil? || string =~ (/(false|f|no|n|0)$/i)
    raise ArgumentError.new("invalid value for Boolean : #{string}")
  end

  def CS.debug_mode?
    env_dm = ENV['cs_debug_mode']
    env_dm = 'false' if !env_dm
    CS.to_boolean(env_dm)
  end

  class Controller
    def initialize
      @cm = CS::ConfigManager.instance

      now = Time.now
      now_a = now.to_a
      @m_start = Time.local(now_a[5], now_a[4], now_a[3], 9, 25)
      @m_close = Time.local(now_a[5], now_a[4], now_a[3], 11, 32)
      @a_start = Time.local(now_a[5], now_a[4], now_a[3], 12, 59)
      @a_close = Time.local(now_a[5], now_a[4], now_a[3], 15, 02)
    end

    def need_refresh?
      return true if CS.debug_mode?

      now = Time.now

      if now.sunday? || now.saturday?
        return false
      end


      if  (@m_start <=> now) <= 0 && (now <=> @m_close) <= 0
        return true;
      end

      if  (@a_start <=> now) <= 0 && (now <=> @a_close) <= 0
        return true;
      end

      return false

    end

    def verbose?
      env_vm = ENV['cs_verbose']
      if (env_vm)
        CS.to_boolean(env_vm)
      else
        CS.to_boolean @cm.get_first_config_value('verbose')
      end
    end

    def color_pair(key)
      env_vm = ENV['cs_theme']

      if env_vm && env_vm == "hot" && key == "up"
        return ["RED", "BLACK"]
      else
        return @cm.get_config_values(key)
      end

    end

    def get_sleep_time
      @cm.get_first_config_value('interval')
    end
  end
end
