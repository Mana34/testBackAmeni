package com.itgate.ProShift.security.jwt;

import com.itgate.ProShift.entity.JwtToken;
import com.itgate.ProShift.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class JwtTokenCleanupService {

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Scheduled(fixedDelay = 10/* <-- l'intervale en minutes*/ * 60 * 1000)
    public void deleteExpiredTokens() {

        List<JwtToken> expiredTokens = jwtTokenRepository.findByExpirationDateBefore(new Date());
        jwtTokenRepository.deleteAll(expiredTokens);
        System.out.println("cleanup done");
    }
}
