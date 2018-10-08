package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.common.enums.OrderChannelEnum;
import com.anthonyzero.seckill.common.enums.OrderStatusEnum;
import com.anthonyzero.seckill.common.redis.RedisService;
import com.anthonyzero.seckill.common.redis.key.OrderKey;
import com.anthonyzero.seckill.dao.OrderDao;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    /**
     * 获取秒杀订单
     * @param userId
     * @param goodsId
     * @return
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(Long userId, long goodsId) {
//        return orderDao.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
    }

    /**
     * 创建订单
     * @param seckillUser
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(SeckillUser seckillUser, GoodsVO goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateTime(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(OrderChannelEnum.PC.getCode());
        orderInfo.setStatus(OrderStatusEnum.NEW.getCode());
        orderInfo.setUserId(seckillUser.getId());
        orderDao.insert(orderInfo);

        // 用户ID + 商品ID 建立唯一unique索引 防止用户重复秒杀
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(seckillUser.getId());
        seckillOrder.setGoodsId(goods.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        // 秒杀订单 加入缓存
        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + seckillUser.getId() + "_" + goods.getId(),
                seckillOrder);

        return orderInfo;
    }

    /**
     * 通过订单ID获取订单信息
     * @param orderId
     * @return
     */
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
