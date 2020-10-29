package com.in28minutes.rest.rateLimiting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

@Component
public class PricingPlanService {

	private Map<String, Bucket> cache = new ConcurrentHashMap<>();
	
	public Bucket resolveBucket(String apiKey) {
		return cache.computeIfAbsent(apiKey, this::newBucket);
	}
	
	private Bucket newBucket(String apiKey) {
		PricingPlan pricingPlan = PricingPlan.resolvePlanFromApiKey(apiKey);
		Bandwidth limit =  pricingPlan.getLimit();
		Bucket bucket = Bucket4j.builder()	
						.addLimit(limit)
						.build();
		return bucket;
	}
}
