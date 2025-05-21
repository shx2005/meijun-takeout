package com.mo.web.config;

import com.mo.web.interceptor.AdminJwtTokenInterceptor;
import com.mo.web.interceptor.CustomerJwtInterceptor;
import com.mo.web.interceptor.EmployeeJwtTokenInterceptor;
import com.mo.web.interceptor.MerchantJwtTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AdminJwtTokenInterceptor adminJwtTokenInterceptor;
    @Autowired
    private CustomerJwtInterceptor customerJwtInterceptor;
    @Autowired
    private MerchantJwtTokenInterceptor merchantJwtTokenInterceptor;
    @Autowired
    private EmployeeJwtTokenInterceptor employeeJwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminJwtTokenInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns("/api/v1/admin/auth/login");
        registry.addInterceptor(customerJwtInterceptor)
                .addPathPatterns("/api/v1/customer/**")
                .excludePathPatterns("/api/v1/customer/auth/login");
        registry.addInterceptor(merchantJwtTokenInterceptor)
                .addPathPatterns("/api/v1/merchants/**")
                .excludePathPatterns("/api/v1/merchants/auth/login");
        registry.addInterceptor(employeeJwtTokenInterceptor)
                .addPathPatterns("/api/v1/employees/**")
                .excludePathPatterns("/api/v1/employees/auth/login");
    }

}
