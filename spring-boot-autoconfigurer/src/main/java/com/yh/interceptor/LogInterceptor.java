package com.yh.interceptor;

import com.yh.annotation.Log;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Log methodAnnotation = handlerMethod.getMethodAnnotation(Log.class);
        log.info("自定义的 starter 奏效了");
        if (methodAnnotation != null) {
            long start = System.currentTimeMillis();
            threadLocal.set(start);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Log methodAnnotation = handlerMethod.getMethodAnnotation(Log.class);
        if (methodAnnotation != null) {
            String requestURI = request.getRequestURI();
            String globalMethodName = method.getDeclaringClass().getName() + "#" + method.getName();
            String desc = methodAnnotation.desc();
            long end = System.currentTimeMillis();
            long start = (long)threadLocal.get();
            long dur = end - start;
            threadLocal.remove();
            log.info("请求路径{}，请求方法{}，描述信息{}，总计耗时{}", requestURI, globalMethodName, desc, dur);
        }
    }


}
