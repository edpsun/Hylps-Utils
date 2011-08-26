# encoding: utf-8
require 'ncursesw'
require 'logger'
require 'rbcurse'
require 'rbcurse/rwidget'
require_relative 'cscontrol'
require_relative 'csscreen'

begin
  $log = Logger.new("cs.log")
  $log.level = Logger::DEBUG

  $log.info("[INFO] CS running debug mode") if CS.debug_mode?

  VER::start_ncurses
  window = VER::Window.root_window

    # init controller
  controller = CS::Controller.new
  data_controller = CS::DataController.new

  screen = CS::Screen.new(window, controller.verbose?)

    # set color for stock list
  lb = screen.get_list_box
  k = 'up'
  lb.set_list_color(k, controller.color_pair(k))
  k='down'
  lb.set_list_color(k, controller.color_pair(k))

  show_1st_time = true
  loop do
    window.resize
    cols = Ncurses.COLS()
    lines = Ncurses.LINES()

    sleep_time = controller.get_sleep_time
    t = Time.now
    screen.set_notify_msg("UT: "+t.strftime("%H:%M:%S"))
    screen.append_notify_msg("Interval: " + sleep_time)

    if controller.need_refresh? || show_1st_time
      data_controller.verbose= controller.verbose?
      screen.verbose= controller.verbose?

      show_1st_time = false
      screen.show_header(" "+data_controller.get_header())
      data_pair = data_controller.refresh_data
      screen.refill_data(data_pair)

      screen.append_notify_msg("State: Open #{cols}x#{lines}")
      screen.refresh
    else
      screen.append_notify_msg("State: Close")
    end

    sleep(sleep_time.to_i)
  end

rescue => ex
ensure
  begin
    window.destroy if !window.nil?
  rescue
  ensure
    VER::stop_ncurses
  end
  p ex if ex
  p(ex.backtrace.join("\n")) if ex
end
