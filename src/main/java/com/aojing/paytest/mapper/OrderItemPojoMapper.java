package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.OrderItemPojo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemPojoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItemPojo record);

    int insertSelective(OrderItemPojo record);

    OrderItemPojo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItemPojo record);

    int updateByPrimaryKey(OrderItemPojo record);
}