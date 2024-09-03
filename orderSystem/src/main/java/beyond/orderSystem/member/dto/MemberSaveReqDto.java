package beyond.orderSystem.member.dto;

import beyond.orderSystem.common.domain.Address;
import beyond.orderSystem.member.domain.Member;
import beyond.orderSystem.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveReqDto {
    private String name;
    @NotEmpty(message = "email is essential")
    private String email;
    @NotEmpty(message = "password is essential") // 특수문자 포함 여부 검색해보셈
    @Size(min = 8, message = "비밀번호는 8자리 이상이어야합니다")
    private String password;
    private Address address; // 보낼 때 객체 안에 객체로 보내야됨.
//    private String city;
//    private String street;
//    private String zipcode;
    private Role role = Role.USER;

    public Member toEntity(String password){
//    public Member toEntity(String encodedPassword){
        Member member = Member.builder()
                .name(this.name)
                .email(this.email)
                .password(password)
//                .address(Address.builder()
//                        .city(this.city)
//                        .street(this.street)
//                        .zipcode(this.zipcode)
//                        .build())
                .role(this.role)
                .address(this.address)
                .build();
        return member;
    }
}
