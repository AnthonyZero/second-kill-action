package com.anthonyzero.seckill.common.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anthonyzero.seckill.domain.SeckillOrder;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.service.OrderService;
import com.anthonyzero.seckill.service.SeckillService;
import com.anthonyzero.seckill.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息接收
 */
@Component
@Slf4j
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_QUEUE)
    public void dealSeckillMessage(String message) {
        log.info("receive message :" + message);
        SeckillMessage seckillMessage = JSON.parseObject(message, new TypeReference<SeckillMessage>(){});
        SeckillUser user = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        // 判断库存
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
        if (goods.getStockCount() <= 0) {
            return;
        }

        // 判断用户是否重复秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (seckillOrder != null) {
            return;
        }

        // 减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goods);
    }
}
