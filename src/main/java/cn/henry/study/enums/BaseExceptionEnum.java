package cn.henry.study.enums;

import cn.henry.study.base.BaseException;
import cn.henry.study.exceptions.*;
import cn.henry.study.result.ResultCode;
import org.springframework.http.HttpStatus;

/**
 * @author zhumaer
 * @desc 异常、HTTP状态码、默认自定义返回码 映射类
 * @since 9/21/2017 14:11 PM
 */
public enum BaseExceptionEnum {

    /**
     * 无效参数
     */
    PARAMETER_INVALID(ParameterInvalidException.class, HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID),

    /**
     * 数据未找到
     */
    NOT_FOUND(DataNotFoundException.class, HttpStatus.NOT_FOUND, ResultCode.RESULE_DATA_NONE),

    /**
     * 接口方法不允许
     */
    METHOD_NOT_ALLOWED(MethodNotAllowException.class, HttpStatus.METHOD_NOT_ALLOWED, ResultCode.INTERFACE_ADDRESS_INVALID),

    /**
     * 数据已存在
     */
    CONFLICT(DataConflictException.class, HttpStatus.CONFLICT, ResultCode.DATA_ALREADY_EXISTED),

    /**
     * 远程访问时错误
     */
    REMOTE_ACCESS_ERROR(RemoteAccessException.class, HttpStatus.INTERNAL_SERVER_ERROR, ResultCode.INTERFACE_OUTTER_INVOKE_ERROR),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(InternalServerException.class, HttpStatus.INTERNAL_SERVER_ERROR, ResultCode.SYSTEM_INNER_ERROR);

    private Class<? extends BaseException> eClass;

    private HttpStatus httpStatus;

    private ResultCode resultCode;

    BaseExceptionEnum(Class<? extends BaseException> eClass, HttpStatus httpStatus, ResultCode resultCode) {
        this.eClass = eClass;
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
    }

    public Class<? extends BaseException> getEClass() {
        return eClass;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public static boolean isSupportHttpStatus(int httpStatus) {
        for (BaseExceptionEnum exceptionEnum : BaseExceptionEnum.values()) {
            if (exceptionEnum.httpStatus.value() == httpStatus) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSupportException(Class<?> z) {
        for (BaseExceptionEnum exceptionEnum : BaseExceptionEnum.values()) {
            if (exceptionEnum.eClass.equals(z)) {
                return true;
            }
        }

        return false;
    }

    public static BaseExceptionEnum getByHttpStatus(HttpStatus httpStatus) {
        if (httpStatus == null) {
            return null;
        }

        for (BaseExceptionEnum exceptionEnum : BaseExceptionEnum.values()) {
            if (httpStatus.equals(exceptionEnum.httpStatus)) {
                return exceptionEnum;
            }
        }

        return null;
    }

    public static BaseExceptionEnum getByEClass(Class<? extends BaseException> eClass) {
        if (eClass == null) {
            return null;
        }

        for (BaseExceptionEnum exceptionEnum : BaseExceptionEnum.values()) {
            if (eClass.equals(exceptionEnum.eClass)) {
                return exceptionEnum;
            }
        }

        return null;
    }
}