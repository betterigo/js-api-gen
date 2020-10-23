package io.github.betterigo.jsgen;

/**
 * <p>Title: ApiParam</p>  
 * <p>Description:接口中的参数描述 </p>
 * @author haodonglei
 * @since 2020年7月16日
 */
public class ApiParam {
	private String name;
	private String in;
	private String description;
	private String required;
	private String type;
	private String defaultValue="";
	private String enumm;//取值范围
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getEnumm() {
		return enumm;
	}
	public void setEnumm(String enumm) {
		this.enumm = enumm;
	}
}
