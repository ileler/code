package {
	
	import fl.controls.Button;
	
	import flash.display.Loader;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TextEvent;
	import flash.external.ExternalInterface;
	import flash.geom.Rectangle;
	import flash.net.URLRequest;
	import flash.printing.PrintJob;
	import flash.printing.PrintJobOrientation;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFieldType;
	import flash.text.TextFormat;
	
	//A4 72DPI	595×842
	[SWF("width"=595,"height"=842)]
	public class PrintSprite4AW595H842 extends Sprite {
		
		private var gw:int = stage.stageWidth;
		private var gh:int = stage.stageHeight;
		private var fontSize:int = 12;
		private var tpdxnumjz:int = -1;
		private var tpdxnumwc:int = -1;
		private var objList:Array = null;
		private var locList:Array = null;
		private var viewSpr:Sprite = null;
		private var currentSpr:Sprite = null;
		private var textFormat:TextFormat = null;
		private var _printlist:Array = null; 
		
		public function PrintSprite4AW595H842() {
			
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE; 
			
			ExternalInterface.addCallback("printBtnClicked", printBtnClicked);	
			ExternalInterface.addCallback("saveBtnClicked", saveBtnClicked);	
			ExternalInterface.addCallback("xValueChanged", xValueChanged);	
			ExternalInterface.addCallback("yValueChanged", yValueChanged);	
			ExternalInterface.addCallback("initPrintInfo", initPrintInfo);		//注册初始化打印信息供JS调用
			ExternalInterface.addCallback("goToPrint", goToPrint);
			ExternalInterface.call("callPrintBack");							//调用JS方法提醒flash已加载完成
			
		}
		
		//保存打印配置
		public function saveBtnClicked():void {
			if(!viewSpr || viewSpr.numChildren < 1)	return;
			var _savelst:Array = new Array();
			var _tmp:Sprite = (Sprite)(viewSpr.getChildAt(0));
			for(var i:int = 0, j:int = _tmp.numChildren; i < j; i++) {
				var attrSpr:Sprite = (Sprite)(_tmp.getChildAt(i));
				var s:String = attrSpr.name;
				var x:Number = (Number)(attrSpr.x);
				var y:Number = (Number)(attrSpr.y);
				var saveS:String = s+"<<>>"+x+"<<>>"+y;
				_savelst.push(saveS);
				
				//循环打印属性对象列表
				for(var a:int = 0, b:int = locList.length; a < b; a++) {
					if((String)(locList[i].objkey) == attrSpr.name){
						locList[i].objx = x;
						locList[i].objy = y;
						break;
					}
				}
			}
			ExternalInterface.call("savePrintProperties",_savelst);		//调用JS方法做相应操作
		}
		
		//
		public function goToPrint():void {
			tpdxnumjz = 0;
			tpdxnumwc = 0;
			_printlist = new Array();
			//循环需打印的对象列表
			for(var ii:int = 0, jj:int = objList.length; ii < jj; ii++) {
				_printlist.push(initPrintObj(objList[ii]));
			}
		}
		
		//批量打印证书
		public function printBtnClicked():void {
			
			if(tpdxnumjz == -1){
				ExternalInterface.call("flashAlter",1);		//请先确认打印
				return;
			}else if(tpdxnumjz != tpdxnumwc){
				ExternalInterface.call("flashAlter",2);		//正在确认打印
				return;
			}
			
			var pj:PrintJob = new PrintJob();
			if(pj.start()) {                
				if(pj.orientation == PrintJobOrientation.LANDSCAPE) {    
					throw new Error("Without embedding fonts you must print one sheet per page with an orientation of portrait.");
				}
				try {
					//循环取得要打印的sprite,添加打印页数
					for(var a:int = 0, b:int = _printlist.length; a < b; a++) {
						pj.addPage((Sprite)(_printlist[a]), new Rectangle(0,0,gw,gh));
					}
					pj.send();
				} catch (e:Error) {
					// do nothing
				}
				tpdxnumjz = -1;
				tpdxnumwc = -1;
				ExternalInterface.call("afterPrintDone");		//调用JS方法做相应操作
			}
			
		}
		
		public function initPrintObj(printObj:Object):Sprite{
			var tmpSpr:Sprite = new Sprite();				//装对象所有需打印的属性的Sprite
			var attrIndex:int = 0;							//属性索引
			var tmpx:int = 1;								//x轴索引
			
			//循环打印属性对象列表
			for(var i:int = 0, j:int = locList.length; i < j; i++) {
				var locationObj:Object = locList[i];	//打印属性对象
				var ks:String = (String)(locationObj.objkey);	//属性Key
				var objAttrSpr:Sprite = new Sprite();			//为属性生成一个Sprite
				objAttrSpr.name = ks;
				if(!locationObj.objx || !locationObj.objy){
					//如果打印属性对象未提供坐标则默认计算坐标
					var _tmpy:int = (attrIndex++) * fontSize + 50;
					if(_tmpy > gh){
						tmpx++;
						attrIndex = 0;
						_tmpy = (attrIndex++) * fontSize;
					}
					objAttrSpr.x = tmpx * 50;
					objAttrSpr.y = _tmpy;
					attrIndex++;
				}else{
					objAttrSpr.x = locationObj.objx;
					objAttrSpr.y = locationObj.objy;
				}
				if(!printObj[ks]){
					printObj[ks] = (printObj[ks] == "" ? "" : locationObj.objvalue);		//如果对象对应的key无值则默认用属性对象的值
				}
				
				//判断是不是图片路径（约定图片对象的属性key以'tpdx'开头）
				if(ks.substr(0,4) == "tpdx" && (String)(printObj[ks]).split("?").length == 2 && (String)(printObj[ks]).split("&").length == 2) {
					var urlstr:String = printObj[ks];
					var url:String = urlstr.split("?")[0];
					var wh:String = urlstr.split("?")[1];
					var w:Number = wh.split("&")[0];
					var h:Number = wh.split("&")[1];
					var rect:Shape = new Shape();
					rect.graphics.beginFill(0xFFFFFF);
					rect.graphics.drawRect(0, 0, w, h);
					rect.graphics.endFill();
					objAttrSpr.addChild(rect);
					tpdxnumjz++;
					var loader:Loader = new Loader();
					loader.mask = rect;
					loader.load(new URLRequest(url));
					objAttrSpr.addChild(loader);
					loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function (e:Event):void {
						tpdxnumwc++;
					});
					
				} else {
					
					textFormat = new TextFormat();
					if(locationObj.fontsize && (Number)(locationObj.fontsize) > 0){
						textFormat.size = locationObj.fontsize;
					}else{
						textFormat.size = fontSize;
					}
					if(locationObj.isbold){
						textFormat.bold = (locationObj.isbold == 1);
					}else{
						textFormat.bold = false;
					}
					if(locationObj.font){
						textFormat.font = locationObj.font;
					}else{
					}
					
					var txtprinti:TextField = new TextField();
					txtprinti.name = "txt"+ks;
					txtprinti.text = printObj[ks];
					txtprinti.selectable = false;
					txtprinti.autoSize = TextFieldAutoSize.LEFT;
					txtprinti.setTextFormat(textFormat);
					if(locationObj.width){
						txtprinti.width = locationObj.width;
						txtprinti.wordWrap = true;
					}
					objAttrSpr.addChild(txtprinti);
				}
//				ExternalInterface.call("flashAlter",objAttrSpr.name+"<<>>"+objAttrSpr.x+"<<>>"+objAttrSpr.y);
				//保存到要打印的sprite里面
				tmpSpr.addChild(objAttrSpr);
			}
			
			return tmpSpr;
			
		}
		
		//locationlist位置列表，打印数据列表,printlist打印数据列表，fontsize打印字体大小
		public function initPrintInfo(locationlist:Array,printlist:Array):void {
			//验空操作
			if(!locationlist || locationlist.length < 1 || !printlist || printlist.length < 1){
				ExternalInterface.call("flashAlter",-1);		//未提供初始化信息
				return;
			}
			
			objList = printlist;
			locList = locationlist;
			
			var tmpSpr:Sprite = initPrintObj((Object)(printlist[0]));
			
			for(var i:int = 0, j:int = tmpSpr.numChildren; i < j; i++){
				tmpSpr.getChildAt(i).addEventListener(MouseEvent.MOUSE_UP, mouseReleased);
				tmpSpr.getChildAt(i).addEventListener(MouseEvent.MOUSE_DOWN, mousePressed);
			}
			
			//只用显示第一个打印信息
			viewSpr = new Sprite();
//			viewSpr.graphics.beginFill(0x000000);
//			viewSpr.graphics.drawRect(0, 0, gw, gh);
//			viewSpr.graphics.endFill();
//			viewSpr.graphics.beginFill(0xFFFFFF);
//			viewSpr.graphics.drawRect(1, 1, gw-2, gh-2);
//			viewSpr.graphics.endFill();
			viewSpr.addChild(tmpSpr);
			addChild(viewSpr);
			
		}
		
		//鼠标按下时触发的事件
		public function mousePressed(event:MouseEvent):void {
			event.currentTarget.startDrag();
			currentSpr = (Sprite)(event.currentTarget);
			ExternalInterface.call("changeAttrLocation",currentSpr.x+"<<>>"+currentSpr.y);
		}
		
		//鼠标松开时触发的事件
		public function mouseReleased(event:MouseEvent):void {
			event.currentTarget.stopDrag();
			//			dynaChangeCurrentSprXY();
			ExternalInterface.call("changeAttrLocation",currentSpr.x+"<<>>"+currentSpr.y);
		}
		
		public function xValueChanged(xValue:Number):void {
			currentSpr.x = xValue;
			dynaChangeCurrentSprXY();
		}
		
		public function yValueChanged(yValue:Number):void {
			currentSpr.y = yValue;
			dynaChangeCurrentSprXY();
		}
		
		public function dynaChangeCurrentSprXY():void {
			if(currentSpr.x < 0){
				currentSpr.x = 0;
			}
			if(currentSpr.x >= gw){
				currentSpr.x = gw - 5;
			}
			if(currentSpr.y < 0){
				currentSpr.y = 0;
			}
			if(currentSpr.y >= gh){
				currentSpr.y = gh - 5;
			}
		}
		
	}
}