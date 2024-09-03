package beyond.orderSystem.member.domain;

import beyond.orderSystem.common.domain.Address;
import beyond.orderSystem.common.domain.BaseTimeEntity;
import beyond.orderSystem.member.dto.MemberResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    public MemberResDto fromEntity(){
        MemberResDto memberResDto = MemberResDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
//                .address(address)
                .address(this.address)
                .build();
        return memberResDto;
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }
}
