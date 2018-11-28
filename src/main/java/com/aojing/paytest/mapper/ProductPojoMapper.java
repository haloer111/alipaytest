package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.ProductPojo;
import com.aojing.paytest.pojo.ProductPojoWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductPojoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPojoWithBLOBs record);

    int insertSelective(ProductPojoWithBLOBs record);

    ProductPojoWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductPojoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ProductPojoWithBLOBs record);

    int updateByPrimaryKey(ProductPojo record);
}