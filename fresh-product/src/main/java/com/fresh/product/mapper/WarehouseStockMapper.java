package com.fresh.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fresh.product.entity.WarehouseStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WarehouseStockMapper extends BaseMapper<WarehouseStock> {

    @Update("UPDATE warehouse_stock SET available_qty = available_qty - #{qty}, locked_qty = locked_qty + #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND available_qty >= #{qty} AND deleted = 0")
    int lockStock(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);

    @Update("UPDATE warehouse_stock SET available_qty = available_qty + #{qty}, locked_qty = locked_qty - #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND locked_qty >= #{qty} AND deleted = 0")
    int releaseStock(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);

    @Update("UPDATE warehouse_stock SET locked_qty = locked_qty - #{qty}, total_qty = total_qty - #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND locked_qty >= #{qty} AND deleted = 0")
    int deductStock(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);

    @Update("UPDATE warehouse_stock SET available_qty = available_qty + #{qty}, total_qty = total_qty + #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND deleted = 0")
    int restoreStock(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);

    @Update("UPDATE warehouse_stock SET available_qty = available_qty + #{qty}, total_qty = total_qty + #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND deleted = 0")
    int stockIn(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);

    @Update("UPDATE warehouse_stock SET available_qty = available_qty - #{qty}, total_qty = total_qty - #{qty}, update_time = NOW() " +
            "WHERE warehouse_id = #{warehouseId} AND sku_id = #{skuId} AND available_qty >= #{qty} AND deleted = 0")
    int stockOut(@Param("warehouseId") Long warehouseId, @Param("skuId") Long skuId, @Param("qty") Integer qty);
}
