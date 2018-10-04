package com.anthonyzero.seckill.vo;

import com.anthonyzero.seckill.domain.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsVO extends Goods {

	private Double seckillPrice;
	private Integer stockCount;
	private Date startTime;
	private Date endTime;

}
