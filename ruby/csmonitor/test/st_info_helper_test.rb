# encoding: utf-8
require 'rubygems'
gem 'test-unit'
require 'test/unit'

require_relative "../csmanager"

class STInfoHelperTest < Test::Unit::TestCase

  # Called before every test method runs. Can be used
  # to set up fixture information.
  def setup
     @helper = CS::STInfoHelper.new
  end

    # Called after every test method runs. Can be used to tear
    # down fixture information.

  def teardown
    # Do nothing
  end

    # Fake test
  def test_get_data
    list = ["sz002201", "sh600036","sz000751"]

    data = @helper.request_str_data(list)
    assert_true data.include?('九鼎新材')
    #puts data
  end

  def test_parse_data()
    test_data = <<-END_OF_DOC
    var hq_str_sz002201="九鼎新材,10.70,10.69,10.80,10.93,10.61,10.79,10.80,4930719,53270102.33,18050,10.79,4917,10.78,8974,10.77,3400,10.76,500,10.75,14113,10.80,31750,10.81,15529,10.82,24980,10.83,11360,10.84,2011-07-15,15:06:00";
    var hq_str_sz000751="锌业股份,7.98,8.03,8.00,8.02,7.89,8.00,8.01,14756435,117576157.96,80547,8.00,80251,7.99,93950,7.98,115600,7.97,80600,7.96,135970,8.01,238379,8.02,186050,8.03,111423,8.04,313254,8.05,2011-07-15,15:06:00";
    var hq_str_sh600036="招商银行,12.92,13.00,13.01,13.08,12.90,13.01,13.02,41104105,534507791,56800,13.01,122354,13.00,106225,12.99,262700,12.98,360300,12.97,106445,13.02,344634,13.03,367050,13.04,390277,13.05,142485,13.06,2011-07-15,15:03:07";
    END_OF_DOC

    @helper.parse_data(test_data)
    zs = @helper.get_info_map['sh600036']
    assert_not_nil zs
    assert_equal "招商银行",zs.name
    assert_equal '13.01',zs.price
    assert_equal 0.01,zs.increase
    assert_equal 0.08,zs.incr_pct

  end
end