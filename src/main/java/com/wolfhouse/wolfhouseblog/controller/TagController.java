package com.wolfhouse.wolfhouseblog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linexsong
 */
@RestController
@Tag(name = "常用标签接口")
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {
}
