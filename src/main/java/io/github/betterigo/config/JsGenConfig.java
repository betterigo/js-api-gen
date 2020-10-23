package io.github.betterigo.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static springfox.documentation.swagger.common.HostNameProvider.componentsFrom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;

import com.google.common.base.Strings;

import io.github.betterigo.jsgen.JsApiGen;
import io.swagger.models.Swagger;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

@ConditionalOnClass(Swagger.class)
@Configuration
public class JsGenConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(JsGenConfig.class);
	
	@Value("${spring.application.name:''}")
	private String appName;
	
	@ConditionalOnBean(Docket.class)
	@Configuration
	class configJsFileDownload{
		@Autowired
		public void configJsFileDownloadTag(Docket docket) {
			logger.info("配置web端js下载地址，path:/jsgen/apis");
			@SuppressWarnings("rawtypes")
			List<VendorExtension> vendorExtensions = new ArrayList<>();
			StringVendorExtension tag1 = new StringVendorExtension("description", "前端js接口文件下载");
			StringVendorExtension tag2 = new StringVendorExtension("url", "/jsgen/apis");
			ObjectVendorExtension extTags = new ObjectVendorExtension("externalDocs");
			extTags.addProperty(tag1);
			extTags.addProperty(tag2);
			vendorExtensions.add(extTags);
			docket.extensions(vendorExtensions);
		}
	}
	//这里要配置一个controller
	@ApiIgnore
	@RestController
	@RequestMapping("/jsgen")
	class ApiJsFileController {
		@Autowired
		private DocumentationCache documentationCache;
		private final String hostNameOverride;
		@Autowired
		private ServiceModelToSwagger2Mapper mapper;
		@Autowired
		private JsonSerializer jsonSerializer;

		@Autowired
		public ApiJsFileController(Environment environment) {
			logger.info("配置swagger的js生成组件...");
			this.hostNameOverride = environment.getProperty("springfox.documentation.swagger.v2.host", "DEFAULT");
		}
		@GetMapping("/apis")
		public void test1(HttpServletResponse resp,HttpServletRequest req) throws IOException {
			Documentation documentation = documentationCache.documentationByGroup("default");
			Swagger swagger = mapper.mapDocumentation(documentation);
			UriComponents uriComponents = componentsFrom(req, swagger.getBasePath());
			swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
//			swagger.setBasePath(appName);
			String fileName = swagger.getInfo().getTitle()+".js";
			if (isNullOrEmpty(swagger.getHost())) {
				swagger.host(hostName(uriComponents));
			}
			Json result = jsonSerializer.toJson(swagger);
			logger.info("swagger json=>{}",result.value());
			try {
				resp.addHeader("content-type", "application/octet-stream");
				String userAgent = req.getHeader("User-Agent");
				String originalFilename = "";
				// 针对IE或者以IE为内核的浏览器
				if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edge")) {
					originalFilename = java.net.URLEncoder.encode(fileName, "UTF-8");
				} else {  
					// 非IE浏览器的处理：  
					originalFilename = new String(fileName.getBytes("UTF-8"), "ISO-8859-1").toString();
				}  
				resp.addHeader("Content-Disposition", "attachment;filename="  + "\""+originalFilename + "\"");
				JsApiGen.createJsApiFile(result.value(),resp.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			resp.getOutputStream().flush();
		}

		private String hostName(UriComponents uriComponents) {
			if ("DEFAULT".equals(hostNameOverride)) {
				String host = uriComponents.getHost();
				int port = uriComponents.getPort();
				if (port > -1) {
					return String.format("%s:%d", host, port);
				}
				return host;
			}
			return hostNameOverride;
		}
	}
}
