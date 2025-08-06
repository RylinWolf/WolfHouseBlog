package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserSubDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import jakarta.validation.Valid;

/**
 * @author linexsong
 */
public interface UserService extends IService<User> {
    /**
     * 根据账号或邮箱查询
     *
     * @param s 账号或邮箱
     * @return 用户 Optional 对象
     */
    User findByAccountOrEmail(String s) throws Exception;

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息的Optional对象，如果未找到返回空Optional
     */
    User findByUserId(Long userId) throws Exception;

    /**
     * 检查是否存在指定账号或邮箱的用户
     *
     * @param s 要检查的账号或邮箱
     * @return 如果存在返回true，不存在返回false
     */
    Boolean hasAccountOrEmail(String s);

    /**
     * 创建新用户
     *
     * @param dto 用户注册数据传输对象，包含用户注册所需的所有信息
     * @return 用户创建是否成功，成功返回true，失败返回false
     */
    UserRegisterVo createUser(UserRegisterDto dto);

    /**
     * 生成账号
     *
     * @param username 用户昵称
     * @param codeLen  账号数字位长度
     * @return 用户账号
     */
    String generateAccount(String username, Integer codeLen);

    /**
     * 根据用户ID获取用户视图对象
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVo getUserVoById(Long id);

    /**
     * 更新当前登录用户的信息
     *
     * @param dto 用户更新信息数据传输对象，包含头像、个人状态、昵称、电话、邮箱和生日等信息
     * @return 更新后的用户视图对象
     */
    UserVo updateAuthedUser(@Valid UserDto dto) throws Exception;

    Boolean subsribe(UserSubDto dto);
}
