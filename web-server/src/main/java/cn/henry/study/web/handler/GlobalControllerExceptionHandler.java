package cn.henry.study.web.handler;

import cn.henry.study.web.base.BaseGlobalExceptionHandler;
import cn.henry.study.common.exceptions.BaseException;
import cn.henry.study.web.result.DefaultWebErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * @author zhumaer
 * @desc 统一异常处理器
 * @since 8/31/2017 3:00 PM
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler extends BaseGlobalExceptionHandler {

    /**
     * 处理400类异常
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public DefaultWebErrorResult handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public DefaultWebErrorResult handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        return super.handleConstraintViolationException(e, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public DefaultWebErrorResult handleBindException(BindException e, HttpServletRequest request) {
        return super.handleBindException(e, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DefaultWebErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return super.handleMethodArgumentNotValidException(e, request);
    }

    /**
     * 处理自定义异常
     */
    @Override
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<DefaultWebErrorResult> handleBusinessException(BaseException e, HttpServletRequest request) {
        return super.handleBusinessException(e, request);
    }

    /**
     * 处理运行时异常
     */
    @Override
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public DefaultWebErrorResult handleThrowable(Throwable e, HttpServletRequest request) {
        // TODO 可通过邮件、微信公众号等方式发送信息至开发人员、记录存档等操作
        return super.handleThrowable(e, request);
    }

}
