package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.SeckillKey;
import com.anthonyzero.seckill.common.utils.Md5Util;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;

@Service
public class SeckillService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    //数学公式 运算符
    private static char[] ops = new char[] { '+', '-', '*' };

    /**
     * 秒杀
     * @param seckillUser
     * @param goodsVO
     * @return
     */
    @Transactional
    public OrderInfo seckill(SeckillUser seckillUser, GoodsVO goodsVO) {
        boolean reduceFlag = goodsService.reduceStock(goodsVO);
        if (reduceFlag) {
            return orderService.createOrder(seckillUser, goodsVO);
        }
        // 减库存失败 说明已经秒杀完了
        redisService.set(SeckillKey.isGoodsOver, "" + goodsVO.getId(), true);
        return null;
    }

    /**
     * 获取自己的秒杀结果
     * @param userId
     * @param goodsId
     * @return orderId:成功返回订单号 -1:秒杀失败 已经秒杀完了 0:排队中 正在异步下单中
     */
    public long getSeckillResult(Long userId, long goodsId) {

        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);

        if (seckillOrder != null) {
            // 秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean isOver = redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 生成验证码
     * @param seckillUser
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(SeckillUser seckillUser, long goodsId) {
        if (seckillUser == null || goodsId < 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        // create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode + "=", 8, 24);
        g.dispose();
        // 把计算结果存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, seckillUser.getId() + "," + goodsId, rnd);
        // 输出图片
        return image;
    }

    /**
     * 生成验证码字符串
     * @param rdm
     * @return
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        System.out.println(exp);
        return exp;
    }

    /**
     * ScriptEngineManager计算验证码的结果
     * @param exp
     * @return
     */
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 验证验证码是否正确
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer oldCode = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId,
                Integer.class);
        if (oldCode == null || oldCode - verifyCode != 0) {
            return false;
        }
        redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    /**
     * 检测地址（秒杀字符串）是否正确
     * @param seckillUser
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(SeckillUser seckillUser, long goodsId, String path) {
        if (seckillUser == null || StringUtils.isEmpty(path) || goodsId <= 0) {
            return false;
        }
        String oldPath = redisService.get(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId,
                String.class);
        return path.equals(oldPath);
    }

    /**
     * 生成秒杀地址
     * @param seckillUser
     * @param goodsId
     * @return
     */
    public String createSeckillPath(SeckillUser seckillUser, long goodsId) {
        String path = DigestUtils.md5Hex(UUID.randomUUID().toString().replaceAll("-", ""));
        redisService.set(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, path);
        return path;
    }

}
