package com.mallang.auth.domain;


public interface PasswordEncoder {

    Password encode(String rawPassword);

    boolean match(String rawPassword, String encrypted);
}
