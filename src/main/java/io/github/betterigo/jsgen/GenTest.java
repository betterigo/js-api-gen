package io.github.betterigo.jsgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * <p>Title: GenTest</p>  
 * <p>Description: 生成前端js文件测试</p>
 * @author haodonglei
 * @since 2020年7月15日
 */
public class GenTest {
	private static final String DIR = "api-json";
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readLines(new File(DIR + File.separator +"swagger-api.json"), Charsets.UTF_8);
		StringBuilder builder = new StringBuilder();
		lines.stream().forEach(str->{
			builder.append(str);
//			System.out.println(str);
		});
		ObjectMapper mapper = new ObjectMapper();
		ApiInfo apiInfo = new ApiInfo();
		JsonNode rootNode = mapper.readTree(builder.toString());
		JsonNode info = rootNode.get("info");
		String name = info.get("description").asText();
		apiInfo.setDescription(name);
		JsonNode versionNode = info.get("version");
		if(versionNode!=null) {
			apiInfo.setVersion(versionNode.asText());
		}
		if(info.get("title")!=null) {
			apiInfo.setTitle(info.get("title").asText());
		}
		System.out.println(name);
		JsonNode items = rootNode.get("paths");
		List<ApiItem> apiList = new ArrayList<>();
		Iterator<Entry<String, JsonNode>> fields = items.fields();
		while (fields.hasNext()) {
			ApiItem api = new ApiItem();
			api.setBaseUrl(rootNode.get("basePath").asText());
			Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) fields
					.next();
			String url = entry.getKey();
			api.setUrl(url);
			JsonNode request = entry.getValue();
			Iterator<Entry<String, JsonNode>> params = request.fields();
			while (params.hasNext()) {
				Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry2 = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) params
						.next();
				String method = entry2.getKey();
				api.setMethod(method);
				JsonNode pNode = entry2.getValue();
				//接口op，将用于命名方法名称
				String operation = pNode.get("operationId").asText();
				//接口描述
				String description = pNode.get("summary").asText();
				api.setDescription(description);
				api.setName(operation);
				//读取参数节点数据
				JsonNode parameters = pNode.get("parameters");
				if(parameters.isArray()) {
					Iterator<JsonNode> pIter = parameters.iterator();
					List<ApiParam> apiParams = new ArrayList<>();
					while(pIter.hasNext()) {
						JsonNode p = pIter.next();
						//解析参数。
						String place = p.get("in").asText();
						//只添加在query的
						if("query".equals(place)) {
							ApiParam apiParam = new ApiParam();
							apiParam.setIn(p.get("in").asText());
							apiParam.setName(p.get("name").asText());
							if(p.get("description")!=null) {								
								apiParam.setDescription(p.get("description").asText());
							}
							JsonNode defaultValue = p.get("default");
							if(defaultValue!=null) {
								apiParam.setDefaultValue(defaultValue.asText());
							}
							apiParam.setRequired(p.get("required").asText());
							apiParam.setType(p.get("type").asText());
							apiParams.add(apiParam);
						}
						if("body".equals(place)) {
							ApiBody apiBody = new ApiBody();
							List<ApiParam> bodyFields = new ArrayList<>();
							apiBody.setDescription(p.get("description").asText());
							apiBody.setName(p.get("name").asText());
							apiBody.setRequired(p.get("required").asText());
							//添加body中的字段
							String bodyPos = p.get("schema").get("$ref").asText();
							String[] pos = bodyPos.split("/");
							//index=0是#，代表根目录，从index=1开始索引
							JsonNode bodyNode = rootNode;
							for(int i=1;i<pos.length;i++) {
								bodyNode = bodyNode.get(pos[i]);
							}
							//此时的bodyNode应该就是我们要用到的body对象了
							apiBody.setType(bodyNode.get("type").asText());
							//处理属性信息
							JsonNode propertiesNode = bodyNode.get("properties");
							Iterator<Entry<String, JsonNode>> propertiesIter = propertiesNode.fields();
							while(propertiesIter.hasNext()) {
								ApiParam bodyProperty = new ApiParam();
								Entry<String, JsonNode> propertyNode = propertiesIter.next();
								bodyProperty.setName(propertyNode.getKey());
								JsonNode propertyValueNode = propertyNode.getValue();
								bodyProperty.setType(propertyValueNode.get("type").asText());
								if(propertyValueNode.get("description")!=null) {
									bodyProperty.setDescription(propertyValueNode.get("description").asText());
								}
								//获取取值范围
								if(propertyValueNode.get("enum")!=null) {
									JsonNode enumm = propertyValueNode.get("enum");
									bodyProperty.setEnumm(enumm.toString());
								}
								bodyFields.add(bodyProperty);
							}
							apiBody.setFields(bodyFields);
							api.setBody(apiBody);
						}
					}
					api.setParameters(apiParams);
				}
				break;
			}
			apiList.add(api);
		}
		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		config.setDirectoryForTemplateLoading(new File("fm-templates"));
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("createDate", sdf.format(now));
		dataMap.put("items", apiList);
		dataMap.put("apiInfo", apiInfo);
		//加载模板文件
		Template template = config.getTemplate("request-api.ftl");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		try {
			template.process(dataMap, bw);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
