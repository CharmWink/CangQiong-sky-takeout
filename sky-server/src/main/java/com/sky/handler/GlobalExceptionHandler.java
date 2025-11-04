package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException SQLex){
        if(SQLex.getMessage().contains("Duplicate entry")){
            //Duplicate entry 'admin' for key 'employee.idx_username'
            String message = SQLex.getMessage();
            String s = message.split(" ")[2].replace("'", "");
            log.error("异常信息：{}", message);
            return Result.error("用户名"+s+MessageConstant.ALREADY_EXISTS+"  请修改!");
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }

}
