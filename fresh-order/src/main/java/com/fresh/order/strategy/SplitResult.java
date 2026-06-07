package com.fresh.order.strategy;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SplitResult {

    private List<WarehouseAllocation> warehouseAllocations = new ArrayList<>();

    @Data
    public static class WarehouseAllocation {
        private Long warehouseId;
        private String warehouseName;
        private List<SkuAllocation> skuAllocations = new ArrayList<>();
    }

    @Data
    public static class SkuAllocation {
        private Long skuId;
        private Integer quantity;
    }

}
