var dyxxlist = function(){
	
	this.buildSearchQuery = function(searchArg) {
		searchArg.par("userflag", LEAP.getUserInfo().detail.userflag, 11);
	}
	
	this.dyxx = function(arg){
		var objname = this.md("objname").getValue();
		if(!objname){
			alert('请先给一个对象名条件以便确认打印对象！');
			return;
		}
		var par = new SearchBuilder();
		par.name = "IIRPrintProperties";
		par.par("objname",objname,11);
		par.par("userflag",LEAP.getUserInfo().detail.userflag,11);
		var result = this.request('DynaSearch', {par : par});
		if(result == null || result.result.length < 1){
			alert('暂无该打印对象的打印配置！');
			return;
		}	
		var dyxxForm = LEAP.form.create3({name:"dyxx", title:"打印信息", autodispose:true});
		LEAP.form.maxSize(dyxxForm.form); // 窗体最大化
		dyxxForm.module.initialInfo(objname,LEAP.convertResult(result),null,this);
	}
	
	this.getCurObjName = function(){
		return this.md("objname").getValue();
	}

}