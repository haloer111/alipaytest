package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.OrderPojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.core.annotation.Order;

@Mapper
public interface OrderPojoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderPojo record);

    int insertSelective(OrderPojo record);

    OrderPojo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderPojo record);

    int updateByPrimaryKey(OrderPojo record);
    OrderPojo selectByOrderNo(Long orderNo);

}