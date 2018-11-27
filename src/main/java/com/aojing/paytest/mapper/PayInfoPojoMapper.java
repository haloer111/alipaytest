package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.PayInfoPojo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayInfoPojoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfoPojo record);

    int insertSelective(PayInfoPojo record);

    PayInfoPojo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfoPojo record);

    int updateByPrimaryKey(PayInfoPojo record);
}