package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.ProductPojo;
import com.aojing.paytest.pojo.ProductPojoWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface ProductPojoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPojoWithBLOBs record);

    int insertSelective(ProductPojoWithBLOBs record);

    ProductPojoWithBLOBs selectByPrimaryKey(Integer id);
    List<ProductPojoWithBLOBs> selectAll();

    int updateByPrimaryKeySelective(ProductPojoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ProductPojoWithBLOBs record);

    int updateByPrimaryKey(ProductPojo record);
}