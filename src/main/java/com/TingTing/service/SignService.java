package com.TingTing.service;

import com.TingTing.dto.ResponseDTO;
import com.TingTing.dto.SignInRequest;
import com.TingTing.dto.SignUpRequest;
import com.TingTing.dto.TokenResponse;
import com.TingTing.entity.User;
import com.TingTing.entity.EmailVerificationToken;
import com.TingTing.mapper.UserMapper;
import com.TingTing.repository.EmailVerificationTokenRepository;
import com.TingTing.repository.UserRepository;
import com.TingTing.util.CodeGenerator;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SignService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final long ACCESS_EXPIRE = 1000 * 60 * 60;         // 1 hour
    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7 days

    // ✅ 이메일 중복 확인
    public boolean isEmailExist(String email) {
        return userRepository.findByUsEmail(email).isPresent();
    }

    // ✅ 인증 코드 전송
    public ResponseDTO sendVerificationEmail(String email) {
        if (isEmailExist(email)) {
            return new ResponseDTO(false, "이미 가입된 이메일입니다.");
        }

        // 기존 인증 기록 삭제
        tokenRepository.deleteByEmail(email);

        // 인증 코드 생성 및 저장
        String code = CodeGenerator.generateNumericCode(6);
        EmailVerificationToken token = EmailVerificationToken.builder()
                .email(email)
                .code(code)
                .verified(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build();

        tokenRepository.save(token);

        // 이메일 전송
        emailService.sendVerificationEmail(email, code);

        return new ResponseDTO(true, "인증코드를 전송했습니다.");
    }

    // ✅ 인증 코드 확인
    public ResponseDTO checkVerificationCode(String email, String code) {
        Optional<EmailVerificationToken> optional = tokenRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (optional.isEmpty()) {
            return new ResponseDTO(false, "인증 요청이 없습니다.");
        }

        EmailVerificationToken token = optional.get();

        if (token.isVerified()) {
            return new ResponseDTO(false, "이미 인증된 이메일입니다.");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ResponseDTO(false, "인증 코드가 만료되었습니다.");
        }

        if (!token.getCode().equals(code)) {
            return new ResponseDTO(false, "인증 코드가 일치하지 않습니다.");
        }

        token.setVerified(true);
        token.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(token);

        return new ResponseDTO(true, "이메일 인증이 완료되었습니다.");
    }

    // ✅ 이메일 인증 여부 확인
    public boolean isEmailVerified(String email) {
        return tokenRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .filter(EmailVerificationToken::isVerified)
                .filter(token -> token.getVerifiedAt().isAfter(LocalDateTime.now().minusHours(1)))
                .isPresent();
    }

    // ✅ 회원가입 처리
    public void signup(SignUpRequest request) {
        if (isEmailExist(request.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        if (!isEmailVerified(request.getEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (userRepository.existsByUsNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        User user = UserMapper.toEntity(request, passwordEncoder);
        userRepository.save(user);

    }

    // ✅ 로그인 처리
    public TokenResponse logIn(SignInRequest dto, HttpServletResponse response) {
        User user = userRepository.findByUsEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getUsPw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsEmail());

        refreshTokenService.saveOrUpdate(user.getUsEmail(), refreshToken, REFRESH_EXPIRE);

        Cookie ac = new Cookie("accessToken", accessToken);
        ac.setHttpOnly(true);
        ac.setPath("/");
        ac.setMaxAge((int) (ACCESS_EXPIRE / 1000));
        response.addCookie(ac);

        Cookie rc = new Cookie("refreshToken", refreshToken);
        rc.setHttpOnly(true);
        rc.setPath("/");
        rc.setMaxAge((int) (REFRESH_EXPIRE / 1000));
        response.addCookie(rc);

        return new TokenResponse(user.getUsNickname());
    }

    // ✅ 로그아웃 처리
    public void logOut(User user, HttpServletResponse response) {
        refreshTokenService.delete(user.getUsEmail());

        Cookie ac = new Cookie("accessToken", null);
        ac.setPath("/");
        ac.setMaxAge(0);
        response.addCookie(ac);

        Cookie rc = new Cookie("refreshToken", null);
        rc.setPath("/");
        rc.setMaxAge(0);
        response.addCookie(rc);
    }
}
