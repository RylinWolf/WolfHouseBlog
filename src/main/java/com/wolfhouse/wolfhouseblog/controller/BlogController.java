package com.wolfhouse.wolfhouseblog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/blog")
@Tag(name = "博客接口")
public class BlogController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
