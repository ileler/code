package coderr.kerwin.osgi.demo.mprimpl.model;

import java.io.Serializable;

public class Account implements Serializable {     
	
	private static final long serialVersionUID = -7970848646314840509L;     
	private Integer id;    
	private String name;    
	private String code;    
	private Integer status;    

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Account() {}

	public Account(Integer id, String name, String code, Integer status) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.status = status;
	}

	@Override    
	public String toString() {        
		return this.id + "#" + this.name + "#" + this.code +  "#" +             
				this.status +  "#";    
	}
}