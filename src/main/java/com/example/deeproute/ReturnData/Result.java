package com.example.deeproute.ReturnData;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
    

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static  <T> Result<T> ok(T data){
        return new Result<T>(0,"OK",data);
    }

    public static<T> Result<T> error(int code, String message) {
        return new Result<T>(code, message,null);
    }

}
