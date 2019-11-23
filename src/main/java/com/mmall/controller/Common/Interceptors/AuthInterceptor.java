package com.mmall.controller.Common.Interceptors;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("start to preHandle the request");

        HandlerMethod method = (HandlerMethod) o;
        String methodName = method.getMethod().getName();
        String className = method.getBean().getClass().getSimpleName();
        StringBuilder paramsBuilder = new StringBuilder();
        Map<String, String[]> paramsMap = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
            String value = Arrays.toString(entry.getValue());
            paramsBuilder.append(entry.getKey()).append("=").append(value).append(",");
        }
        if (paramsBuilder.length() != 0) paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);

        String cookie = CookieUtil.readLoginToken(httpServletRequest);

        if (className.equals("UserManagerController") && methodName.equals("login")) {
            log.info("拦截器拦截到{}类的{}方法，移至controller执行", className, methodName);
            return true;
        }
        log.info("拦截器拦截到{}类的{}方法", className, methodName);

        User user = null;
        if (StringUtils.isNotEmpty(cookie)) {
            String userInfo = RedisShardedUtil.get(cookie);
            user = JsonUtil.string2Obj(userInfo, User.class);
        }
        if (user == null || !user.getRole().equals(Const.SUPERUSER)) {
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = httpServletResponse.getWriter();

            if (user == null) {
                if (className.equals("ProductManagerController") && methodName.equals("uploadRichText")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("message", "用户未登录,请先登录管理员");
                    writer.print(JsonUtil.obj2String(resultMap));
                } else {
                    writer.print(JsonUtil.obj2String(ReturnResponse.ReturnErrorByMessage("用户未登录,请先登录")));
                }
            } else {
                if (className.equals("ProductManagerController") && methodName.equals("uploadRichText")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("success", false);
                    resultMap.put("message", "无权限操作,需要管理员权限");
                    writer.print(JsonUtil.obj2String(resultMap));
                } else {
                    writer.print(JsonUtil.obj2String(ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限")));
                }
            }
            writer.flush();
            writer.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("start the postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("start the afterCompletion");
    }
}
