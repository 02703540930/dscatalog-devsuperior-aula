package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Autowired
	private JwtTokenStore tokenStore;
	
	// endpoint para todos
	private static final String[] PUBLIC = { "/oauth/token" };
	
	// endpoints operador ou admin
	private static final String[] OPERADOR_OR_ADMIN = { "/products/**", "/categories/** " };
	
	// endpoints exclusivo admin
	private static final String[] ADMIN = { "/users/**" };
	
		
	//decodificar o token e analisar expiracao e secrets
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}
	
	// Autorizacoes de rota por tipo de user e endpoints
	// catalogo liberado sem login 
	// Cruds produto e categoria login operador 
	// Crud  usuario login admin 
	// Qualquer outra rota precisa apenas autenticacao
	@Override
	public void configure(HttpSecurity http) throws Exception {
	
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERADOR_OR_ADMIN).permitAll()
		.antMatchers(OPERADOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated();			
	}
	
}
