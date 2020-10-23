package io.github.betterigo.jsgen;

import java.util.List;

/**
 * <p>Title: ApiBody</p>  
 * <p>Description: 接口的请求体</p>
 * @author haodonglei
 * @since 2020年7月16日
 */
public class ApiBody {
	private String type;
	private String name;
	private String description;
	private String required;
	private List<ApiParam> fields;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public List<ApiParam> getFields() {
		return fields;
	}
	public void setFields(List<ApiParam> fields) {
		this.fields = fields;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
