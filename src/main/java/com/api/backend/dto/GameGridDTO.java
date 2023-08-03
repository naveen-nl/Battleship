package com.api.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameGridDTO {

	private Long gameGridId;
	private int rowIndex;
	private int columnIndex;
	private String gridValue;


}