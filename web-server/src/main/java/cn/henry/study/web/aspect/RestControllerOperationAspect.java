package cn.henry.study.web.aspect;

import cn.henry.study.web.configuration.DataSourceConfig;
import cn.henry.study.web.constants.HeaderConstants;
import cn.henry.study.web.database.DatabaseContextHolder;
import cn.henry.study.web.handler.GlobalControllerExceptionHandler;
import cn.henry.study.web.utils.IpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author zhumaer
 * @desc 请求参数、响应体统一日志打印
 * @since 10/10/2017 9:54 AM
 */
@Component
@Aspect
public class RestControllerOperationAspect {
    private static Logger logger = LoggerFactory.getLogger(RestControllerOperationAspect.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    /**
     * 环绕通知
     *
     * @param joinPoint 连接点
     * @return 切入点返回值
     * @throws Throwable 异常信息
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) " +
            "|| @annotation(org.springframework.web.bind.annotation.RestController)")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        boolean logFlag = this.needToLog(method);

        if (!logFlag) {
            // 设定指定数据源
            String routeKey = request.getParameter("routeKey");
            if (StringUtils.isEmpty(routeKey)) {
                routeKey = dataSourceConfig.defaultRouteKey();
            }
            DatabaseContextHolder.setRouteKey(routeKey);
            Object result = joinPoint.proceed();
            // 清除绑定的数据源
            DatabaseContextHolder.removeRouteKey();
            return result;
        }

        String ip = IpUtils.getRealIp(request);
        String methodName = this.getMethodName(joinPoint);
        String params = this.getParamsJson(joinPoint);

        String callSource = request.getHeader(HeaderConstants.CALL_SOURCE);
        String appVersion = request.getHeader(HeaderConstants.APP_VERSION);
        String apiVersion = request.getHeader(HeaderConstants.API_VERSION);
        String userAgent = request.getHeader("user-agent");

        logger.info("Started request method [{}] params [{}] IP [{}] callSource [{}] appVersion [{}] apiVersion [{}] userAgent [{}]",
                methodName, params, ip, callSource, appVersion, apiVersion, userAgent);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        logger.info("Ended request method [{}] params[{}] response is [{}] cost [{}] millis ",
                methodName, params, this.deleteSensitiveContent(result), System.currentTimeMillis() - start);
        return result;
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String shortMethodNameSuffix = "(..)";
        if (methodName.endsWith(shortMethodNameSuffix)) {
            methodName = methodName.substring(0, methodName.length() - shortMethodNameSuffix.length());
        }
        return methodName;
    }

    private String getParamsJson(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            //移除敏感内容
            String paramStr;
            if (arg instanceof HttpServletResponse) {
                paramStr = HttpServletResponse.class.getSimpleName();
            } else if (arg instanceof HttpServletRequest) {
                paramStr = HttpServletRequest.class.getSimpleName();
            } else if (arg instanceof MultipartFile) {
                long size = ((MultipartFile) arg).getSize();
                paramStr = MultipartFile.class.getSimpleName() + " size:" + size;
            } else {
                paramStr = this.deleteSensitiveContent(arg);
            }
            sb.append(paramStr).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private boolean needToLog(Method method) {
        // GET请求不记录日志
        return method.getAnnotation(GetMapping.class) == null
                && !method.getDeclaringClass().equals(GlobalControllerExceptionHandler.class);
    }

    /**
     * 删除参数中的敏感内容
     *
     * @param obj 参数对象
     * @return 去除敏感内容后的参数对象
     */
    private String deleteSensitiveContent(Object obj) {
        JSONObject jsonObject = new JSONObject();
        if (obj == null || obj instanceof Exception) {
            return jsonObject.toJSONString();
        }

        try {
            String param = JSON.toJSONString(obj);
            jsonObject = JSONObject.parseObject(param);
            List<String> sensitiveFieldList = this.getSensitiveFieldList();
            for (String sensitiveField : sensitiveFieldList) {
                if (jsonObject.containsKey(sensitiveField)) {
                    jsonObject.put(sensitiveField, "******");
                }
            }
        } catch (ClassCastException e) {
            return String.valueOf(obj);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 敏感字段列表
     */
    private List<String> getSensitiveFieldList() {
        List<String> sensitiveFieldList = Lists.newArrayList();
        sensitiveFieldList.add("pwd");
        sensitiveFieldList.add("password");
        return sensitiveFieldList;
    }
}
