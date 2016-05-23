var objname = null;
var selfForm = null;
var printlist = null;
var parentForm = null;
var locationlist = null;

var dyxx = function() {

	this.pageLoad = function(){
		//4A	72DPI	PrintSprite4AW595H842
		//4A	96DPI	PrintSprite4AW794H1123
		this.setFlash("PrintSprite4AW595H842",595,842);
		this.ut("xvalue").valueChange(this.xValueChanged);
		this.ut("yvalue").valueChange(this.yValueChanged);
	}
	
	this.setFlash = function(flash,width,height){
		if(!flash || !width || !height)	return;
		var tp = (window.location.href.indexOf('/CSIP/')!=-1) ? '/CSIP/' : '/';
		document.getElementById("container").innerHTML=
			'<object id="flashvars" name="flashvars" ' + 
				'align="center" style="width:'+width+'px;height:'+height+'px;margin:0px;padding:0px;vertical-align:center;border:1px solid black" ' + 
				'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ' + 
				'codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" > ' + 
					'<param name="quality" value="high" /> ' +
					'<param name="allowscriptAccess" value="always" /> ' + 
					'<param name="movie" id="path1" value="' + tp + 'LEAP/CSIP/dyxx/'+flash+'.swf" /> ' + 
					'<embed id="path2" src="' + tp + 'LEAP/CSIP/dyxx/'+flash+'.swf" allowScriptAccess="never" allowNetworking="internal" ' + 
						'autostart="0" quality="high" bgcolor="#ffffff" style="width:100%;height:100%" name="flashvars" align="center" ' + 
						'type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" /> ' + 
			'</object>';
	}
	
	/**
	 * 打印初始化
	 * placelst Location对象列表（Location格式{userflag:用户编码,objkey:字段名,objvalue:字段默认值,objx:X值,objy:Y值,font:字体,isbold:是否粗体,fontsize:字体大小,status:状态}）
	 * plist    需要打印的对象列表（对象格式{属性1:属性值1,属性2:属性值3,...}）
	 * parent	调用本页面的页面对象、即为本页面的父页面
	 */	
	this.initialInfo = function(on, placelst, plist, parent) {
		if(!on){
			alert('必须提供打印对象名称！');
			this.hideForm();
			return;
		}	
		objname = on;
		if((!placelst || placelst.length < 1)&&(!plist || plist.length < 1)){
			//默认供参考
			var location1 = new Object();
			location1.objkey='testkey1';
			location1.objvalue='testvalue2';
			var location2 = new Object();
			location2.objkey='testkey2';
			location2.objvalue='testvalue2';
			var location3 = new Object();
			location3.objkey='tpdx1';
			location3.objvalue='图片对象';
			location3.objx=100;
			location3.objy=100;
			placelst = new Array(location1,location2,location3);
			
			var printobj1 = new Object();
			printobj1.testkey1 = '对象1key1';
			printobj1.tpdx1 = 'http://www.baidu.com/img/baidu_jgylogo3.gif?117&38';
			var printobj2 = new Object();
			printobj2.testkey1 = '对象2key1';
			printobj2.testkey2 = '对象2key2';
			var printobj3 = new Object();
			printobj3.testkey1 = '对象3key1';
			printobj3.testkey2 = '对象3key2';
			printobj3.tpdx1 = 'http://coderr-wordpress.stor.sinaapp.com/uploads/2014/08/tx.jpg?120&120';
			plist = new Array(printobj1,printobj2,printobj3);
		}else if(placelst&&placelst.length>0&&(!plist || plist.length < 1)){
			plist = new Array(new Object());
		}
		
		selfForm = this;
		printlist = plist;
		parentForm = parent;
		locationlist = placelst;
	}
	
	//界面保存按钮的点击事件
	this.saveBtnClicked = function(arg){
		document.getElementById("flashvars").saveBtnClicked();	//点击保存按钮后调用flash方法保存当前空间的定位位置
	}
	
	//界面打印按钮的点击事件
	this.print = function(arg){
		//由于flash为单线程。必须有个预打印过程用来加载所有须打印的对象
		//status 0(按钮此时不能点击)   1(按钮为预打印)   2(按钮为可打印)
		if(this.getUT("print").status == 0)	return;	//此处为防止多次连续点击、判断按钮状态	
		
		if(this.getUT("print").status == 1){	//默认是预打印
			this.getUT("print").status = 0;
			document.getElementById("flashvars").goToPrint();			//点击预打印后调用flash方法准备打印
			this.getUT("print").innerHTML = "开始打印证书";
			this.getUT("print").status = 2;								//预打印完成后将按钮设为可打印
		}else{
			this.getUT("print").status = 0;
			document.getElementById("flashvars").printBtnClicked();		//点击打印后调用flash方法开始打印
		}
	}
	
	//改变X值后调用flash方法改变当前控件的X值
	this.xValueChanged = function(arg){
		var xvalue = null;
		if(!(xvalue = this.ut("xvalue").getValue()) || (isNaN(xvalue = parseFloat(xvalue))))	return;
		document.getElementById("flashvars").xValueChanged(xvalue);		//调用flash方法改变当前控件X值
	}
	
	//改变Y值后调用flash方法改变当前控件的Y值
	this.yValueChanged = function(arg){
		var yvalue = null;
		if(!(yvalue = this.ut("yvalue").getValue()) || (isNaN(yvalue = parseFloat(yvalue))))	return;
		document.getElementById("flashvars").yValueChanged(yvalue);		//调用flash方法改变当前控件Y值
	}
	
}

//flash回调方法 flash对象加载完成后回调方法
function callPrintBack() {
	//调用flash内部方法初始化打印信息
	document.getElementById("flashvars").initPrintInfo(locationlist, printlist);
}

//flash回调方法 flash保存打印配置后会回调该方法、参数为Location对象数组、此处做保存数据到数据库的操作
function savePrintProperties(_savelst) {
	if(_savelst){
		//改变位置后、界面按钮变为预打印
		selfForm.getUT("print").status = 1;
		selfForm.getUT("print").innerHTML = "准备打印证书";
		
		//此处做保存操作
		var result = selfForm.request('savePrintProperties', {par1 : objname, par2 : _savelst});
		if(result&&result=='success'){
			alert('保存成功');
		}else{
			alert('保存失败');
		}
	}else{
		alert(result?(result):'无数据保存');
	}
}

//flash回调方法 flash内拖动控件后会回调该方法、参数为(X<<>>Y)格式的X为控件的X值、Y为控件的Y值
function changeAttrLocation(arg){
	if(!arg)	return;
	selfForm.ut("xvalue").setValue(arg.split("<<>>")[0]);
	selfForm.ut("yvalue").setValue(arg.split("<<>>")[1]);
}

//flash回调方法 flash打印完成后会回调该方法
function afterPrintDone() {
	//此处可以做自定义操作

	//do something
	if(parentForm&&parentForm.afterPrintDone)	parentForm.afterPrintDone();
	
	//打印完成后、界面按钮变为预打印
	selfForm.getUT("print").status = 1;
	selfForm.getUT("print").innerHTML = "准备打印证书";
}

//flash回调方法 供flash显示消息的方法
function flashAlter(arg){
	if(arg == -1){
		alert('未提供初始化信息');
	}else if(arg == 1){
		alert('请先准备打印');
		selfForm.getUT("print").status = 1;
		selfForm.getUT("print").innerHTML = "准备打印证书";
	}else if(arg == 2){
		alert('正在准备打印');
	}else{
		alert(arg);
	}
}