package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.dao.GoodsDao;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    /**
     * 获取秒杀商品列表
     * @return
     */
    public List<GoodsVO> listGoodsVO() {
        return goodsDao.listGoodsVO();
    }
}
