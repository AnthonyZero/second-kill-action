package com.anthonyzero.seckill.config;

import com.anthonyzero.seckill.common.core.CurrentUserContext;
import com.anthonyzero.seckill.common.core.SysConstant;
import com.anthonyzero.seckill.common.utils.CookieUtil;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.SeckillUserService;
import com.sun.org.apache.regexp.internal.REUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SeckillUserService seckillUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == SeckillUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//        //获取token
//        String paramToken = request.getParameter(SysConstant.COOKIE_NAME_TOKEN); //请求参数上的token
//        String cookieToken = CookieUtil.getCookieValue(request, SysConstant.COOKIE_NAME_TOKEN); //cookie中的token
//        if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
//            return null;
//        }
//        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
//        return seckillUserService.getUserByToken(response, token);
        return CurrentUserContext.getUser();
    }
}
