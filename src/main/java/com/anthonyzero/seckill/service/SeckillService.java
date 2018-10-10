package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.SeckillKey;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;

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

}
