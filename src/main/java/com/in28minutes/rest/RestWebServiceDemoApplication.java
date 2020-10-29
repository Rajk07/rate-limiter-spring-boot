package com.in28minutes.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.in28minutes.rest.interceptor.RateLimitInterceptor;

@SpringBootApplication
public class RestWebServiceDemoApplication  implements WebMvcConfigurer{

	@Autowired
	@Lazy
	RateLimitInterceptor rateLimitInterceptor;
	
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rateLimitInterceptor)
				.addPathPatterns("/ratelimiting/interceptor/**");
	}
	
	public static void main(String[] args) {
		
		 new SpringApplicationBuilder(RestWebServiceDemoApplication.class)
         .run(args);
	}


	

}
