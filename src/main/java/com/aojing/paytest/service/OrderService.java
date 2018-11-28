package com.aojing.paytest.service;

import com.aojing.paytest.common.ServerResponse;
import com.aojing.paytest.pojo.OrderPojo;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author gexiao
 * @date 2018/11/27 13:53
 */
public interface OrderService {
    PageInfo<OrderPojo> findAll(int pageNum, int pageSize);

    OrderPojo findById(Integer orderId);

    public ServerResponse aliCallback(Map<String,String> params);
}
