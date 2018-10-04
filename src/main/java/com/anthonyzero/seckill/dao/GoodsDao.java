package com.anthonyzero.seckill.dao;

import com.anthonyzero.seckill.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsDao {

    /**
     * 获取秒杀商品列表
     * @return
     */
    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_time, sg.end_time from seckill_goods sg left join goods g on sg.goods_id=g.id")
    List<GoodsVO> listGoodsVO();
}
