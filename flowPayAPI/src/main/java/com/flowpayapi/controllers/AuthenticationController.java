package com.flowpayapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowpayapi.domain.user.AuthenticationDTO;
import com.flowpayapi.domain.user.LoginResponseDTO;
import com.flowpayapi.domain.user.RegisterUserDTO;
import com.flowpayapi.domain.user.User;
import com.flowpayapi.exceptions.UserException;
import com.flowpayapi.infra.security.TokenService;
import com.flowpayapi.services.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.login(), authenticationDTO.password());
		var auth = this.authenticationManager.authenticate(usernamePassword);
		var token = tokenService.generateToken((User) auth.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponseDTO(token));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDTO registerDTO) throws UserException {
		this.userService.register(registerDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso"); 
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<User>> listAll() {
		List<User> users = userService.listAll();
		return ResponseEntity.ok(users);
	}
}