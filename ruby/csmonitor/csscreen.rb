# encoding: utf-8
require 'rbcurse'
require 'rbcurse/rlistbox'
require 'rbcurse/vieditable'


#init_pair(0,  Colour::BLACK,   Colour::BLACK)
#init_pair(1,  Colour::RED,     Colour::BLACK)
#init_pair(2,  Colour::GREEN,   Colour::BLACK)
#init_pair(3,  Colour::YELLOW,  Colour::BLACK)
#init_pair(4,  Colour::BLUE,    Colour::BLACK)
#init_pair(5,  Colour::MAGENTA, Colour::BLACK)
#init_pair(6,  Colour::CYAN,    Colour::BLACK)
#init_pair(7,  Colour::WHITE,   Colour::BLACK)


class RubyCurses::Listbox
  include ViEditable

  dsl_accessor :to_print_borders
  Pattern_For_Nested_Color = /\[%c:(.*)\]/
  UDES_Pattern = /\d(%[A-Z])/

  def print_different_color=(b)
    @print_different_color=b
  end

  def set_list_color(key, values)
    if key == 'up'
      @list_cell_up_color_pair = values
    end

    if key == 'down'
      @list_cell_down_color_pair = values
    end
  end

  def cell_renderer_acc(*val)
    cell_render = nil
    match_indicator = UDES_Pattern.match(val[0])

    color_pair = nil
    if (match_indicator)
      case match_indicator[1]
        when "%D"
          color_pair = @list_cell_down_color_pair if defined? @list_cell_down_color_pair
        when "%U"
          color_pair = @list_cell_up_color_pair if defined? @list_cell_up_color_pair
      end
      val[0].gsub!(match_indicator[1], '%')
    end

    #'OOO s[%c:green]un PPP' => 'OOO sun PPP'
    md = Pattern_For_Nested_Color.match(val[0])
    if md
      bg = (color_pair == nil ? 'BLACK' :color_pair[1])
      color_pair = [md[1], bg]
      val[0].gsub!(Pattern_For_Nested_Color, '')
    end

    if  color_pair.nil?
      return  cell_renderer()
      else
      return RubyCurses::ListCellRenderer.new "", {"color"=>color_pair[0], "bgcolor"=>color_pair[1], \
      "parent" => self, "display_length"=> @width-2-@left_margin}
    end
  end

end


module CS
  class Screen
    Global_Notify_Msg = "q b B d C-L"
    Notify_Msg_Sep_Count = 3

    def initialize (win, verbose=false)
      @window = win
      @form = RubyCurses::Form.new @window

      @notify_msg=''
      @show_st_id=false

      @cols = Ncurses.COLS()
      @lines = Ncurses.LINES()

      @list_box_current_index = -1
      @verbose = verbose

      @cm = CS::ConfigManager.instance

      prepare_ui
    end

    def verbose= v
      @verbose = v
    end

    def prepare_ui
      add_notify_msg_label
      add_toggle_button
      add_list_box
      start_key_handle_thread
    end

    def start_key_handle_thread
      Thread.new(@window, @form, self) do |win, form, screen|
        while ((ch = win.getchar()) != 'q'[0].ord)

          if ch == 'd'[0].ord # shilt + s
            screen.supress_assert_msg
            next
          end

          if ch == 'b'[0].ord || ch == 'B'[0].ord
            screen.show_more_info_from_browser(ch)
            next
          end

          if ch == 12 # C-l
            Thread.new() do
              am = AssertManager.instance
              am.show_log_files
            end
            next
          end

          form.handle_key(ch)
          win.wrefresh
        end

        @window.destroy if !@window.nil?
        VER::stop_ncurses
        exit(0)
      end
    end


    def show_more_info_from_browser (ch)
      id, name = get_select_st_id_name

      if !id || id.length != 8
        return
      end

      url = nil
      if ch == 'b'[0].ord
        url_p = @cm.get_first_config_value('external_info_url')
        url = url_p.gsub(/@URL@/, id) if url_p
      else #M-b
        url_p = @cm.get_first_config_value('local_info_url')

        Dir.foreach(url_p) do |f|
          url = File.join(url_p, f) if f.include? id[2..-1]
        end

      end

      browser = @cm.get_first_config_value('browser')

      return if url.nil? || browser.nil?
      $log.info("Browser #{browser}    URL: #{url}")

      Thread.new(browser, url) do |b, u|
        cmd = "#{b} #{u} "
        IO.popen(cmd)
      end

    end

    def supress_assert_msg
      id, name = get_select_st_id_name

      st = @cm.get_stock(id)
      return if !st

      st.skip_assert = !st.skip_assert

      msg = "#{id} #{name} skip_assert is set to #{st.skip_assert}"
      space_num =@cols - @toggle_button_width * @toggle_button_num -msg.length
      set_notify_msg(msg + ' ' * space_num)
      refresh
    end

    def get_select_st_id_name
      rt_st = @osst_data[@list_box.current_index]
      id = rt_st.id
      name = rt_st.name

      return [id, name]
    end

    def add_notify_msg_label
      @notify_msg = RubyCurses::Variable.new
      @notify_msg.value = "start...."
      @notify_msg_label = RubyCurses::Label.new @form, {'text_variable' => @notify_msg, "name"=>"message_label", \
       "row" => (@lines-1), "col" => 1, "height" => 1, 'color' => 'blue'}
    end

    def add_toggle_button
      @toggle_button_num = 2
      @toggle_button_width =5

      row = @lines-1
      col =@cols - @toggle_button_width

      @tb1 = RubyCurses::ToggleButton.new @form do
        value true
        onvalue "D"
        offvalue "d"
        row row
        col col
        mnemonic 'd'
      end

      col -= @toggle_button_width
      @tb2 = RubyCurses::ToggleButton.new @form do
        value true
        onvalue "S"
        offvalue "s"
        row row
        col col
        mnemonic 's'
      end

      @tb1.command { |form|
        vbs = ENV['cs_verbose']
        if vbs == nil || !CS.to_boolean(vbs)
          ENV['cs_verbose'] = 'true'
        else
          ENV['cs_verbose'] = 'false'
        end

        space_num = col - Global_Notify_Msg.length
        msg = "Set cs_verbose to #{ENV['cs_verbose']}"
        set_notify_msg(msg + ' '*(space_num - msg.length))
      }

      @tb2.command { |form|
        @show_st_id = !@show_st_id
        space_num = col - Global_Notify_Msg.length
        msg = "Set show ID to #{@show_st_id}"
        set_notify_msg(msg + ' '*(space_num - msg.length))
      }
    end

    def get_list_box
      @list_box
    end

    def add_list_box
      # add list box
      data = RubyCurses::Variable.new []
      box_width = @cols
      box_height = @lines - 2
      print_borders = 1
      print_borders = 0 if @verbose

      @list_box = RubyCurses::Listbox.new @form do
        name "datalist"
        row 1
        col 0
        width box_width
        height box_height
        list_variable data
        selection_mode :SINGLE
        show_selector true
        row_selected_symbol ""
        row_unselected_symbol ""
        title_attrib 'reverse'
        cell_editing_allowed false
      end
      @list_box.vieditable_init_listbox
      @list_box.one_key_selection = false
      @list_box.print_different_color=true
      @list_box.to_print_borders print_borders

        # add event
      @list_box.bind(:ENTER_ROW) do |list_box|
        @list_box_current_index = list_box.current_index
          #set_notify_msg "Row # #{list_box.current_index}, #{list_box.list()[list_box.current_index]}"
        msg = "@#{@list_box_current_index} ID: #{@osst_data[@list_box_current_index].id} \Name: #{@osst_data[@list_box_current_index].name}"
        space_num =@cols - @toggle_button_width * @toggle_button_num -msg.length
        set_notify_msg(msg + ' ' * space_num) if @show_st_id
      end

    end

      #def append_data(data=[])
      #  data.each { |d| @list_box.list_data_model.append d }
      #end

    def resize_if_change
      c = @cols
      l = @lines
      @cols = Ncurses.COLS()
      @lines = Ncurses.LINES()

      if (c == @cols && l == @lines)
        return
      else
        #list box
        box_width = @cols
        box_height = @lines - 2
        @list_box.width = box_width
        @list_box.height = box_height

        @notify_msg_label.move((@lines-1), 1)
          #@notify_msg_label.row=(@lines-1)
          #
        @tb1.row=(@lines-1)
        col = @cols - @toggle_button_width
        @tb1.col = col
          #
        @tb2.row=(@lines-1)
        col -= @toggle_button_width
        @tb2.col = col
      end
    end

    def refill_data(data_pair=[])
      @data = data_pair[0]
      @osst_data = data_pair[1]
      resize_if_change

      model = @list_box.list_data_model

        #model.remove_all
        #@data.each { |d| model.append d }

      len = model.length
      @data.each { |d| model.append d }
      (len-1).downto(0) do |v|
        model.delete_at v
      end
    end

    def show_header(h)
      colorlabel = RubyCurses::Label.new @form, {'text' => h, "row" => 0, "col" => 0, "color"=>"BLUE"}
    end

    def append_notify_msg msg
      @notify_msg.value = @notify_msg.value + " " * Notify_Msg_Sep_Count + msg
    end

    def set_notify_msg msg
      @notify_msg.value = Global_Notify_Msg + " " * Notify_Msg_Sep_Count + msg
    end

    def refresh
      @form.repaint
      @window.wrefresh
      Ncurses::Panel.update_panels
    end

  end
end
