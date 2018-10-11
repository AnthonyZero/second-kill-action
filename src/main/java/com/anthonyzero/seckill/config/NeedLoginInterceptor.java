package com.anthonyzero.seckill.config;

import com.anthonyzero.seckill.common.annotation.NeedLogin;
import com.anthonyzero.seckill.common.core.SysConstant;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.common.exception.GlobalException;
import com.anthonyzero.seckill.common.utils.CookieUtil;
import com.anthonyzero.seckill.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class NeedLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private SeckillUserService seckillUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(o instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) o;
        Method method = handlerMethod.getMethod();
        NeedLogin needLogin = method.getAnnotation(NeedLogin.class);

        // 有 @NeedLogin 注解，需要认证
        if (needLogin != null) {
            String paramToken = request.getParameter(SysConstant.COOKIE_NAME_TOKEN);
            String cookieToken = CookieUtil.getCookieValue(request, SysConstant.COOKIE_NAME_TOKEN);

            if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
                throw new GlobalException(CodeMsgEnum.SERVER_ERROR);
            }
            String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
            if (seckillUserService.getUserByToken(response, token) == null) {
                throw new GlobalException(CodeMsgEnum.SESSION_ERROR);
            }
            return true;
        }
        return true;
    }



    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


}
