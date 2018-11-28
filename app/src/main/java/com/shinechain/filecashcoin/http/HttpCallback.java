package com.shinechain.filecashcoin.http;

/**
 * Created by zjs on 2018/11/22 15:42
 * Describe:
 */
public abstract class HttpCallback<T> {

	public abstract void onSuccess(T t);

	public abstract void onFail(Exception e);

	public void onFinish(){

	}
}
