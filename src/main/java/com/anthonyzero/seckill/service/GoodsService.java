package com.anthonyzero.seckill.service;

import com.anthonyzero.seckill.dao.GoodsDao;
import com.anthonyzero.seckill.domain.SeckillGoods;
import com.anthonyzero.seckill.vo.GoodsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 获取商品信息
     * @param goodsId
     * @return
     */
    public GoodsVO getGoodsVOByGoodsId(long goodsId){
        return goodsDao.getGoodsVOByGoodsId(goodsId);
    }

    /**
     * 减少商品库存 -1
     * @param goods
     * @return
     */
    @Transactional
    public boolean reduceStock(GoodsVO goods) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        int executeLine = goodsDao.reduceStock(seckillGoods);
        return executeLine > 0;
    }
}
