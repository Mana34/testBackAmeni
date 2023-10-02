package com.itgate.ProShift.service;

import com.itgate.ProShift.entity.JwtToken;
import com.itgate.ProShift.repository.JwtTokenRepository;
import com.itgate.ProShift.service.interfaces.IJwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class JwtTokenService implements IJwtTokenService {
    @Autowired
    JwtTokenRepository jwtTokenRepository;
    @Override
    public List<JwtToken> getAllActiveTokens() {
        return jwtTokenRepository.findAllByExpirationDateAfter(new Date());
    }
}
