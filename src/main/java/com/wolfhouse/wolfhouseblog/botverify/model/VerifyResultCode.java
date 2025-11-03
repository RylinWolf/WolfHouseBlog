package com.wolfhouse.wolfhouseblog.botverify.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;

/**
 * @author Rylin Wolf
 */
public enum VerifyResultCode {
    /** 通过 */
    PASS("T001", "服务端校验通过"),
    TEST_PASS("T005", "测试模式，配置通过"),

    /** 未通过 */
    FORBIDDEN_PASS("F001", "疑似攻击，风控未通过"),
    NO_VERIFY_PARAM("F002", "验证参数未传递"),
    ILLEGAL_VERIFY_PARAM("F003", "验证参数非法，注意，该参数不得修改"),
    TEST_UNPASS("F004", "测试模式，配置不通过"),
    ILLEGAL_SCENE_ID("F005", "场景 ID 不合法"),
    SCENE_ID_NOT_EXIST("F006", "场景 ID 不存在"),
    REPEATED("F008", "验证数据重复提交"),
    VIRTUAL_ENVIRONMENT("F009", "检测到虚拟设备环境，策略不允许"),
    IP_BANNED("F010", "同 IP 访问频率超出限制"),
    DEVICE_BANNED("F011", "同设备访问频率超出限制"),
    INCONSISTENT_SCENE_ID("F012", "服务端参数场景 ID 与前端配置不一致"),
    VERIFY_PARAM_WITHOUT_PARAM("F013", "验证参数中缺少参数"),
    NO_INITIAL_REQUEST("F014", "无初始化记录，可重试"),
    UNPASS("F015", "验证交互未通过"),
    URL_UNPASS("F016", "自定义 URL 验证策略未通过"),
    FORBIDDEN("F017", "疑似攻击，协议或参数异常"),
    V3_REPEATED_VERIFY_PARAM("F018", "V3 架构，验证参数重复使用"),
    V3_REQUEST_TIMEOUT("F019", "V3 架构，行为验证请求和业务验签请求间隔超出90秒"),
    V3_INCONSISTENT_SCENE_ID("F018", "V3 架构，场景 ID 不匹配"),

    /** 未知问题 */
    UNKNOWN(null, "未知错误");

    @EnumValue
    public final String code;
    @JsonValue
    public final String msg;

    VerifyResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static VerifyResultCode of(String code) {
        for (VerifyResultCode value : VerifyResultCode.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
