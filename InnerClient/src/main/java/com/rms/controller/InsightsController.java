package com.rms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
//@RequestMapping("/rms")
public class InsightsController {
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/top-earning-artists")
	public Object[] showUsers() {
		Object[] users = restTemplate.getForObject("http://DASHBOARDSERVICE/insights/top-earning-artists", Object[].class);
		return users;
	}

}
