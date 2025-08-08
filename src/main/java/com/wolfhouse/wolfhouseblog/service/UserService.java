package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.domain.User;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserRegisterDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.UserSubDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserRegisterVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.UserVo;
import jakarta.validation.Valid;

/**
 * 用户服务接口
 *
 * @author linexsong
 */
public interface UserService extends IService<User> {
    /**
     * 根据账号或邮箱查询用户
     *
     * @param s 账号或邮箱
     * @return 用户对象，如果未找到则返回null
     * @throws Exception 查询过程中可能发生的异常
     */
    User findByAccountOrEmail(String s) throws Exception;

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户对象，如果未找到则返回null
     * @throws Exception 查询过程中可能发生的异常
     */
    User findByUserId(Long userId) throws Exception;

    /**
     * 检查指定的账号或邮箱是否已被使用
     *
     * @param s 要检查的账号或邮箱
     * @return 如果已存在返回true，不存在返回false
     */
    Boolean hasAccountOrEmail(String s);

    /**
     * 创建新用户并进行注册
     *
     * @param dto 用户注册数据传输对象，包含用户注册所需的所有信息
     * @return 用户注册结果视图对象
     * @throws Exception 注册过程中可能发生的异常
     */
    UserRegisterVo createUser(UserRegisterDto dto) throws Exception;

    /**
     * 根据用户昵称生成唯一账号。
     * 生成规则：使用用户昵称的拼音作为前缀，后面附加指定长度的随机数字
     *
     * @param username 用户昵称
     * @param codeLen  账号末尾随机数字的长度
     * @return 生成的唯一用户账号
     */
    String generateAccount(String username, Integer codeLen);

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息视图对象
     * @throws Exception 查询过程中可能发生的异常
     */
    UserVo getUserVoById(Long id) throws Exception;

    /**
     * 更新当前已认证用户的个人信息
     *
     * @param dto 用户更新信息数据传输对象，包含头像、个人状态、昵称、电话、邮箱和生日等信息
     * @return 更新后的用户详细信息视图对象
     * @throws Exception 更新过程中可能发生的异常
     */
    UserVo updateAuthedUser(@Valid UserDto dto) throws Exception;

    /**
     * 订阅其他用户
     *
     * @param dto 用户订阅数据传输对象
     * @return 订阅是否成功
     * @throws Exception 订阅过程中可能发生的异常
     */
    Boolean subsribe(UserSubDto dto) throws Exception;

    /**
     * 获取已订阅用户列表
     *
     * @param dto 分页查询参数
     * @return 已订阅用户简要信息的分页结果
     * @throws Exception 查询过程中可能发生的异常
     */
    PageResult<UserBriefVo> getSubscribedUsers(UserSubDto dto) throws Exception;

    /**
     * 检查是否已订阅指定用户
     *
     * @param dto 用户订阅数据传输对象
     * @return 如果已订阅返回true，否则返回false
     */
    Boolean isSubscribed(UserSubDto dto);

    /**
     * 删除指定用户账号
     *
     * @param userId 要删除的用户ID
     * @return 删除是否成功
     * @throws Exception 删除过程中可能发生的异常
     */
    Boolean deleteAccount(Long userId) throws Exception;
}
