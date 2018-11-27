package com.aojing.paytest.controller;
import com.aojing.paytest.handler.AliPayMessageHandler;
import com.aojing.paytest.interceptor.AliPayMessageInterceptor;
import com.egzosn.pay.common.bean.TransactionType;
import java.util.Date;
import com.egzosn.pay.common.bean.CurType;

import com.aojing.paytest.config.ZfbInfoProperties;
import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.common.util.sign.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * @author gexiao
 * @date 2018/11/27 15:09
 */

@RequestMapping("/ali/")
@Controller
@Slf4j
@EnableAutoConfiguration
public class OrderController {

    private PayService service = null;
    @Autowired
    ZfbInfoProperties zfbInfoProperties;
    @Resource
    private AutowireCapableBeanFactory spring;

    @PostConstruct
    public void init() {
        AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
        aliPayConfigStorage.setPid(zfbInfoProperties.getPid());
        aliPayConfigStorage.setAppId(zfbInfoProperties.getAppid());
        aliPayConfigStorage.setKeyPublic(
                zfbInfoProperties.getPublic_key());
        aliPayConfigStorage.setKeyPrivate(zfbInfoProperties.getPrivate_key());
        aliPayConfigStorage.setNotifyUrl(zfbInfoProperties.getCallback_url());
        aliPayConfigStorage.setReturnUrl(zfbInfoProperties.getCallback_url());
        aliPayConfigStorage.setSignType(zfbInfoProperties.getSign_type());
        //aliPayConfigStorage.setSeller("2088102169916436");
        aliPayConfigStorage.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfigStorage.setTest(true);

        //请求连接池配置
        HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
        //最大连接数
        httpConfigStorage.setMaxTotal(20);
        //默认的每个路由的最大连接数
        httpConfigStorage.setDefaultMaxPerRoute(10);
        service = new AliPayService(aliPayConfigStorage, httpConfigStorage);
        //增加支付回调消息拦截器
          service.addPayMessageInterceptor(new AliPayMessageInterceptor());
        //设置回调消息处理
          service.setPayMessageHandler(spring.getBean(AliPayMessageHandler.class));
    }

    /**
     * 获取二维码图像
     * 二维码支付
     * @param price       金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toQrPay.do", produces = "image/jpeg;charset=UTF-8")
    @ResponseBody
    public byte[] toAliQrPay(  ) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PayOrder payOrder = new PayOrder();
         payOrder.setSubject("葛潇订单title");
         payOrder.setBody("葛潇测试");
         payOrder.setPrice(new BigDecimal("0.01"));
         payOrder.setOutTradeNo(System.currentTimeMillis()+"");
         payOrder.setTransactionType(AliTransactionType.SWEEPPAY);
       // PayOrder payOrder = new PayOrder("订单title", "摘要", new BigDecimal(0.01) , UUID.randomUUID().toString().replace("-", ""));
        payOrder.setTransactionType(AliTransactionType.SWEEPPAY);

        ImageIO.write(service.genQrPay(
                payOrder ), "JPEG", baos);
        return baos.toByteArray();
    }



    /**
     * 支付回调地址
     *
     * @param request 请求
     *
     * @return 返回对应的响应码
     *
     * 业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看{@link com.egzosn.pay.common.api.PayService#setPayMessageHandler(com.egzosn.pay.common.api.PayMessageHandler)}
     *
     * 如果未设置 {@link com.egzosn.pay.common.api.PayMessageHandler} 那么会使用默认的 {@link com.egzosn.pay.common.api.DefaultPayMessageHandler}
     *
     */
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public String payBack(HttpServletRequest request) throws IOException {
        //业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看com.egzosn.pay.common.api.PayService.setPayMessageHandler()
        return service.payBack(request.getParameterMap(), request.getInputStream()).toMessage();
    }
}
