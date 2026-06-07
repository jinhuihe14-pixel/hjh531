package com.fresh.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private Long total;

    public static <T> PageResult<T> of(List<T> list, Long total) {
        return new PageResult<>(list, total);
    }

}
