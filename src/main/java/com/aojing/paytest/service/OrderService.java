package com.aojing.paytest.service;

import com.aojing.paytest.pojo.OrderPojo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author gexiao
 * @date 2018/11/27 13:53
 */
public interface OrderService {
    PageInfo<OrderPojo> findAll(int pageNum, int pageSize);

    OrderPojo findById(Integer orderId);
}
