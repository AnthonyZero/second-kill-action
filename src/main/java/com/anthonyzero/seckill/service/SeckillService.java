package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.domain.OrderInfo;
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
        return null;
    }
}
