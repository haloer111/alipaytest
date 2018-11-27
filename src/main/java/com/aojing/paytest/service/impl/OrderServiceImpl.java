package com.aojing.paytest.service.impl;

import com.aojing.paytest.config.ZfbInfoProperties;
import com.aojing.paytest.mapper.OrderPojoMapper;
import com.aojing.paytest.pojo.OrderPojo;
import com.aojing.paytest.service.OrderService;
import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gexiao
 * @date 2018/11/27 13:53
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderPojoMapper orderPojoMapper;





    @Override
    public PageInfo<OrderPojo> findAll(int pageNum, int pageSize) {
       /* //PageHelper.startPage(pageNum, pageSize);
        orderPojoMapper.selectByPrimaryKey(103);*/
        return null;
    }

    @Override
    public OrderPojo findById(Integer orderId) {
        OrderPojo orderPojo = orderPojoMapper.selectByPrimaryKey(orderId);
        return orderPojo;
    }

    public void toPay(){

      /*============start================*/
















    }
}
