package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.common.core.SysConstant;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.common.exception.GlobalException;
import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.SeckillUserKey;
import com.anthonyzero.seckill.common.utils.Md5Util;
import com.anthonyzero.seckill.dao.SeckillUserDao;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class SeckillUserService {

    @Autowired
    private SeckillUserDao seckillUserDao;
    @Autowired
    private RedisService redisService;

    /**
     * 登陆
     * @param response
     * @param loginVO
     * @return
     */
    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null) {
            throw new GlobalException(CodeMsgEnum.SERVER_ERROR);
        }
        //获取用户
        SeckillUser seckillUser = seckillUserDao.getUserById(Long.parseLong(loginVO.getMobile()));
        if (seckillUser == null) {
            throw new GlobalException(CodeMsgEnum.MOBILE_NOT_EXIST);
        }
        //验证密码
        String password = Md5Util.getMd5(loginVO.getPassword(), seckillUser.getSalt());
        if (!password.equals(seckillUser.getPassword())) {
            throw new GlobalException(CodeMsgEnum.PASSWORD_ERROR);
        }
        // redis 存用户信息
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisService.set(SeckillUserKey.token, token, seckillUser);
        // 生成cookie
        addCookie(response, token);
        return token;
    }

    /**
     * 生成cookie
     * @param response
     * @param token
     */
    private void addCookie(HttpServletResponse response, String token) {

        Cookie cookie = new Cookie(SysConstant.COOKIE_NAME_TOKEN, token);
        //cookie 过期时间跟redis 用户信息过期时间 一致
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 通过token获取用户信息
     * @param response
     * @param token
     * @return
     */
    public SeckillUser getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
        if (user != null) {
            redisService.set(SeckillUserKey.token, token, user);
            // 延迟有效时间
            addCookie(response, token);
        }

        return user;
    }
}
