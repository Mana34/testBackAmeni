package com.itgate.ProShift.service.interfaces;

import com.itgate.ProShift.entity.JwtToken;

import java.util.List;

public interface IJwtTokenService {
    List<JwtToken> getAllActiveTokens();
}
