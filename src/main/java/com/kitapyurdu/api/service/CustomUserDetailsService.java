package com.kitapyurdu.api.service;

import com.kitapyurdu.api.auth.CustomUserDetails;
import com.kitapyurdu.api.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		CustomUserDetails u = userRepository.findForLogin(usernameOrEmail);
		if (u == null) throw new UsernameNotFoundException("Kullanıcı bulunamadı");
		return u;
	}
}
