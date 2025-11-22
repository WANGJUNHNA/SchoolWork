package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 后端统一返回结果
 */

@Data
public class Result {

    private Integer code; //编码：1成功，0为失败
    private String msg; //错误信息
    private Object data; //数据

    //增加，删除，修改数据时，不需要给前端返回额外的数据，需要用到无参构造器
    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    //查询操作时候，需要返回查询到的数据，需要用到有参构造器
    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }
    // 注意：如果不用 @Data 注解，必须手动写以下代码：
    // 1. 无参构造（必需）
        public Result() {
        }
    //
    // 2. 有参构造（用于静态工厂方法）
     public Result(Integer code, String msg, Object data) {
         this.code = code;
         this.msg = msg;
         this.data = data;
     }
    //
    // 3. 所有字段的 getter 方法（必需）
     public Integer getCode() { return code; }
     public String getMsg() { return msg; }
     public Object getData() { return data; }

}
