package com.flowpayapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.flowpayapi.domain.user.RegisterUserDTO;
import com.flowpayapi.domain.user.User;
import com.flowpayapi.domain.user.UserRole;
import com.flowpayapi.exceptions.UserException;
import com.flowpayapi.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public void register(RegisterUserDTO registerDTO) throws UserException {
		if(this.userRepository.findByLogin(registerDTO.login()) != null ) {
			throw new UserException("Usuário cadastrado.");
		}
		
		if(registerDTO.role() == null) {
			throw new UserException("Informe a role.");
		}
		
		if(UserRole.ROLE_ADMIN != registerDTO.role() 
				|| UserRole.ROLE_ADMIN != registerDTO.role() ) {
			throw new UserException("Role inválida.");
		}
		
		String encryptedPass = new BCryptPasswordEncoder().encode(registerDTO.password());
		User newUser = new User();
		newUser.setLogin(registerDTO.login());
		newUser.setPassword(encryptedPass);
		newUser.setRole(registerDTO.role());
		
		this.userRepository.save(newUser);
	}
	
	public UserDetails findByUsername(String username){
		UserDetails userDetails = userRepository.findByLogin(username);
		return userDetails;
	}
	
	public List<User> listAll() {
		List<User> users = userRepository.findAll();
		return users;
	}
}
