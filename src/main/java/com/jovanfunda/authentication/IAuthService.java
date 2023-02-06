package com.jovanfunda.authentication;

import com.jovanfunda.model.database.User;
import org.springframework.stereotype.Service;

@Service
public interface IAuthService {
    public String generateJWT(User user);

    public boolean isAuthorized(String token);
}

