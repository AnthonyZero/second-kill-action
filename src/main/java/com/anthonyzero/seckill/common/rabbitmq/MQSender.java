package com.anthonyzero.seckill.common.rabbitmq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送
 */
@Component
@Slf4j
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 入队
     * @param seckillMessage
     */
    public void sendSeckillMessage(SeckillMessage seckillMessage) {
        String msg = JSON.toJSONString(seckillMessage);
        log.info("send message:", msg);
        amqpTemplate.convertAndSend(RabbitMQConfig.SECKILL_QUEUE, msg);
    }
}
