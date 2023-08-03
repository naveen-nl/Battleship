package com.api.backend.utils;

public class Constants {

	public static final int GRID_VALUE = 10;
	public static final String SHIP_DATA_FILE_PATH = "/ShipInfo.txt";
	public static final String USER_ROLE = "USER";
	public static final String[] CACHE_KEYS = { "players", "games", "ships" };
	public static final String[] AUTH_WHITE_LIST = { "/v3/api-docs/**", "/v2/api-docs/**", "/h2-console/**",
			"/swagger-ui/**", "/swagger-resources/**" };
	public static final String[] WEB_WHITE_LIST = { "/h2-console/**", "/swagger-ui/**" };
	public static final String[] HTTP_METHODS= {"GET", "POST", "DELETE", "PUT"};
	public static final String[] HTTP_HEADERS= {"*"};
	public static final String CORS_PATTERN="/**";
}
