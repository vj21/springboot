package com.vanderlei.apiuol.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration

@EnableSwagger2

public class SwaggerConfig{

    @Bean
    public Docket produceApi(){
    return new Docket(DocumentationType.SWAGGER_2)
    		.apiInfo(apiInfo())
    		.select()
    		.apis(RequestHandlerSelectors.basePackage("com.vanderlei.apiuol.controller"))
    		.paths(paths()).build();
    }

	private ApiInfo apiInfo() {
	    return new ApiInfoBuilder()
	    		.title("Projeto UOL - APIclientes")
	    		.description("Este projeto contém uma API Rest com CRUD de clientes (id, nome, idade, minTemp e MaxTemp), onde a informação de temperatura é obtida através do ip cliente que está fazendo a requição")
	    		.version("1.0-SNAPSHOT")
	    		.build();
	}
	
	private Predicate<String> paths() {	
		return Predicates.and(PathSelectors.regex("/clientes.*"),Predicates.not(PathSelectors.regex("/error.*")));
	}
}