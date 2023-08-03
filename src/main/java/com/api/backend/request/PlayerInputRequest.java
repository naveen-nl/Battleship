package com.api.backend.request;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInputRequest {
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{4,8}$", message = "Invalid player name. Contain at least one uppercase, one lowercase and number.")
	@Size(min = 4, max = 10, message = "Invalid player name length.Min is 4 and max is 10.")
	private String playerName;
}