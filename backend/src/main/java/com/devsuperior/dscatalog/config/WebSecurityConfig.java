package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// implementacao adidionada para fazer com que os endpoits nao precisem fazer login, ignoring
// o actuator eh uma biblioteca usada pelo spring cluod oauth
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDetailsService userDetailService;
	
	
	//analisa como busca o email com o userDetailService e encripta email com passwordEncoder
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
	}

	
	//como esta no application.properties open.view = false, temos que liberar o acesso aos endpoints
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("actuator/**");
	}


	//criado para deixar disponivel como componente do sistema o autenticationManager
	//quando for implementar o AuthorizationServer vai precisar dele
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	
}
