var dyxxinfo = function(){
	
	var objname = null;

	this.insertBeforeSubmit = function(dataArg) {
		var par = null;
		var result = null;
		try{
			par = new SearchBuilder();
			par.name = "IIRPrintProperties";
			par.addParameter("objname",dataArg.objname,11);
			par.addParameter("objkey",dataArg.objkey,11);
			result = this.request('DynaSearch',{par : par});
			if(result != null){
				alert("键值重复！");
				return false;
			}else{
				dataArg.userflag = LEAP.getUserInfo().detail.userflag;
			}
		}finally{
			par = result = null;
		}
	}
	
	this.pageLoad = function(arg){
	}
	
	this.insertPageLoad = function(arg) {
		this.md("objname").readOnly(false);
		this.md("objkey").readOnly(false);
		objname = this.getParentModule().getCurObjName();
	}
	
	this.setDefaultPageData = function(arg) {
		if(objname)	this.md("objname").setValue(objname);
	}
	
	this.modifyPageLoad = function(arg) {
		this.md("objname").readOnly();
		this.md("objkey").readOnly();
	}

}