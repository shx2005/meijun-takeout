package com.mo.web.controller;

import com.mo.web.MoWebApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MoWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    @Autowired
    private PaymentController paymentController;

    @Test
    public void pay() {

    }
}
