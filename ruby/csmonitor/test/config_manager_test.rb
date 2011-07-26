# encoding: utf-8
require 'logger'
require 'rubygems'
gem 'test-unit'
require 'test/unit'

require_relative "../csmanager"
require_relative "../cscontrol"

class ConfigManagerTest < Test::Unit::TestCase

  # Called before every test method runs. Can be used
  # to set up fixture information.
  def setup
    $log = Logger.new("/tmp/view.log")
    $log.level = Logger::INFO

    ENV['cs_debug_mode'] = 'true'
    config =File.join(File.dirname(__FILE__), "test_config.xml")
    @cm = CS::ConfigManager.instance
  end

    # Called after every test method runs. Can be used to tear
    # down fixture information.

  def teardown
    # Do nothing
  end


  def test_get_values
    assert_equal(2, @cm.get_config_values('up').length)
    assert_equal(['GREEN', 'BLACK'], @cm.get_config_values('down'))
    assert_nil @cm.get_config_values('PPPP')
  end

  def test_get_first_value
    assert_equal("100", @cm.get_first_config_value('interval'))
    assert_nil(@cm.get_first_config_value('KKK'))
    assert_nil(@cm.get_first_config_value('PPPP'))
  end

  def test_get_group
    assert_equal("own", @cm.get_group("own").name)
    assert_nil @cm.get_group("own1")

    puts @cm.groups.inspect
    assert_equal 3, @cm.groups.length
    @cm.groups.each { |g| assert_not_nil(@cm.get_group(g)) }
  end

  def test_get_list_in_group
    assert_equal([], @cm.get_list_in_group("hi"))
    assert_equal(10, @cm.get_list_in_group("flag").length)
    assert_equal(9, @cm.get_list_in_group("own").length)

    own_list = @cm.get_list_in_group("own")
    assert_equal "sh600036", own_list[0]
    assert_equal "sh601919", own_list[1]

    assert_equal "sh000001", @cm.get_list_in_group("flag")[0]

    puts "[+] flag list: #{@cm.get_list_in_group("flag")}"
    puts "[+] own  list: #{@cm.get_list_in_group("own")}"
  end

  def test_get_all_stock_os_list
    list = @cm.get_all_stock_os_list
    assert_equal 37, list.length
    assert_equal 'sh000001' , list[0].id
    assert_equal '中国平安' , list[1].name
    assert_equal 'sh600525' , list[-2].id
  end

  def test_get_stock_os_with_conditions
    list = @cm.get_stock_os_with_conditions
    assert_equal 4 , list.size
  end
end