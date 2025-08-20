package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.BlogPermissionConstant;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/api/a/auth")
@PreAuthorize("@ss.hasRole('" + BlogPermissionConstant.SUPER_ADMIN + "')")
public class AuthorityController {
}
