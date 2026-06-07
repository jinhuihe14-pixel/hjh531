package com.fresh.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别：0-女，1-男")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "积分")
    private Integer integral;

    @Schema(description = "等级")
    private Integer level;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
