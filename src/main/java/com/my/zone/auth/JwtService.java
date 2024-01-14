package com.my.zone.auth;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRET_KEY = "qNmd0XpGOYQ7IXAEo/GCmI0c94fpoF683YI5WSpazdOSdsxfzdNU0ZwcNX7GTT3p";

	public String generateToken(UserDetails details) {
		return generateToken(new HashMap<>(), details);
	}

	public String generateToken(Map<String, Object> claimResolver, UserDetails details) {
		return Jwts.builder().claims(claimResolver).subject(details.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)).signWith(generateSignKey())
				.compact();
	}

	public Key generateSignKey() {
		byte[] arr = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(arr);

	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(generateSignKey()).build().parseClaimsJws(token).getPayload();
	}

	public <T> T extractClaim(Function<Claims, T> claims, String token) {
		Claims extractAllClaims = extractAllClaims(token);
		return claims.apply(extractAllClaims);
	}

	public String getUserName(String token) {
		return extractClaim(Claims::getSubject, token);
	}

	public Date getExpirationDate(String token) {
		return extractClaim(Claims::getExpiration, token);
	}

	public boolean isTokenExpired(String token) {
		return (getExpirationDate(token).before(new Date(System.currentTimeMillis())));
	}

	public boolean isTokenExpired(UserDetails details, String token) {
		return (getUserName(token).equals(details.getUsername()) && !isTokenExpired(token));
	}

}
