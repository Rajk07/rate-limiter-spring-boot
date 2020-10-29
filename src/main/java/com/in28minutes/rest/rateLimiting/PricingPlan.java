package com.in28minutes.rest.rateLimiting;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

public enum PricingPlan {

	FREE{
		@Override
		 Bandwidth getLimit() {
			return Bandwidth.classic(2, Refill.intervally(2, Duration.ofMinutes(1)));
		}
	},
	BASIC{
		@Override
		 Bandwidth getLimit() {
			return Bandwidth.classic(4, Refill.intervally(4, Duration.ofMinutes(1)));
		}
	},
	PROFESSIONAL{
		@Override
		 Bandwidth getLimit() {
			return Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(2)));
		}
	};
	
	 static PricingPlan resolvePlanFromApiKey(String apiKey) {
		if(apiKey==null || apiKey.isEmpty()) {
			return FREE;
		}
		else if(apiKey.startsWith("BAS-")) {
			return BASIC;
		}
		else if(apiKey.startsWith("PRO-")) {
			return PROFESSIONAL;
		}
		return FREE;
	}
	
	 Bandwidth getLimit() {
		 return null;
	 }
}
