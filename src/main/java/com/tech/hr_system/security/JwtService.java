package com.tech.hr_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Đây là "Con dấu đỏ" để đóng lên thẻ (Phải là một chuỗi ngẫu nhiên, bí mật và rất dài)
    // Thực tế đi làm, người ta sẽ giấu chuỗi này đi chứ không để tơ hơ ở đây đâu nhé!
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // --- 1. MÁY IN THẺ: Tạo thẻ JWT mới ---
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Ghi tên người dùng lên thẻ
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ghi ngày cấp
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Hạn sử dụng: 24 giờ
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Đóng dấu mộc đỏ
                .compact(); // Ép lại thành một chuỗi String dài ngoằng
    }

    // --- 2. MÁY ĐỌC THẺ: Lấy tên người dùng từ cái thẻ ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- 3. MÁY SOI THẺ: Kiểm tra thẻ thật/giả và còn hạn không ---
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- CÁC HÀM HỖ TRỢ BÊN TRONG (Không cần bận tâm lắm) ---
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Dùng con dấu đỏ để giải mã
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
