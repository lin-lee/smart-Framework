package org.smart4j.framework.bean;

public class FormParam {
	
	private String filedName;
	private Object filedValue;
	
	public FormParam(String filedName, Object filedValue) {
		super();
		this.filedName = filedName;
		this.filedValue = filedValue;
	}

	public String getFiledName() {
		return filedName;
	}

	public Object getFiledValue() {
		return filedValue;
	}

}
