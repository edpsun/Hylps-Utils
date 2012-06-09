function click_on_serial_id(event) {
    var str = 'id:   ' + $(event.target).parent().attr('st_id')+ '\nName: ' + $(event.target).parent().attr('st_name')
    alert(str);
}

function click_on_stock_name(event) {
    var st_id=$(event.target).parent().attr('st_id');
    if (st_id == ''){
        return;
    } 
    window.open("http://stockhtm.finance.qq.com/sstock/ggcx/"+st_id+".shtml", "_blank");
}

$(function(){
    $('.serial_id').attr('style', 'cursor: pointer;').bind("click", click_on_serial_id);
    $('.stock_name').attr('style', 'cursor: pointer;').bind("click", click_on_stock_name);
});
