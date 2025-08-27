package com.TingTing.service;

import com.TingTing.dto.ResponseDto;
import com.TingTing.dto.SignInRequestDto;
import com.TingTing.dto.SignUpRequestDto;
import com.TingTing.dto.TokenResponseDto;
import com.TingTing.entity.User;
import com.TingTing.entity.EmailVerificationToken;
import com.TingTing.mapper.UserMapper;
import com.TingTing.repository.EmailVerificationTokenRepository;
import com.TingTing.repository.UserRepository;
import com.TingTing.util.CodeGenerator;
import com.TingTing.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // 만료시간(ms)
    private final long ACCESS_EXPIRE = 1000L * 60 * 60;          // 1 hour
    private final long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7 days

    // 로컬↔EC2처럼 "크로스-사이트"라면 None+Secure가 필수
    private static final String COOKIE_SAMESITE = "None"; // 크로스-사이트 전송 허용
    private static final boolean COOKIE_SECURE = true;    // SameSite=None이면 HTTPS 필수

    // ✅ 이메일 중복 확인
    public boolean isEmailExist(String email) {
        return userRepository.findByUsEmail(email).isPresent();
    }

    // ✅ 인증 코드 전송
    public ResponseDto sendVerificationEmail(String email) {
        if (isEmailExist(email)) {
            return new ResponseDto(false, "이미 가입된 이메일입니다.");
        }

        tokenRepository.deleteByEmail(email);

        String code = CodeGenerator.generateNumericCode(6);
        EmailVerificationToken token = EmailVerificationToken.builder()
                .email(email)
                .code(code)
                .verified(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build();

        tokenRepository.save(token);
        emailService.sendVerificationEmail(email, code);

        return new ResponseDto(true, "인증코드를 전송했습니다.");
    }

    // ✅ 인증 코드 확인
    public ResponseDto checkVerificationCode(String email, String code) {
        Optional<EmailVerificationToken> optional = tokenRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (optional.isEmpty()) {
            return new ResponseDto(false, "인증 요청이 없습니다.");
        }

        EmailVerificationToken token = optional.get();

        if (token.isVerified()) {
            return new ResponseDto(false, "이미 인증된 이메일입니다.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ResponseDto(false, "인증 코드가 만료되었습니다.");
        }
        if (!token.getCode().equals(code)) {
            return new ResponseDto(false, "인증 코드가 일치하지 않습니다.");
        }

        token.setVerified(true);
        token.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(token);

        return new ResponseDto(true, "이메일 인증이 완료되었습니다.");
    }

    // ✅ 이메일 인증 여부 확인
    public boolean isEmailVerified(String email) {
        return tokenRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .filter(EmailVerificationToken::isVerified)
                .filter(t -> t.getVerifiedAt().isAfter(LocalDateTime.now().minusHours(1)))
                .isPresent();
    }

    // ✅ 회원가입 처리
    public void signup(SignUpRequestDto request) {
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

    // ✅ 로그인 처리 — HttpOnly + SameSite=None + Secure 쿠키로 심기
    public TokenResponseDto logIn(SignInRequestDto dto, HttpServletResponse response) {
        User user = userRepository.findByUsEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getUsPw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsEmail());

        refreshTokenService.saveOrUpdate(user.getUsEmail(), refreshToken, REFRESH_EXPIRE);

        // Set-Cookie: SameSite=None; Secure; Path=/
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(Duration.ofMillis(ACCESS_EXPIRE))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(Duration.ofMillis(REFRESH_EXPIRE))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new TokenResponseDto(user.getUsNickname());
    }

    // ✅ 로그아웃 처리 — 쿠키 만료 + 리프레시 제거
    public void logOut(User user, HttpServletResponse response) {
        refreshTokenService.delete(user.getUsEmail());

        ResponseCookie expiredAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie expiredRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAMESITE)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefresh.toString());
    }

    // ✅ 임시 비밀번호 설정
    public void sendTemporaryPassword(String email) {
        Optional<User> optionalUser = userRepository.findByUsEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다.");
        }

        User user = optionalUser.get();

        String tempPassword = generateRandomPassword(10);
        String encodedPassword = passwordEncoder.encode(tempPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        String subject = "TingTing 임시 비밀번호 안내";
        String message = "안녕하세요, TingTing입니다.\n\n" +
                "임시 비밀번호는 다음과 같습니다:\n\n" +
                tempPassword + "\n\n" +
                "로그인 후 반드시 비밀번호를 변경해주세요.";
        emailService.sendEmail(user.getEmail(), subject, message);
    }

    // ✅ 임시 비밀번호 생성
    private String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{};:<>?";

        String allChars = upper + lower + digits + symbols;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 최소 1개씩 포함 보장
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));

        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 셔플링
        List<Character> charList = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(charList);
        StringBuilder finalPassword = new StringBuilder();
        charList.forEach(finalPassword::append);

        return finalPassword.toString();
    }
}



//package com.TingTing.service;
//
//import com.TingTing.dto.ResponseDto;
//import com.TingTing.dto.SignInRequestDto;
//import com.TingTing.dto.SignUpRequestDto;
//import com.TingTing.dto.TokenResponseDto;
//import com.TingTing.entity.User;
//import com.TingTing.entity.EmailVerificationToken;
//import com.TingTing.mapper.UserMapper;
//import com.TingTing.repository.EmailVerificationTokenRepository;
//import com.TingTing.repository.UserRepository;
//import com.TingTing.util.CodeGenerator;
//import com.TingTing.util.JwtTokenProvider;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class SignService {
//
//    private final UserRepository userRepository;
//    private final RefreshTokenService refreshTokenService;
//    private final JwtTokenProvider jwtTokenProvider;
//    private final EmailVerificationTokenRepository tokenRepository;
//    private final EmailService emailService;
//    private final PasswordEncoder passwordEncoder;
//
//    private final long ACCESS_EXPIRE = 1000 * 60 * 60;          // 1 hour
//    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7 days
//
//    // 👉 로컬 개발(HTTP/프록시)에서는 false, HTTPS 배포 시 true 로 바꿔주세요
//    private static final boolean SECURE_COOKIE = false;
//
//    // ✅ 이메일 중복 확인
//    public boolean isEmailExist(String email) {
//        return userRepository.findByUsEmail(email).isPresent();
//    }
//
//    // ✅ 인증 코드 전송
//    public ResponseDto sendVerificationEmail(String email) {
//        if (isEmailExist(email)) {
//            return new ResponseDto(false, "이미 가입된 이메일입니다.");
//        }
//
//        // 기존 인증 기록 삭제
//        tokenRepository.deleteByEmail(email);
//
//        // 인증 코드 생성 및 저장
//        String code = CodeGenerator.generateNumericCode(6);
//        EmailVerificationToken token = EmailVerificationToken.builder()
//                .email(email)
//                .code(code)
//                .verified(false)
//                .createdAt(LocalDateTime.now())
//                .expiresAt(LocalDateTime.now().plusMinutes(3))
//                .build();
//
//        tokenRepository.save(token);
//
//        // 이메일 전송
//        emailService.sendVerificationEmail(email, code);
//
//        return new ResponseDto(true, "인증코드를 전송했습니다.");
//    }
//
//    // ✅ 인증 코드 확인
//    public ResponseDto checkVerificationCode(String email, String code) {
//        Optional<EmailVerificationToken> optional = tokenRepository.findTopByEmailOrderByCreatedAtDesc(email);
//        if (optional.isEmpty()) {
//            return new ResponseDto(false, "인증 요청이 없습니다.");
//        }
//
//        EmailVerificationToken token = optional.get();
//
//        if (token.isVerified()) {
//            return new ResponseDto(false, "이미 인증된 이메일입니다.");
//        }
//        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            return new ResponseDto(false, "인증 코드가 만료되었습니다.");
//        }
//        if (!token.getCode().equals(code)) {
//            return new ResponseDto(false, "인증 코드가 일치하지 않습니다.");
//        }
//
//        token.setVerified(true);
//        token.setVerifiedAt(LocalDateTime.now());
//        tokenRepository.save(token);
//
//        return new ResponseDto(true, "이메일 인증이 완료되었습니다.");
//    }
//
//    // ✅ 이메일 인증 여부 확인
//    public boolean isEmailVerified(String email) {
//        return tokenRepository.findTopByEmailOrderByCreatedAtDesc(email)
//                .filter(EmailVerificationToken::isVerified)
//                .filter(t -> t.getVerifiedAt().isAfter(LocalDateTime.now().minusHours(1)))
//                .isPresent();
//    }
//
//    // ✅ 회원가입 처리
//    public void signup(SignUpRequestDto request) {
//        if (isEmailExist(request.getEmail())) {
//            throw new RuntimeException("이미 가입된 이메일입니다.");
//        }
//        if (!isEmailVerified(request.getEmail())) {
//            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
//        }
//        if (userRepository.existsByUsNickname(request.getNickname())) {
//            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
//        }
//
//        User user = UserMapper.toEntity(request, passwordEncoder);
//        userRepository.save(user);
//    }
//
//    // ✅ 로그인 처리 — 여기서 HttpOnly 쿠키를 확실히 심어줍니다
//    public TokenResponseDto logIn(SignInRequestDto dto, HttpServletResponse response) {
//        User user = userRepository.findByUsEmail(dto.getEmail())
//                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
//
//        if (!passwordEncoder.matches(dto.getPassword(), user.getUsPw())) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
//        }
//
//        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsEmail());
//        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsEmail());
//
//        refreshTokenService.saveOrUpdate(user.getUsEmail(), refreshToken, REFRESH_EXPIRE);
//
//        // 🔐 도메인 지정하지 않습니다. (프록시/로컬에서 localhost에 정상 저장)
//        addCookie(response, "accessToken", accessToken, (int) (ACCESS_EXPIRE / 1000), SECURE_COOKIE);
//        addCookie(response, "refreshToken", refreshToken, (int) (REFRESH_EXPIRE / 1000), SECURE_COOKIE);
//
//        // 응답 바디에 토큰을 내려줄 필요는 없음(쿠키로 인증)
//        // 여기서는 닉네임만 돌려 UI가 환영문구 등에 쓰게 함
//        return new TokenResponseDto(user.getUsNickname());
//    }
//
//    // ✅ 로그아웃 처리 — 쿠키 즉시 만료
//    public void logOut(User user, HttpServletResponse response) {
//        refreshTokenService.delete(user.getUsEmail());
//
//        removeCookie(response, "accessToken", SECURE_COOKIE);
//        removeCookie(response, "refreshToken", SECURE_COOKIE);
//    }
//
//    // ===== 쿠키 유틸 =====
//    private void addCookie(HttpServletResponse res, String name, String value, int maxAge, boolean secure) {
//        Cookie c = new Cookie(name, value);
//        c.setHttpOnly(true);
//        c.setPath("/");
//        c.setMaxAge(maxAge);
//        c.setSecure(secure); // 로컬 개발:false, HTTPS 배포:true
//        // c.setDomain(...)  // ❌ 절대 지정하지 마세요
//        res.addCookie(c);
//
//        // (선택) SameSite 설정이 꼭 필요하면 아래 헤더 방식으로 추가:
//        // res.addHeader("Set-Cookie",
//        //         name + "=" + value + "; Path=/; HttpOnly; Max-Age=" + maxAge + (secure ? "; Secure" : "") + "; SameSite=Lax");
//    }
//
//    private void removeCookie(HttpServletResponse res, String name, boolean secure) {
//        Cookie c = new Cookie(name, null);
//        c.setHttpOnly(true);
//        c.setPath("/");
//        c.setMaxAge(0);
//        c.setSecure(secure);
//        res.addCookie(c);
//    }
//
//    // ✅ 임시 비밀번호 설정
//    public void sendTemporaryPassword(String email) {
//        Optional<User> optionalUser = userRepository.findByUsEmail(email);
//        if (optionalUser.isEmpty()) {
//            throw new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다.");
//        }
//
//        User user = optionalUser.get();
//
//        String tempPassword = generateRandomPassword(10);
//        String encodedPassword = passwordEncoder.encode(tempPassword);
//        user.setPassword(encodedPassword);
//        userRepository.save(user);
//
//        String subject = "TingTing 임시 비밀번호 안내";
//        String message = "안녕하세요, TingTing입니다.\n\n" +
//                "임시 비밀번호는 다음과 같습니다:\n\n" +
//                tempPassword + "\n\n" +
//                "로그인 후 반드시 비밀번호를 변경해주세요.";
//        emailService.sendEmail(user.getEmail(), subject, message);
//    }
//
//    // ✅ 임시 비밀번호 생성
//    private String generateRandomPassword(int length) {
//        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        String lower = "abcdefghijklmnopqrstuvwxyz";
//        String digits = "0123456789";
//        String symbols = "!@#$%^&*()-_=+[]{};:<>?";
//
//        String allChars = upper + lower + digits + symbols;
//        SecureRandom random = new SecureRandom();
//        StringBuilder password = new StringBuilder();
//
//        // 최소 1개씩 포함 보장
//        password.append(upper.charAt(random.nextInt(upper.length())));
//        password.append(lower.charAt(random.nextInt(lower.length())));
//        password.append(digits.charAt(random.nextInt(digits.length())));
//        password.append(symbols.charAt(random.nextInt(symbols.length())));
//
//        for (int i = 4; i < length; i++) {
//            password.append(allChars.charAt(random.nextInt(allChars.length())));
//        }
//
//        // 셔플링
//        List<Character> charList = password.chars()
//                .mapToObj(c -> (char) c)
//                .collect(Collectors.toList());
//        Collections.shuffle(charList);
//        StringBuilder finalPassword = new StringBuilder();
//        charList.forEach(finalPassword::append);
//
//        return finalPassword.toString();
//    }
//}
