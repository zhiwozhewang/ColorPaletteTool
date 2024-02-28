package com.whatscolors.demo.utils.http;

import java.io.Serializable;

/**
 * Description: <RespDTO><br>
 * Author:    mxdl<br>
 * Date:      2019/2/19<br>
 * Version:    V1.0.0<br>
 * Update:     <br>
 */
public class RespDTO<T> implements Serializable{


    public int code ;
    public String message = "";


    public T data;

    @Override
    public String toString() {
        return "RespDTO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setError(String error) {
        this.message = error;
    }
}
