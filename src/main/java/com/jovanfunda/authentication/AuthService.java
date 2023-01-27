package com.jovanfunda.authentication;

import com.jovanfunda.model.Permission;
import com.jovanfunda.model.User;
import com.jovanfunda.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@Service
public class AuthService implements IAuthService {

    private static Key key = new SecretKeySpec("secret".getBytes(), "AES");

    @Autowired
    UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateJWT(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("permissions", user.getPermissions());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60L))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean hasPermission(String token, Permission permission) {
        if (token.contains("Bearer ")) {
            String jwt = token.substring(token.indexOf("Bearer ") + 7);
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
            List<Permission> permissions = (ArrayList<Permission>) claims.getBody().get("permissions");
            for (Iterator<Permission> i = permissions.iterator(); i.hasNext();) {
                String item = String.valueOf(i.next());
                if(item.equals(permission.name())) {
                    return true;
                }
            }
        }
        return false;
    }

    // ubaciti da gde god pise da je istekao token da te baci na login stranu?? kao elearning

    public boolean isAuthorized(String token) {
        if (!isEmpty(token) && token.contains("Bearer ")) {
            String jwt = token.substring(token.indexOf("Bearer ") + 7);
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);

            if (userRepository.findById(claims.getBody().getSubject()).isPresent()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(String s){
        return s == null || s.isEmpty();
    }

}
