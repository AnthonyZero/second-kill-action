package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.common.exception.GlobalException;
import com.anthonyzero.seckill.common.utils.Md5Util;
import com.anthonyzero.seckill.dao.SeckillUserDao;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    @Autowired
    private SeckillUserDao seckillUserDao;

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
        return "";
    }
}
