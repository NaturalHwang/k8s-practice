package beyond.orderSystem.member.controller;

import beyond.orderSystem.common.auth.JwtTokenProvider;
import beyond.orderSystem.common.dto.CommonErrorDto;
import beyond.orderSystem.common.dto.CommonResDto;
import beyond.orderSystem.member.domain.Member;
import beyond.orderSystem.member.dto.*;
import beyond.orderSystem.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("2")
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, @Qualifier("2")RedisTemplate<String, Object> template){
        this.redisTemplate = template;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/member/create")
    public ResponseEntity<?> memberCreate(@Valid @RequestBody MemberSaveReqDto dto){
        Member member = memberService.memberCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED,
                "member is successfully created", "member number is " + member.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
//    admin만 회원목록 전체 조회 가능
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/list")
    public ResponseEntity<?> memberList(Pageable pageable){
        Page<MemberResDto> dtos = memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                "members are found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


//    본인은 본인 회원 정보만 조회 가능
//    /member/myinfo
    @GetMapping("/member/myinfo")
    public ResponseEntity<?> memberMyinfo(){
        MemberResDto memberResDto = memberService.memberDetail();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                "here is your info", memberResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PatchMapping("/member/reset-password")
    public ResponseEntity<?> memberResetPassword(@RequestBody MemberChangePasswordDto dto){
        memberService.changePassword(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                "password changed", HttpStatus.OK);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto){
//        email, password가 일치한 지 검증
        Member member = memberService.login(dto);
//        일치할 경우 accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole().toString());

//        redis에 email과 rt를 key:value로 하여 저장
        redisTemplate.opsForValue().set(member.getEmail(), refreshToken, 240, TimeUnit.HOURS); // 240시간

//        생성된 토큰을 CommonResDto에 담아 사용자에게 return
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("refreshToken", refreshToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Login is successful", loginInfo);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAccessToken(@RequestBody MemberRefreshDto dto){
        String rt = dto.getRefreshToken();
        Claims claims = null;
        try {
//            코드를 통해 rt 검증
            claims = Jwts.parser().setSigningKey(secretKeyRt).parseClaimsJws(rt).getBody();
        } catch (Exception e){
            return new ResponseEntity<>(new CommonErrorDto(
                    HttpStatus.BAD_REQUEST.value(), "invailid refresh token"), HttpStatus.BAD_REQUEST);
        }

        String email = claims.getSubject();
        String role = claims.get("role").toString();

//        redis를 조회하여 rt 추가 검증
        Object obj = redisTemplate.opsForValue().get(email);
        if(obj == null || !obj.toString().equals(rt)){
            return new ResponseEntity<>(
        new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "invalid token"), HttpStatus.BAD_REQUEST);
        }

        String newAt = jwtTokenProvider.createToken(email, role);

        Map<String, Object> info = new HashMap<>();
        info.put("token", newAt);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "A.T is renewed", info);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
