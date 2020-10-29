package com.in28minutes.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.rest.dao.UserDAOService;
import com.in28minutes.rest.entity.User;
import com.in28minutes.rest.rateLimiting.PricingPlanService;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

@RestController
@RequestMapping("throttling/priced")
public class UserControllerPricingPlan {

	@Autowired
	UserDAOService userService;
	@Autowired
	PricingPlanService pricingPlanService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(@RequestHeader("X-API-KEY")String apiKey){
		Bucket bucket = pricingPlanService.resolveBucket(apiKey);		
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		if(probe.isConsumed()) {
			List<User> users = userService.getUsers();
			return  ResponseEntity.ok()
					.header("X-RATE-LIMIT-REMAINING", Long.toString(probe.getRemainingTokens()))
					.body(users);
		}
		long wiatForRefill = probe.getNanosToWaitForRefill()/1000000000;
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.header("X-RATE-LIMIT-RETRY-AFTER-SECONDS", Long.toString(wiatForRefill))
				.build();
	
	}
}
