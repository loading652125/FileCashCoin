package com.shinechain.filecashcoin.test.bean;

/**
 * Created by zjs on 2018/12/08 18:14
 * Describe:
 */
public class ValidateCode {

    /**
     * type : 1
     * to : +8618148758808
     * lang : 1
     * geetestChallenge : 6ec10a83d2c0767d5b08b3a54fa91241iz
     * geetestValidate : 14872032137006a308e242f71188d305
     * geetestSeccode : 14872032137006a308e242f71188d305|jordan
     * gtServerStatus : 1
     * geetestUserId : fcc-geetest
     */

    private int type;
    private String to;
    private int lang;
    private String geetestChallenge;
    private String geetestValidate;
    private String geetestSeccode;
    private String gtServerStatus;
    private String geetestUserId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getGeetestChallenge() {
        return geetestChallenge;
    }

    public void setGeetestChallenge(String geetestChallenge) {
        this.geetestChallenge = geetestChallenge;
    }

    public String getGeetestValidate() {
        return geetestValidate;
    }

    public void setGeetestValidate(String geetestValidate) {
        this.geetestValidate = geetestValidate;
    }

    public String getGeetestSeccode() {
        return geetestSeccode;
    }

    public void setGeetestSeccode(String geetestSeccode) {
        this.geetestSeccode = geetestSeccode;
    }

    public String getGtServerStatus() {
        return gtServerStatus;
    }

    public void setGtServerStatus(String gtServerStatus) {
        this.gtServerStatus = gtServerStatus;
    }

    public String getGeetestUserId() {
        return geetestUserId;
    }

    public void setGeetestUserId(String geetestUserId) {
        this.geetestUserId = geetestUserId;
    }
}
