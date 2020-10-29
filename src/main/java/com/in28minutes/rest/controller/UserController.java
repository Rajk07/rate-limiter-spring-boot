package com.in28minutes.rest.controller;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.rest.dao.UserDAOService;
import com.in28minutes.rest.entity.User;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

@RestController
@RequestMapping(value ="/throttling")
public class UserController {

	@Autowired
	private UserDAOService service;
	
	
	private Bucket bucket;

	public UserController() {
		this.bucket = Bucket4j
				        .builder()		
				        .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(2))))
						.build();
	}
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers() {
		if(bucket.tryConsume(1)) {
			List<User> usersList = service.getUsers();
			return new ResponseEntity(usersList, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
	}

}
