package com.jimetevenard.jimnotesback.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListeController {
	
	
	@GetMapping("/liste")
	public String[] list(){
		return new String[]{"foo","bar","baz"};
	}

}
