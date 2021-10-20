package com.devsuperior.dscatalog.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.devsuperior.dscatalog.entitites.User;
import com.devsuperior.dscatalog.repositories.UserRepository;

@Component
public class JwtTokenEnhacer implements TokenEnhancer{

	@Autowired
	UserRepository userRepository;
	
	//implementa objetos no token, como Id, nome...
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		User user = userRepository.findByEmail(authentication.getName());
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("userFirstName", user.getFirstName());
		map.put("userId", user.getId());
		
	    // adicionar os dados no token com um tipo padrao que tem  o setAdditionalInformation
		DefaultOAuth2AccessToken token =  (DefaultOAuth2AccessToken) accessToken;
		token.setAdditionalInformation(map);
		
		//pode ser tb return token atribuido acima
		return accessToken;		
		
	}

}
