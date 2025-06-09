package com.mo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/")
@Controller
public class IndexController {

    @GetMapping()
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index/index");
        modelAndView.addObject("title", "meijun-ordering");
        return modelAndView;
    }
}
