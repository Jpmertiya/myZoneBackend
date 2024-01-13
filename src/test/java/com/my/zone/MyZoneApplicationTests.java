package com.my.zone;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class MyZoneApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void testMongoTemplate() {
		assertThat(mongoTemplate).isNotNull();
	}

	@Test
	void testMongoConfiguration() {
		MongoProperties bean = applicationContext.getBean(MongoProperties.class);
		assertThat(bean.getUri()).isEqualTo("mongodb://localhost:27017/myZone");
	}

}
