package com.shinechain.filecashcoin.test.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zjs on 2018/12/08 14:41
 * Describe:
 */
public class RegisterGeetest {

    /**
     * challenge : 9d489022506eb2644122ed0bce7b8714
     * gt : 8627047836713235234999b3b1d8b453
     * success : 1
     * user_id : fcc-geetest
     */

    private String challenge;
    private String gt;
    private Integer success;
    @SerializedName("user_id")
    private String userId;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RegisterGeetest{" +
                "challenge='" + challenge + '\'' +
                ", gt='" + gt + '\'' +
                ", success=" + success +
                ", userId='" + userId + '\'' +
                '}';
    }
}
