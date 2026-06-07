package com.fresh.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别：0-女，1-男")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

}
