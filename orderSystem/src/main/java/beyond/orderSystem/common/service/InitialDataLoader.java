package beyond.orderSystem.common.service;

import beyond.orderSystem.member.domain.Role;
import beyond.orderSystem.member.dto.MemberSaveReqDto;
import beyond.orderSystem.member.repository.MemberRepository;
import beyond.orderSystem.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// CommandLineRunner를 상속함으로서 해당 컴포넌트가 스프링빈으로 등록되는 시점에 run메서드 실행
@Component
public class InitialDataLoader implements CommandLineRunner {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        if(memberRepository.findByEmail("admin@test.com").isEmpty()){
            memberService.memberCreate(MemberSaveReqDto.builder()
                            .email("admin@test.com")
                            .name("admin")
                            .password("12341234")
                            .role(Role.ADMIN)
                            .build());
        }
    }
}
