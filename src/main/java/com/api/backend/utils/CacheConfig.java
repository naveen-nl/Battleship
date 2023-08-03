package com.api.backend.utils;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class CacheConfig provides configuration for caching in the application.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	
	/**
	 * Configures the cache manager.
	 *
	 * @return the cache manager instance to be used in the application
	 */
	@Bean
	@Override
	public CacheManager cacheManager() {
		// ConcurrentMapCacheManager
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
		cacheManager.setCacheNames(Arrays.asList(Constants.CACHE_KEYS));
		return cacheManager;
	}

	/**
	 * Configures the key generator for caching.
	 *
	 * @return the key generator instance to be used in the application
	 */
	@Override
	public KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}
}
