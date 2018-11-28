package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.ApyAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gexiao
 * @date 2018/11/28 16:51
 */
@Mapper
public interface PayAccountMapper {


    ApyAccount findByPayId(Integer id);
}
