# encoding: utf-8
require 'rubygems'
gem 'test-unit'
require 'test/unit'

require_relative "../csmanager"

class StockManagerTest < Test::Unit::TestCase

  # Called before every test method runs. Can be used
  # to set up fixture information.
  def setup
    @config =File.join(File.dirname(__FILE__), "EBK")
    @stm = CS::STManager.new @config
    @stm.load_data
  end

    # Called after every test method runs. Can be used to tear
    # down fixture information.

  def teardown
    # Do nothing
  end

  def test_wrong_ebk_dir
    assert_raise(RuntimeError) do
      h = CS::STManager.new "not_exist"
      h.load_data
    end
    assert_nothing_raised(RuntimeError) do
      h = CS::STManager.new @config
      h.load_data
    end
  end

  def test_get_list
    list = @stm.get_stock_list
    assert_equal(27, list.length)

    #puts list.inspect

    exp = "sz002184"
    pp = list.find_index('sz002097')
    puts "=>%d" % pp
    assert_equal(exp,list[pp + 1])
  end

end