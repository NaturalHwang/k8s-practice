package beyond.orderSystem.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangePasswordDto {
    private String email;
    private String asIsPassword;
    private String toBePassword;
}
