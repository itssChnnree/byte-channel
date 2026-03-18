package com.ruoyi.web.exceptionHandle;

import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import com.ruoyi.system.http.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * [mysql相关异常全局处理]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/1/18
 */
@RestControllerAdvice
@Slf4j
public class MysqlExceptionHandler {

    // 处理特定异常
    @ExceptionHandler({MySQLTimeoutException.class, MySQLTransactionRollbackException.class, TransactionTimedOutException.class})
    public Result handleResourceNotFound(MySQLTimeoutException ex) {
        log.error("mysql连接超时{}",ex.getMessage());
        return Result.fail("当前系统繁忙，请稍后再试");
    }

}
