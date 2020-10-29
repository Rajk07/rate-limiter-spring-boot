package com.in28minutes.rest.throttling;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

@SpringBootTest
class ThrottlingBasic {

	@Test
	void test_basicUsage() {
		Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
		Bandwidth limit = Bandwidth.classic(10, refill);
		Bucket bucket = Bucket4j.builder().addLimit(limit).build();
		
		for(int i=0;i<10;i++) {
			assertTrue(bucket.tryConsume(1));
		}
		assertFalse(bucket.tryConsume(1));
	}
	
	@Test
	void test_afterGivenDuration() {
		Bucket bucket = Bucket4j
				.builder()
				.addLimit(Bandwidth.classic(1, Refill.intervally(1,Duration.ofSeconds(2))))
				.build();
		assertTrue(bucket.tryConsume(1));
		Executors.newScheduledThreadPool(1)
			.schedule(() -> { assertTrue(bucket.tryConsume(1));} , 2, TimeUnit.SECONDS);
	}
	
	@Test
	void test_multipleLimits() {
		Bucket bucket = Bucket4j
						.builder()
						.addLimit(Bandwidth.classic(10, Refill.intervally(10,Duration.ofMinutes(1))))
						.addLimit(Bandwidth.classic(5, Refill.intervally(5,Duration.ofSeconds(20))))
						.build();
		for(int i=0;i<5;i++) {
			assertTrue(bucket.tryConsume(1));
		}
		assertFalse(bucket.tryConsume(1));
		Executors.newScheduledThreadPool(1)
				.schedule(()-> { bucket.tryConsume(15);	}, 20, TimeUnit.SECONDS);
				
	}

}
