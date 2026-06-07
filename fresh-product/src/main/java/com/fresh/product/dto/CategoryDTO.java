package com.fresh.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分类DTO")
public class CategoryDTO {

    @Schema(description = "分类ID")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级分类ID")
    private Long parentId;

    @NotNull(message = "层级不能为空")
    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
