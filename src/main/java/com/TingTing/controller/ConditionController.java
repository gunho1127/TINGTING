//package com.TingTing.controller;
//
//import com.TingTing.service.ConditionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/conditions")
//@RequiredArgsConstructor
//public class ConditionController {
//
//    private final ConditionService conditionService;
//
//    @PostMapping
//    public ResponseEntity<ConditionResponseDto> createCondition(
//            @RequestBody ConditionRequestDto requestDto,
//            @AuthenticationPrincipal UserDetailsImpl userDetails // JWT로 인증된 사용자 정보
//    ) {
//        Long userId = userDetails.getUser().getId();
//        ConditionResponseDto response = conditionService.saveCondition(userId, requestDto);
//        return ResponseEntity.ok(response);
//    }
//}
//
