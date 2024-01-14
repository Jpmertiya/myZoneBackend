package com.my.zone.Models;

import java.time.LocalDateTime;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document
@CompoundIndex(name = "userId_index", def = "{'userId': 1}")
public class Notes {
	@Id
	private ObjectId id;

	@NotNull
	@Size(min = 2, message = "title should be atleast 2 character long")
	private String title;
	@NotNull
	@Size(min = 5, message = "description should be atleast 5 character Long")
	private String description;
	@NotNull(message = "enter tags please")
	private String tag;

	private String userId;

	@Indexed(name = "date")
	private LocalDateTime dateTime;

}
