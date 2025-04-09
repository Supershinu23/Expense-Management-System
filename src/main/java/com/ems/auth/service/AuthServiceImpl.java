package com.ems.auth.service;

import com.ems.auth.entity.User;
import com.ems.auth.model.LoginRequest;
import com.ems.auth.model.RegisterRequest;
import com.ems.auth.repository.UserRepo;
import com.ems.auth.model.RoleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void registerUser(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleType.USER);

        userRepository.save(user);

        // Generate and store OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        redisTemplate.opsForValue().set(request.getMobile(), otp, 5, TimeUnit.MINUTES);

        System.out.println("Simulated OTP sent to user mobile: " + otp);
    }

    public boolean verifyOtp(String mobile, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get(mobile);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            // OTP matched, update user
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.setVerified(true);
            userRepository.save(user);

            // Cleanup OTP
            redisTemplate.delete(mobile);
            return true;
        }
        return false;
    }

    @Override
    public String login(LoginRequest request) throws JsonProcessingException {
        // 1. Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Load user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("User not verified with OTP.");
        }

        // 3. Generate token
        String token = jwtService.generateToken(user); // custom method

        // 4. Save token in Redis with TTL (e.g., 30 minutes)
        redisTemplate.opsForValue().set(token, new ObjectMapper().writeValueAsString(user), 30, TimeUnit.MINUTES);

        return token;
    }
}
