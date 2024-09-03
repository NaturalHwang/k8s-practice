package beyond.orderSystem.member.repository;

import beyond.orderSystem.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
//    Page<Member> findAll(Pageable pageable); // 이거 지워도 작동함
}
