package io.github.betterigo.jsgen;

import java.util.List;

public class ApiItem {
	private String name;
	private String method;
	private String baseUrl;
	private String url;
	private String description;
	private List<ApiParam> parameters;
	private ApiBody body;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ApiParam> getParameters() {
		return parameters;
	}
	public void setParameters(List<ApiParam> parameters) {
		this.parameters = parameters;
	}
	public String getBaseUrl() {
		if("/".equals(baseUrl)) {
			return "";
		}
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public ApiBody getBody() {
		return body;
	}
	public void setBody(ApiBody body) {
		this.body = body;
	}
}
