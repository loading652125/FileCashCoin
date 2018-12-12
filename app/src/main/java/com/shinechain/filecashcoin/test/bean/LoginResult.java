package com.shinechain.filecashcoin.test.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zjs on 2018/12/10 17:08
 * Describe:
 */
public class LoginResult extends Result {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        /**
         * IsOpenGoogle : 0
         * Uuid : 02d79cf7-d81d-f975-83b0-ee1104c50f42
         */

        @SerializedName("IsOpenGoogle")
        private int isOpenGoogle;
        @SerializedName("Uuid")
        private String uuid;

        public int getIsOpenGoogle() {
            return isOpenGoogle;
        }

        public void setIsOpenGoogle(int isOpenGoogle) {
            this.isOpenGoogle = isOpenGoogle;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
