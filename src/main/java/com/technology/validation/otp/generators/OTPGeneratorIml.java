package com.technology.validation.otp.generators;

import org.springframework.stereotype.Component;

import java.util.SplittableRandom;
import java.util.stream.IntStream;

@Component
public class OTPGeneratorIml implements OTPGenerator{
    @Override
    public String generateOTP(int otpLength) {
        SplittableRandom splittableRandom = new SplittableRandom();
        StringBuilder stringBuilder = new StringBuilder();
        IntStream stream = splittableRandom.ints(otpLength, 0, 10);// stream of random inst
        stream.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
