package com.mo.web.interceptor;

import com.mo.common.constant.JwtClaimsConstant;
import com.mo.common.properties.JwtProperties;
import com.mo.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class EmployeeJwtTokenInterceptor implements HandlerInterceptor {
    private final JwtProperties jwtProperties;
    @Autowired
    public EmployeeJwtTokenInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        String token = request.getHeader(jwtProperties.getEmployeeTokenName());

        if(token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "missing token");
            return false;
        }

        try{
            String secretKey = jwtProperties.getEmployeeSecretKey();
            Jws<Claims> jws = JwtUtil.parseJwt(secretKey, token);
            Claims claims = jws.getPayload();
            request.setAttribute("claims:", claims);
            Long id = claims.get(JwtClaimsConstant.EMPLOYEE_ID, Long.class);
            log.info("当前管理员： {}", id);
            return true;
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token invalid");
            log.error("JWT 验证失败: ", e);
            return false;
        }
    }
}
