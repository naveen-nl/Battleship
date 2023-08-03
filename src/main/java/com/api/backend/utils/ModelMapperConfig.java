
package com.api.backend.utils;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
	/*
	 * TODO: Replace this modelmapper with custom model mapper for handling entity
	 * to response and request to entity
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
