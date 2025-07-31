package com.wolfhouse.wolfhouseblog.auth.service.verify;

import java.util.function.Predicate;

/**
 * 验证节点接口，提供了一系列验证相关的操作方法。
 * 实现此接口的类负责执行具体的验证逻辑，包括设置验证目标、验证规则和异常处理等功能。
 *
 * @param <T> 需要验证的数据类型参数
 * @author linexsong
 */
public interface VerifyNode<T> {

    /**
     * 设置需要验证的目标对象。
     * 该方法用于为验证节点指定要验证的数据对象，验证操作将在此对象上执行。
     *
     * @param target 需要进行验证的目标数据对象
     * @return 返回当前验证节点实例，支持链式调用
     */
    VerifyNode<T> target(T target);

    /**
     * 设置验证断言函数。
     * 该方法用于设置验证规则，通过传入的断言函数定义具体的验证逻辑。
     *
     * @param predicate 验证断言函数，用于定义验证规则
     * @return 返回当前验证节点实例，支持链式调用
     */
    VerifyNode<T> predicate(Predicate<T> predicate);

    /**
     * 设置验证失败时要抛出的异常。
     * 该方法用于定义验证失败时的异常处理逻辑。
     *
     * @param e 验证失败时要抛出的异常对象
     * @return 返回当前验证节点实例，支持链式调用
     */
    VerifyNode<T> exception(Exception e);

    VerifyNode<T> exception(String message);


    /**
     * 使用指定的断言函数执行验证操作。
     * 该方法允许在执行验证时临时指定一个断言函数，而不使用预设的验证规则。
     *
     * @param predicate 用于本次验证的断言函数，定义临时的验证规则
     * @return 验证结果，验证通过返回true，否则返回false
     */
    boolean verify(Predicate<T> predicate);

    /**
     * 执行验证操作。
     * 使用预设的断言函数对目标对象进行验证，不抛出异常。
     *
     * @return 验证结果，验证通过返回true，验证失败返回false
     */
    boolean verify();

    /**
     * 执行验证操作并处理自定义异常。
     * 使用预设的断言函数进行验证，验证失败时抛出指定的异常。
     * 该方法允许在验证时临时指定要抛出的异常对象。
     *
     * @param e 验证失败时要抛出的异常对象
     * @return 验证结果，验证通过返回true，验证失败则抛出指定的异常
     * @throws Exception 验证失败时抛出的异常
     */
    boolean verifyWithCustomE(Exception e) throws Exception;

    /**
     * 执行验证操作并使用预设的自定义异常。
     * 使用预设的断言函数进行验证，验证失败时抛出预先设置的自定义异常。
     *
     * @return 验证结果，验证通过返回true，验证失败则抛出预设的异常
     * @throws Exception 验证失败时抛出的异常
     */
    boolean verifyWithCustomE() throws Exception;


    /**
     * 执行验证操作并使用默认异常处理。
     * 使用预设的断言函数进行验证，验证失败时抛出默认的验证异常。
     *
     * @return 验证结果，验证通过返回true，验证失败抛出异常
     * @throws Exception 验证失败时抛出的默认验证异常
     */
    boolean verifyWithE() throws Exception;

    /**
     * 获取当前的异常对象。
     * 返回验证节点中设置的异常对象，该异常可能是预设的验证失败异常或自定义异常。
     *
     * @return 返回当前配置的异常对象，如果未设置则返回null
     */
    Exception getException();

    /**
     * 设置验证策略。
     * 用于配置验证节点的行为模式，决定验证失败时采用的处理方式，
     * 如是否抛出异常，是否使用自定义异常等。
     *
     * @param strategy 验证策略对象，定义验证失败时的处理行为
     */
    VerifyNode<T> setStrategy(VerifyStrategy strategy);

    /**
     * 设置自定义异常。
     * 配置验证失败时要使用的自定义异常对象，该异常将在验证失败且策略配置为使用自定义异常时抛出。
     *
     * @param e 要设置的自定义异常对象
     */
    VerifyNode<T> setCustomException(Exception e);

    /**
     * 获取当前验证策略。
     * 返回当前验证节点使用的验证策略，该策略决定了验证失败时的处理方式。
     * 可能的策略包括：普通验证、抛出异常、使用自定义异常等。
     *
     * @return 返回当前配置的验证策略对象
     */
    VerifyStrategy getStrategy();
}
