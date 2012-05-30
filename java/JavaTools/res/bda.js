	function toggleDisplay(divName) { 
		var divD = document.getElementById(divName); 
		if(divD.style.display=="none") { 
			divD.style.display = "block"; 
		} else { 
			divD.style.display = "none"; 
		} 
	}
	
	function resetFilter(){
		$('#listdiv').hide();
		$(':input[type=text]').val('');
		$("#filterButton").click();
	}
	
	var total = 0;
	var exportList="";
	var markList="[MARK]<br>";
	var tipList="[TIP]<br>";
	function doFilter(){
		$('#listdiv').hide();
		stockid = $(':input[name=stockid]').val();
		tagstr = $(':input[name=tags]').val();
		change_rate_min = $(':input[name=change_rate_min]').val();
		change_rate_max =$(':input[name=change_rate_max]').val();
		qnum_min = $(':input[name=qnum_min]').val();
		qnum_max = $(':input[name=qnum_max]').val();
		
		fdata= {stockid:stockid,tagstr:tagstr,change_rate_min:change_rate_min,
				change_rate_max:change_rate_max,qnum_min:qnum_min,qnum_max:qnum_max};
		
		total = 0;
		exportList="";
		markList = "[MARK]<br>";
		tipList="[TIP]<br>";
		$('.stdiv').each(function(){
		  filterDiv($(this),fdata);
		});
		$('#total').text("Current Total:"+total);
	}
	
	function filterDiv(div, fdata){
		stockid = div.attr('stockid');
		show_by_id=true;
		if(fdata.stockid){
		  if(fdata.stockid.indexOf(stockid) > -1 || stockid.indexOf(fdata.stockid) > -1){
		    show_by_id=true;
		  }else{
		    show_by_id=false;
		  }
		}
		//tags
		show_by_tag=false;
		if(jQuery.trim(fdata.tagstr) == "" || jQuery.trim(fdata.tagstr) == ","){
		  show_by_tag=true;
		}else{
		  tags = fdata.tagstr.split(",");
		  st_tags = div.attr('tags');
		  
		  for(var i=0;i<tags.length;i++)
		  {
		    str = jQuery.trim(tags[i]);
		    if(str.length == 0){
		      continue;
		    }else{
		      if(st_tags.indexOf(str) > -1){
			show_by_tag=true;
		      }else{
			show_by_tag=false;
		      }
		    }
		  }
		}
		
		//rate
		rate = div.attr('hCRate');
		qnum = div.attr('hQnum');
		
		show_by_rate_min=true;
		if(fdata.change_rate_min){
		  if(parseFloat(rate) >= parseFloat(fdata.change_rate_min)){
		    show_by_rate_min=true;
		  }else{
		    show_by_rate_min=false;
		  }
		}
		
		
		show_by_rate_max = true;
		if(fdata.change_rate_max){ 
		  if(parseFloat(rate) <= parseFloat(fdata.change_rate_max)){
		    show_by_rate_max=true;
		  }else{
		    show_by_rate_max=false;
		  }
		}
		
		show_by_qnum_min=true;
		if(fdata.qnum_min){
		  if(parseInt(qnum) >= parseInt(fdata.qnum_min)){
		    show_by_qnum_min=true;
		  }else{
		    show_by_qnum_min=false;
		  }
		}
		
		show_by_qnum_max=true;
		if(fdata.qnum_max){
		  if(parseInt(qnum) <= parseInt(fdata.qnum_max)){
		    show_by_qnum_max=true;
		  }else{
		    show_by_qnum_max=false;
		  }
		}
		//alert(show_by_tag +"::::"+ show_by_rate_min +"::::"+ show_by_rate_max +"::::"+ show_by_qnum_min +"::::"+ show_by_qnum_max);
		if(show_by_id && show_by_tag && show_by_rate_min && show_by_rate_max && show_by_qnum_min && show_by_qnum_max){
		  elid=div.attr('elid');
		  div.show();
		  total = total +1;
		  exportList=exportList + elid +'<br>';
		
		  markList = markList +"0"+ elid +'=7<br>';
		  tipList = tipList +"0"+ elid +'=Tags:' + div.attr('tags') +"#$hCRate:" + rate +"#$hQnum:" + qnum +"<br>";
		}else{
		  div.hide();
		}
	}
	
	function toggleStockList(event){
		$('#listdiv').html(exportList +"<br> <br> <br> <br> " + markList +"<br><br>" + tipList);
		$('#listdiv').toggle();
	}
	
	function showAllPic(){
		if($('.st_pic_div').length>0){
			$('.st_pic_div').each(function(){
			 	$(this).toggle();
			});
		}else{
			$('.stdiv').each(function(){
			  showPic($(this));
			});
		}
	}
	
	function showPic(div){
		img_w_prefix='http://image.sinajs.cn/newchart/weekly/n/';
		img_d_prefix='http://image.sinajs.cn/newchart/daily/n/';
		img_div=$('<div class="st_pic_div"></div>');
		stid=div.attr('stockid');
		
		if (stid.charAt(0) == '6'){
			img_id = 'sh'+stid+'.gif';
		}else{
			img_id = 'sz'+stid+'.gif';
		}
		
		img=$('<img/>').attr('src', img_d_prefix+img_id);
		img_div.append(img);
		img_div.append($('<br><br>'));
		
		img=$('<img/>').attr('src', img_w_prefix+img_id);
		img_div.append(img);
		img_div.append($('<br><br>'));
		
		div.append(img_div);
	}

	$(function(){
		$("#filterButton").bind("click", function(event) {
			doFilter();
		});
		
		$("#cleanButton").bind("click", function(event) {
			resetFilter();
		});
		
		$("#exportButton").bind("click", function(event) {
			toggleStockList();
		});
		
		$("#showPicButton").bind("click", function(event) {
			showAllPic();
		});
	});