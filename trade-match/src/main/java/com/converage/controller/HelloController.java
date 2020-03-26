package com.converage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@RestController
public class HelloController {

    @RequestMapping("hi")
    public String hi() {
        return "hi";
    }
}
