package com.mo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.api.service.DeliveryService;
import com.mo.common.properties.AmapPropertities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final ObjectMapper objectMapper;

    public DeliveryServiceImpl( ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 计算距离
     * @author Anesidora
     * @param userLocation 用户位置经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @param merchantLocation 商家位置经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @return double
     */
    @Override
    public double calculateDistance(String userLocation, String merchantLocation) {
        String key = AmapPropertities.key;

        String url = "https://restapi.amap.com/v5/direction/bicycling?" +
                    "origin=" + userLocation + "&" +
                    "destination=" + merchantLocation + "&" +
                    "key=" + key;

        RestTemplate  restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode.get("route").get("paths").get(0).get("distance").asInt();
    }
}
