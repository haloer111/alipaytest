package com.aojing.paytest.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.aojing.paytest.common.ServerResponse;
import com.aojing.paytest.form.QueryOrder;
import com.aojing.paytest.handler.AliPayMessageHandler;
import com.aojing.paytest.interceptor.AliPayMessageInterceptor;
import com.aojing.paytest.service.OrderService;
import com.egzosn.pay.common.bean.RefundOrder;
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
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Autowired
    OrderService orderService;

    @Resource
    private AutowireCapableBeanFactory spring;

    @PostConstruct
    public void init() {
       /* AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
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
        service.setPayMessageHandler(spring.getBean(AliPayMessageHandler.class));*/
    }

    /**
     * 获取二维码图像
     * 二维码支付
     *
     * @return 二维码图像
     */
    @RequestMapping(value = "toQrPay.do", produces = "image/jpeg;charset=UTF-8")
    @ResponseBody
    public byte[] toAliQrPay() throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //todo 进行数据库查询,根据userId,orderId查询 order
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject("葛潇订单title");
        payOrder.setBody("葛潇测试");
        payOrder.setPrice(new BigDecimal("0.01"));
        payOrder.setOutTradeNo("1492491089753");
        payOrder.setTransactionType(AliTransactionType.SWEEPPAY);

        ImageIO.write(service.genQrPay(
                payOrder), "JPEG", baos);
        return baos.toByteArray();
    }



    /**
     * 查询
     *
     * @param queryOrder 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
    @RequestMapping("query.do")
    @ResponseBody
    @Deprecated
    public ServerResponse<Map<String, Object>> query(QueryOrder queryOrder) {
        return ServerResponse.createBySuccess(service.query(queryOrder.getTradeNo(), queryOrder.getOutTradeNo()));
    }

    /**
     * 通用查询接口，根据 AliTransactionType 类型进行实现,此接口不包括退款
     *
     * @param order 订单的请求体
     * @return 返回支付方对应接口的结果
     */
    @RequestMapping("secondaryInterface")
    @ResponseBody
    public Map<String, Object> secondaryInterface(QueryOrder order) {
        TransactionType type = AliTransactionType.valueOf(order.getTransactionType());
        return service.secondaryInterface(order.getTradeNoOrBillDate(), order.getOutTradeNoBillType(), type);
    }

    /**
     * 交易关闭接口
     *
     * @param order 订单的请求体
     * @return 返回支付方交易关闭后的结果
     */
    @RequestMapping("close")
    public Map<String, Object> close(QueryOrder order) {



        return service.close(order.getTradeNo(), order.getOutTradeNo());
    }

    /**
     * 交易撤销接口
     *
     * @param order 订单的请求体
     * @return 返回支付方交易关闭后的结果
     */
    @RequestMapping("cancel")
    public Map<String, Object> cancel(QueryOrder order) {
        return service.cancel(order.getTradeNo(), order.getOutTradeNo());
    }

    /**
     * 申请退款接口
     *
     * @param order 订单的请求体
     * @return 返回支付方申请退款后的结果
     */
    @RequestMapping("refund.do")
    @ResponseBody
    public Map<String, Object> refund() {
        //todo[支付宝退款] 1.前台传支付宝流水号(tradeNo).商品id(outTradeNo
        //1. 校验数据
        //2. 进行退款操作
        //3. 将退款信息写入数据库
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundNo("1555333221");
        refundOrder.setTradeNo("2018112822001412710500531258");
        refundOrder.setOutTradeNo("1492091089753");
        refundOrder.setRefundAmount(new BigDecimal("0.01"));
        refundOrder.setTotalAmount(new BigDecimal("0.01"));
        refundOrder.setOrderDate(new Date());
        refundOrder.setCurType(CurType.CNY);
        refundOrder.setDescription("葛潇测试退款!!");

        Map result = service.refund(refundOrder);
        log.info("[支付宝退款] 结果:{}",result);

        return result;
    }

    /**
     * 查询退款
     *
     * @param refundOrder 订单的请求体
     * @return 返回支付方查询退款后的结果
     */
    @RequestMapping(value = "refund_query.do",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> refundQuery(RefundOrder refundOrder) {
        return service.refundquery(refundOrder);
    }


    /**
     * 支付回调地址
     *
     * @param request 请求
     * @return 返回对应的响应码
     * <p>
     * 业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler
     * ，详情查看
     * {@link com.egzosn.pay.common.api.PayService#setPayMessageHandler(com.egzosn.pay.common.api.PayMessageHandler)}
     * <p>
     * 如果未设置 {@link com.egzosn.pay.common.api.PayMessageHandler} 那么会使用默认的
     * {@link com.egzosn.pay.common.api.DefaultPayMessageHandler}
     */
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public String payBack(HttpServletRequest request) throws IOException {
        //业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看com.egzosn.pay.common.api.PayService
        // .setPayMessageHandler()
        // return service.payBack(request.getParameterMap(), request.getInputStream()).toMessage();


        //获取支付方返回的对应参数
        Map<String, String> params = service.getParameter2Map(request.getParameterMap(), request.getInputStream());
        if (null == params) {
            return service.getPayOutMessage("fail", "失败").toMessage();
        }

        log.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"),
                params.toString());

        //校验
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, zfbInfoProperties.getAlipay_public_key(),
                    "utf-8", zfbInfoProperties.getSign_type());
            log.info("[支付宝回调] RSA校验:{}", alipayRSACheckedV2);
            if (alipayRSACheckedV2) {
                //这里处理业务逻辑
                //......业务逻辑处理块........
                ServerResponse serverResponse = orderService.aliCallback(params);
                if (serverResponse.isSuccess()) {
                    return "success";
                }

            }
        } catch (AlipayApiException e) {
            log.error("[支付宝回调] RSA校验失败,params={}", params);
        }

        return service.getPayOutMessage("fail", "失败").toMessage();

    }


}
