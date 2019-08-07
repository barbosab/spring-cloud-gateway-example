package net.youqu.micro.service.enums;

/**
 * description:返回码枚举
 *
 * @author wangpeng
 * @date 2018/05/13
 */
public enum ResultCodeEnum {
    SGIN_EMPTY("6001", "签名为空"),
    PUBLICKEY_EMPTY("6002", "公钥为空"),
    PRIVATEKEY_EXPIRE("6003", "私钥过期"),
    SIGN_INVALID("6004", "验签不通过"),
    TIMEOUT("6005", "服务端超时");

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
