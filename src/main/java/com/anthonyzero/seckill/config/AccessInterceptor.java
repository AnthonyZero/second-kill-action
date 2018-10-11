package com.anthonyzero.seckill.config;

import com.anthonyzero.seckill.common.annotation.AccessLimit;
import com.anthonyzero.seckill.common.core.CurrentUserContext;
import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.core.SysConstant;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.AccessKey;
import com.anthonyzero.seckill.common.utils.CookieUtil;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    private SeckillUserService seckillUserService;
    @Autowired
    private RedisService redisService;
    /**
     * 拦截之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            SeckillUser seckillUser = getUser(request, response);
            CurrentUserContext.setUser(seckillUser); //保持当前用户对象

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                // 方法上没有加AccessLimit注解 不用限制访问次数 直接返回true
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (seckillUser == null) {
                    render(response, CodeMsgEnum.SERVER_ERROR);
                    return false;
                }
                key = "_" + seckillUser.getId();
            }
            AccessKey accessKey = AccessKey.withExpire(seconds);
            // 查询访问的次数
            Integer count = redisService.get(accessKey, key, Integer.class);
            if (count == null) {
                // 键不存在 初始化
                redisService.set(accessKey, key, 1);
            } else if (count < maxCount) {
                redisService.incr(accessKey, key);
            } else {
                render(response, CodeMsgEnum.ACCESS_LIMIT_REACHED);
                return false;
            }

        }
        return true;
    }


    /**
     * 获取用户
     * @param request
     * @param response
     * @return
     */
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SysConstant.COOKIE_NAME_TOKEN);
        String cookieToken = CookieUtil.getCookieValue(request, SysConstant.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return seckillUserService.getUserByToken(response, token);
    }


    private void render(HttpServletResponse response, CodeMsgEnum codeMsgEnum) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String msg = Result.error(codeMsgEnum).toString();
        outputStream.write(msg.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
}
