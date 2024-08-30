package beyond.orderSystem.member.service;

import beyond.orderSystem.member.domain.Member;
import beyond.orderSystem.member.dto.MemberChangePasswordDto;
import beyond.orderSystem.member.dto.MemberLoginDto;
import beyond.orderSystem.member.dto.MemberResDto;
import beyond.orderSystem.member.dto.MemberSaveReqDto;
import beyond.orderSystem.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Member memberCreate(MemberSaveReqDto dto){
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
//        if(dto.getPassword().length() < 8){
//            throw new IllegalArgumentException("비밀번호는 8자리 이상으로 설정해야합니다");
//        }
//        Member member = dto.toEntity();
//        memberRepository.save(member);
        return memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));

//        return member;
    }

    public Page<MemberResDto> memberList(Pageable pageable){
        Page<Member> members = memberRepository.findAll(pageable);
//        List<Member> memberList = memberRepository.findAll(); // --> List를 return하고 싶으면 이렇게 씀: 오버로딩
//        Page<MemberResDto> memberResDtos = members.map(a->a.fromEntity());
//        return memberResDtos;
//        return members.map(a->a.fromEntity());
        return members.map(Member::fromEntity); // 위의 주석 다 똑같은 return
    }

    public Member login(MemberLoginDto dto){
//        email 존재 여부
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
                ()-> new EntityNotFoundException("해당 이메일은 존재하지 않습니다"));
//        password 일치 여부
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public MemberResDto memberDetail(){
        Member member = memberRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
                () -> new EntityNotFoundException("조회 실패")
        );
        return member.fromEntity();
    }

    public Member changePassword(MemberChangePasswordDto dto){
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
                ()-> new EntityNotFoundException("해당 이메일은 존재하지 않습니다"));
        if(!passwordEncoder.matches(dto.getAsIsPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        String newPassword = dto.getToBePassword();
        member.changePassword(passwordEncoder.encode(newPassword));
        return member;
    }
}
