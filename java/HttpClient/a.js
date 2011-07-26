function bd(str){
	var c1,c2,c3,c4;
	var i,len,out;
	len=str.length;
	i=0;
	out="";
	while(i<len)
	{
		do{
			c1=bDC[str.charCodeAt(i++)&0xff]
		}while(i<len&&c1==-1);
		
		if(c1==-1)break;
		do{
			c2=bDC[str.charCodeAt(i++)&0xff]}
		while(i<len&&c2==-1);
		
		if(c2==-1)break;
		
		out+=String.fromCharCode((c1<<2)|((c2&0x30)>>4));
		do{
			c3=str.charCodeAt(i++)&0xff;
			if(c3==61)return out;
			c3=bDC[c3]
		}while(i<len&&c3==-1);
		
		if(c3==-1)break;
		out+=String.fromCharCode(((c2&0XF)<<4)|((c3&0x3C)>>2));
		do{
			c4=str.charCodeAt(i++)&0xff;
			if(c4==61)return out;
			c4=bDC[c4]
		}while(i<len&&c4==-1);
		
		if(c4==-1)break;
		out+=String.fromCharCode(((c3&0x03)<<6)|c4)
		
	}
	return out;
}

function get_url(){
  	var URLArr=ThisURL.split("&");

	var URLArr_length=URLArr.length;
	var URLMap=[[]];
	for(var i=0;i<URLArr_length;i++)
	{
		var item=URLArr[i];
		var itemArr=item.split("=");
		URLMap[itemArr[0]]=itemArr[1];
	}
		
	var key=URLMap["s"].substr(17,1);
	
	if(key=="a") {realkey="j";}
	else{realkey="k";}
		
	url=bd(decodeURIComponent(URLMap[realkey]));
		
	url=url.substr(32,url.length);
	return url;
}

var targetu = get_url();
