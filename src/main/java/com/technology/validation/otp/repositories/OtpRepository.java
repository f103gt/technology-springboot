package com.technology.validation.otp.repositories;

import com.technology.validation.otp.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, BigInteger> {
    Optional<Otp> findOtpByOtp(String otp);
}
