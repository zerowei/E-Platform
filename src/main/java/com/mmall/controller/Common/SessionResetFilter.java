package com.mmall.controller.Common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionResetFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String cookie = CookieUtil.readLoginToken(request);

        if (!StringUtils.isEmpty(cookie)) {
            String userInfo = RedisShardedUtil.get(cookie);
            User user = JsonUtil.string2Obj(userInfo, User.class);
            if (user != null) {
                RedisShardedUtil.expire(cookie, Const.expireTime.sessionExpireTime);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
