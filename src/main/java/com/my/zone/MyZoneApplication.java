package com.my.zone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "newsZone", version = "1.0.1", description = "newsZone api using Spring boot", license = @License(name = "JSRathore", url = "/myzone/api/v1")))
public class MyZoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyZoneApplication.class, args);
	}

}
