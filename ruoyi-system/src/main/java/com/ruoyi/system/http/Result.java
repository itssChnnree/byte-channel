package com.ruoyi.system.http;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor// Lombok 插件注解。
public class Result<T> {

    // 状态码。
    private Integer code;

    // 信息。
    private String message;

    // 数据。
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public Result() {
    }

    public static <T> Result<T> build(Integer code, String message, T resultData) {

        Result<T> result = new Result<>();

        if (resultData != null){
            result.setData(resultData);
        }

        result.setCode(code);
        result.setMessage(message);

        return result;
    }

    public static Result success(){
        Result result = new Result();
        result.setCode(ResultCodeEnum.SUCCESS.getResultCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getResultMsg());
        return result;
    }

    public static<T> Result<T> success(T resultData){
        Result<T> result = build(ResultCodeEnum.SUCCESS.getResultCode(), ResultCodeEnum.SUCCESS.getResultMsg(), resultData);
        return result;
    }

    public static<T> Result<T> success(String msg){
        return build(ResultCodeEnum.SUCCESS.getResultCode(), msg, null);
    }

    public static Result fail(){
        Result result = new Result();
        result.setCode(ResultCodeEnum.FAIL.getResultCode());
        result.setMessage(ResultCodeEnum.FAIL.getResultMsg());
        return result;
    }

    //失败的方法
    public static<T> Result<T> fail(T resultData) {
        return build(ResultCodeEnum.FAIL.getResultCode(), ResultCodeEnum.FAIL.getResultMsg(), resultData);
    }

    public static<T> Result<T> fail(String msg) {
        return build(ResultCodeEnum.ERROR.getResultCode(), msg, null);
    }
}
