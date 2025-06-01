package com.mo.web.controller;

import com.mo.api.dto.AdminPageQueryDTO;
import com.mo.api.dto.AdminSaveDTO;
import com.mo.api.dto.AdminUpdateDTO;
import com.mo.api.service.AdminService;
import com.mo.api.service.OrderService;
import com.mo.common.enumeration.UserIdentity;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Admin;
import com.mo.entity.OrderDetail;
import com.mo.entity.Product;
import com.mo.service.mapper.AdminMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "管理员接口")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Operation(summary = "获取所有管理员")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Admin.class)))
    @GetMapping("/all")
    public Result<List<Admin>> getAll(){
        List<Admin> list = adminService.getAllAdmins();

        return Result.success(list);
    }

    @Operation(summary = "分页获取管理员")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Admin.class)))
    @GetMapping("/page")
    public PageResult getPage(AdminPageQueryDTO dto){
        int num = dto.getPageNum();
        int size = dto.getPageSize();
        int offset = (num - 1) * size;
        List<Admin> list = adminService.getPage(offset, size);

        return PageResult.success(list.size(), list, num, size);
    }

    @Operation(summary = "更新管理员")
    @Parameters({
            @Parameter(name = "adminUpdateDTO", description = "管理员更新参数", required = true, schema = @Schema(implementation = AdminUpdateDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/update")
    public Result<String> update(AdminUpdateDTO dto){
        Admin admin = new Admin();
        BeanUtils.copyProperties(dto, admin);
        adminService.updateAdmin(admin);

        return Result.success("更新成功");
    }

    @Operation(summary = "删除管理员")
    @Parameters({
            @Parameter(name = "id", description = "管理员id", required = true, schema = @Schema(implementation = Long.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id){
        adminService.delete(id);

        return Result.success();
    }

    @Operation(summary = "添加管理员")
    @Parameters({
            @Parameter(name = "admin", description = "管理员参数", required = true, schema = @Schema(implementation = Admin.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/add")
    public Result<String> add(@RequestBody AdminSaveDTO dto){
        Admin admin = new Admin();
        BeanUtils.copyProperties(dto, admin);
        admin.setIdentity(UserIdentity.ADMIN);

        adminService.saveAdmin(admin);

        return Result.success();
    }

    @GetMapping("/sales")
    public Result<List<Product>> getSales(Long userId){
        Map<Long, Product> productDict = new HashMap<>();
        for(var i:adminService.getAllOrderDetail()){
            Long id = i.getItemId();
            if(productDict.containsKey(id)){
                productDict.compute(i.getItemId(),(key, value) -> {
                    value.setSales(value.getSales() + i.getQuantity());
                    return value;
                });
            }
            else{
                Product product = new Product();
                product.setProductId(id);
                product.setName(i.getName());
                product.setSales(Long.valueOf(i.getQuantity()));
                product.setType(i.getItemType());
                productDict.put(id,product);
            }
        }
        return Result.success(productDict.values().stream().toList());
    }

    @GetMapping("/traffic")
    public Result<Long> getTraffic(Long userId){
        Long traffic = (long) adminService.getTraffic();
        return Result.success(traffic);
    }

    @GetMapping("/sales/total")
    public Result<BigDecimal> getSalesTotal(Long userId){
        BigDecimal total = adminService.getSalesTotal();
        return Result.success(total);
    }
}
