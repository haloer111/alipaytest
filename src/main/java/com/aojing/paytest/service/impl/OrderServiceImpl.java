package com.aojing.paytest.service.impl;

import com.aojing.paytest.common.ServerResponse;
import com.aojing.paytest.mapper.OrderPojoMapper;
import com.aojing.paytest.mapper.PayInfoPojoMapper;
import com.aojing.paytest.pojo.OrderPojo;
import com.aojing.paytest.pojo.PayInfoPojo;
import com.aojing.paytest.service.OrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gexiao
 * @date 2018/11/27 13:53
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderPojoMapper orderPojoMapper;
    @Autowired
    PayInfoPojoMapper payInfoPojoMapper;


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

    @Override
    public ServerResponse aliCallback(Map<String, String> params) {

        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        OrderPojo order = orderPojoMapper.selectByOrderNo(orderNo);





















































































        //todo 1. 支付回调校验规则,2. 添加对于字段枚举
        // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        // 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能
        //    有多个seller_id/seller_email），
        // 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
        //    在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
        //    在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
        if (order == null) {
            return ServerResponse.createByErrorMessage("非本商城的订单,回调忽略");
        }
        if (order.getStatus() >= 20) {
            return ServerResponse.createByErrorMessage("支付宝重复调用");
        }
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            // order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(20);
            orderPojoMapper.updateByPrimaryKeySelective(order);
        }

        PayInfoPojo payInfo = new PayInfoPojo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(0);
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoPojoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }


}
