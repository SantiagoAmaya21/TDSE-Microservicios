package com.tdse.twitter.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    
    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }
    
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        
        // Extract permissions from the 'permissions' claim if present
        if (claims.containsKey("permissions")) {
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) claims.get("permissions");
            return permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        
        // Extract scopes from the 'scope' claim
        if (claims.containsKey("scope")) {
            String scope = claims.get("scope").toString();
            return List.of(scope.split(" ")).stream()
                    .map(s -> "SCOPE_" + s)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        
        return List.of();
    }
}
