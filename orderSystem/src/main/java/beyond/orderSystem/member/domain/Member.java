package beyond.orderSystem.member.domain;

import beyond.orderSystem.common.domain.Address;
import beyond.orderSystem.common.domain.BaseTimeEntity;
import beyond.orderSystem.member.dto.MemberResDto;
import beyond.orderSystem.ordering.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Ordering> orderings;

    public MemberResDto fromEntity(){
        MemberResDto memberResDto = MemberResDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
//                .address(address)
                .address(this.address)
                .orderCount(this.orderings.size())
                .build();
        return memberResDto;
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }
}
