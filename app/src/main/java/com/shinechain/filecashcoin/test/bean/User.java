package com.shinechain.filecashcoin.test.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zjs on 2018/11/22 16:52
 * Describe:
 */
public class User {
	@SerializedName("user_name")
	private String userName;
	private String password;
	private String phone;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "User{" +
				"userName='" + userName + '\'' +
				", password='" + password + '\'' +
				", phone='" + phone + '\'' +
				'}';
	}
}
