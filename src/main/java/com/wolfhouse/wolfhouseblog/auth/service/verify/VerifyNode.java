package com.wolfhouse.wolfhouseblog.auth.service.verify;

import java.util.function.Predicate;

/**
 * 验证节点接口，用于执行验证逻辑。实现该接口的类负责具体验证操作的执行。
 *
 * @param <T> 需要验证的数据类型参数
 * @author linexsong
 */
public interface VerifyNode<T> {

    /**
     * 设置需要验证的目标对象。该方法用于为验证节点指定要验证的数据。
     *
     * @param target 需要进行验证的目标数据对象
     */
    VerifyNode<T> target(T target);

    /**
     * 使用指定断言创建验证节点
     *
     * @param predicate 验证断言函数
     * @return 返回新创建的验证节点
     */
    VerifyNode<T> predicate(Predicate<T> predicate);

    /**
     * 创建带异常处理的验证节点
     *
     * @param e 验证失败时抛出的异常
     * @return 返回新创建的验证节点
     */
    VerifyNode<T> exception(Exception e);


    /**
     * 执行验证操作，通过传入的断言函数进行验证
     *
     * @param predicate 负责验证的断言函数，用于定义具体的验证规则
     * @return 验证结果，验证通过返回true，否则返回false
     */
    boolean verify(Predicate<T> predicate);

    /**
     * 执行验证操作
     *
     * @return 验证结果，成功返回true
     */
    boolean verify();

    /**
     * 执行验证操作，若验证失败则抛出自定义异常。允许指定具体的异常类型和错误信息。
     *
     * @param e 验证失败时要抛出的异常对象
     * @return 验证结果，验证通过返回true，验证失败则抛出指定的异常
     */
    boolean verifyWithCustomE(Exception e) throws Exception;

    /**
     * 执行验证操作，若验证失败则抛出自定义异常。
     *
     * @return 验证结果，验证通过返回true，验证失败则抛出指定的异常
     */
    boolean verifyWithCustomE() throws Exception;


    /**
     * 执行验证操作，验证失败时抛出异常
     *
     * @return 验证结果，成功返回true，失败抛出异常
     */
    boolean verifyWithE() throws Exception;

    /**
     * 设置验证策略，用于决定验证失败时的处理方式
     *
     * @param strategy 验证策略对象，定义验证失败时的行为模式
     */
    void setStrategy(VerifyStrategy strategy);

    /**
     * 设置自定义异常和错误信息
     *
     * @param e 自定义异常对象
     */
    void setCustomException(Exception e);

    /**
     * 设置验证断言函数
     *
     * @param predicate 用于验证的断言函数
     */
    void setVerifyBy(Predicate<T> predicate);

    /**
     * 获取当前设置的自定义异常
     *
     * @return 返回设置的自定义异常对象
     */
    Exception getCustomException();

    /**
     * 获取当前验证策略，用于了解当前节点的验证行为模式
     *
     * @return 返回当前使用的验证策略对象，表示验证失败时的处理方式
     */
    VerifyStrategy getStrategy();
}
