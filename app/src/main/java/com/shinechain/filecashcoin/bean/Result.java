package com.shinechain.filecashcoin.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zjs on 2018/12/10 17:05
 * Describe:
 */
public class Result {

    /**
     * status : 200
     * code : 10000
     * msg : 登录成功
     */

    private int status;
    private int code;
    private String msg;
    @SerializedName("dev_info")
    private String devIfno;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDevIfno() {
        return devIfno;
    }

    public void setDevIfno(String devIfno) {
        this.devIfno = devIfno;
    }
}
