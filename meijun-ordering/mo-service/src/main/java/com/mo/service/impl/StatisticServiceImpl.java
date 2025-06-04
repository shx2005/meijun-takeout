package com.mo.service.impl;

import com.mo.api.service.StatisticService;
import com.mo.entity.Order;
import com.mo.entity.OrderDetail;
import com.mo.entity.Product;
import com.mo.service.mapper.OrderDetailMapper;
import com.mo.service.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {
    private final OrderDetailMapper orderDetailMapper;
    private final OrderMapper orderMapper;

    public StatisticServiceImpl(OrderDetailMapper orderDetailMapper, OrderMapper orderMapper) {
        this.orderDetailMapper = orderDetailMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<Product> getSales() {
        List<OrderDetail> details = orderDetailMapper.getAllOrderDetail();

        Map<Long, Product> map = details.stream()
                .map(detail -> {
                    Product product = Product.builder()
                            .productId(detail.getItemId())
                            .name(detail.getName())
                            .type(detail.getItemType())
                            .sales(detail.getQuantity())
                            .build();
                    return Map.of(detail.getItemId(), product);
                })
                .reduce(new HashMap<>(), (map1, map2) -> {
                    map2.forEach((k, v) -> map1.merge(k, v, (v1, v2) -> {
                        v1.setSales(v1.getSales() + v2.getSales());
                        return v1;
                    }));
                    return map1;
                });

        return map.values().stream().toList();
    }

    @Override
    public int getTraffic() {
        List<Order> list = orderMapper.getAll();

        return list.size();
    }

    @Override
    public double getSalesTotal() {
        List<Order> list = orderMapper.getAll();

        return list.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }
}
