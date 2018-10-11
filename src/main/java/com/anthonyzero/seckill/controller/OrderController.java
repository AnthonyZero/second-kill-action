package com.anthonyzero.seckill.controller;

import com.anthonyzero.seckill.common.core.CurrentUserContext;
import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import com.anthonyzero.seckill.domain.OrderInfo;
import com.anthonyzero.seckill.domain.SeckillUser;
import com.anthonyzero.seckill.service.GoodsService;
import com.anthonyzero.seckill.service.OrderService;
import com.anthonyzero.seckill.vo.GoodsVO;
import com.anthonyzero.seckill.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订单
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;


    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/detail")
    public Result<OrderDetailVO> seckill(@RequestParam("orderId") long orderId) {
        if (CurrentUserContext.getUser() == null) {
            return Result.error(CodeMsgEnum.SESSION_ERROR);
        }

        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsgEnum.ORDER_NOT_EXIST);
        }

        long goodsId = order.getGoodsId();
        GoodsVO goodsVO = goodsService.getGoodsVOByGoodsId(goodsId);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrder(order);
        orderDetailVO.setGoods(goodsVO);

        return Result.success(orderDetailVO);
    }
}
