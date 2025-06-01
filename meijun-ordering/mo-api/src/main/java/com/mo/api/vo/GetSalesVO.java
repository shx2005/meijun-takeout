package com.mo.api.vo;

import com.mo.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "获取总商品销售信息VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSalesVO {
    List<Product> data;
}
