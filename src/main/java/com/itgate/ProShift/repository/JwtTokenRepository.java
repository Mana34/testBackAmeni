package com.itgate.ProShift.repository;

import com.itgate.ProShift.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    JwtToken findByToken(String token);
    List<JwtToken> findByExpirationDateBefore(Date d);
    List<JwtToken> findAllByExpirationDateAfter(Date d);
}
