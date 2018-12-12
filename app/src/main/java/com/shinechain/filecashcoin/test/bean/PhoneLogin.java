package com.shinechain.filecashcoin.test.bean;

/**
 * Created by zjs on 2018/12/08 11:04
 * Describe:
 */
public class PhoneLogin {
    private String token;
    private Integer status;
    private Long code;
    private String msg;
    private Object object;
    private String jwt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public String toString() {
        return "PhoneLogin{" +
                "token='" + token + '\'' +
                ", status=" + status +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", object=" + object +
                ", jwt='" + jwt + '\'' +
                '}';
    }
}
