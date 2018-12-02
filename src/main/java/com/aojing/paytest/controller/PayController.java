package com.aojing.paytest.controller;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.aojing.paytest.common.PayResponse;
import com.aojing.paytest.common.ServerResponse;
import com.aojing.paytest.enums.PayIdEnum;
import com.aojing.paytest.enums.PayType;
import com.aojing.paytest.form.QueryOrder;
import com.aojing.paytest.handler.AliPayMessageHandler;
import com.aojing.paytest.service.ApyAccountService;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.api.PayMessageInterceptor;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.*;
import com.egzosn.pay.common.http.UriVariables;
import com.egzosn.pay.common.util.MatrixToImageWriter;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.union.api.UnionPayConfigStorage;
import com.egzosn.pay.union.api.UnionPayService;
import com.egzosn.pay.union.bean.UnionTransactionType;
import com.egzosn.pay.wx.bean.WxTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 发起支付入口
 *
 * @author: egan
 * @email egzosn@gmail.com
 * @date 2016/11/18 0:25
 */
@Controller
@RequestMapping("/pay/")
@Slf4j
public class PayController {

    @Resource
    private ApyAccountService service;

    /*@RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("/index.html");
    }*/


    /**
     * 跳到支付页面
     * 针对实时支付,即时付款
     *
     * @param payId           账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param bankType        针对刷卡支付，卡的类型，类型值
     * @param price           金额
     * @return 跳到支付页面
     */
    @RequestMapping(value = "toPay.html", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toPay(HttpServletRequest request, Integer payId, String transactionType, String bankType,
                        BigDecimal price) {
        payId = 3;
        transactionType = UnionTransactionType.WEB.getType();
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);

        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price,
                UUID.randomUUID().toString().replace("-", ""),
                PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(transactionType));
        // ------  微信H5使用----
        order.setSpbillCreateIp(request.getHeader("X-Real-IP"));
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        order.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ? requestURL.indexOf("/") :
                requestURL.length()));
        //设置网页名称
        order.setWapName("在线充值");
        // ------  微信H5使用----

        //此处只有刷卡支付(银行卡支付)时需要
        if (StringUtils.isNotEmpty(bankType)) {
            order.setBankType(bankType);
        }
        Map orderInfo = payResponse.getService().orderInfo(order);
        return payResponse.getService().buildRequest(orderInfo, MethodType.POST);
    }

    /**
     * 跳到支付页面
     * 针对实时支付,即时付款
     *
     * @return 跳到支付页面
     */
    @RequestMapping(value = "toWxPay.html", produces = "text/html;charset=UTF-8")
    public String toWxPay(HttpServletRequest request) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(2);

        PayOrder order = new PayOrder("订单title", "摘要", new BigDecimal(0.01), UUID.randomUUID().toString().replace("-"
                , ""), WxTransactionType.MWEB);
        order.setSpbillCreateIp(request.getHeader("X-Real-IP"));
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        order.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ? requestURL.indexOf("/") :
                requestURL.length()));
        //设置网页名称
        order.setWapName("在线充值");

        Map orderInfo = payResponse.getService().orderInfo(order);
        return payResponse.getService().buildRequest(orderInfo, MethodType.POST);
    }


    /**
     * 公众号支付
     *
     * @param payId  账户id
     * @param openid openid
     * @param price  金额
     * @return 返回jsapi所需参数
     */
    @RequestMapping(value = "jsapi")
    public Map toPay(Integer payId, String openid, BigDecimal price) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);

        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price,
                UUID.randomUUID().toString().replace("-", ""),
                PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType("JSAPI"));
        order.setOpenid(openid);

        Map orderInfo = payResponse.getService().orderInfo(order);
        orderInfo.put("code", 0);

        return orderInfo;
    }


    /**
     * 刷卡付,pos主动扫码付款(条码付)
     *
     * @param payId           账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param authCode        授权码，条码等
     * @param price           金额
     * @return 支付结果
     */
    @RequestMapping(value = "microPay")
    public Map<String, Object> microPay(Integer payId, String transactionType, BigDecimal price, String authCode) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);

        PayOrder order = new PayOrder("huodull order", "huodull order", null == price ? new BigDecimal(0.01) : price,
                UUID.randomUUID().toString().replace("-", ""),
                PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(transactionType));
        //设置授权码，条码等
        order.setAuthCode(authCode);
        //支付结果
        Map<String, Object> params = payResponse.getService().microPay(order);
        PayConfigStorage storage = payResponse.getService().getPayConfigStorage();
        //校验
        if (payResponse.getService().verify(params)) {
            PayMessage message = new PayMessage(params, storage.getPayType(), storage.getMsgType().name());
            //支付校验通过后的处理
            payResponse.getRouter().route(message);
        }
        //这里开发者自行处理
        return params;
    }

    /**
     * 获取二维码图像
     * 二维码支付
     *
     * @param payId           账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param price           金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toQrPay.do", produces = "image/jpeg;charset=UTF-8")
    @ResponseBody
    public byte[] toWxQrPay(Integer payId, String transactionType, BigDecimal price) throws IOException {
        //手动测试传参
        //支付宝
      /*  payId = 1;
        transactionType = AliTransactionType.SWEEPPAY.getType();*/
        //银联
        payId = 3;
        transactionType = UnionTransactionType.APPLY_QR_CODE.getType();


        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //todo 进行数据库查询,根据userId,orderId查询 order
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject("订单title");
        payOrder.setBody("葛潇测试");
        payOrder.setPrice(new BigDecimal("0.01"));
        payOrder.setOutTradeNo(System.currentTimeMillis() + "");
        payOrder.setTransactionType(PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(transactionType));

        ImageIO.write(payResponse.getService().genQrPay(payOrder),
                "JPEG",
                baos);
        return baos.toByteArray();
    }

    /**
     * 获取一码付二维码图像
     * 二维码支付
     *
     * @param wxPayId  微信账户id
     * @param aliPayId 支付宝id
     * @param price    金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toWxAliQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
    public byte[] toWxAliQrPay(Integer wxPayId, Integer aliPayId, BigDecimal price, HttpServletRequest request) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里为需要生成二维码的地址
        StringBuffer url = request.getRequestURL();
        url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
        url.append("/toWxAliPay.html?");
        if (null != wxPayId) {
            url.append("wxPayId=").append(wxPayId).append("&");
        }
        if (null != aliPayId) {
            url.append("aliPayId=").append(aliPayId).append("&");
        }
        url.append("price=").append(price);

        ImageIO.write(MatrixToImageWriter.writeInfoToJpgBuff(url.toString()), "JPEG", baos);
        return baos.toByteArray();
    }

    /**
     * 支付宝与微信平台的判断 并进行支付的转跳
     *
     * @param wxPayId  微信账户id
     * @param aliPayId 支付宝id
     * @param price    金额
     * @return 支付宝与微信平台的判断
     */
    @RequestMapping(value = "toWxAliPay.html", produces = "text/html;charset=UTF-8")
    public String toWxAliPay(Integer wxPayId, Integer aliPayId, BigDecimal price, HttpServletRequest request) throws IOException {
        StringBuilder html = new StringBuilder();

        //订单
        PayOrder payOrder = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price,
                System.currentTimeMillis() + "");
        String ua = request.getHeader("user-agent");
        if (ua.contains("MicroMessenger")) {
            payOrder.setTransactionType(WxTransactionType.NATIVE);
            PayService service = this.service.getPayResponse(wxPayId).getService();
            return String.format("<script type=\"text/javascript\">location.href=\"%s\"</script>",
                    (String) service.orderInfo(payOrder).get("code_url"));
        }
        if (ua.contains("AlipayClient")) {
            payOrder.setTransactionType(AliTransactionType.SWEEPPAY);
            AliPayService service = (AliPayService) this.service.getPayResponse(aliPayId).getService();
            JSONObject result =
                    service.getHttpRequestTemplate().postForObject(service.getReqUrl() + "?" + UriVariables.getMapToParameters(service.orderInfo(payOrder)), null, JSONObject.class);
            result = result.getJSONObject("alipay_trade_precreate_response");
            return String.format("<script type=\"text/javascript\">location.href=\"%s\"</script>", result.getString(
                    "qr_code"));
        }

        return String.format("<script type=\"text/javascript\">alert(\"请使用微信或者支付宝App扫码%s\");window.close();</script>"
                , ua);


    }


    /**
     * 获取支付预订单信息
     *
     * @param payId           支付账户id
     * @param transactionType 交易类型
     * @return 支付预订单信息
     */
    @RequestMapping(value = "getOrderInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getOrderInfo(Integer payId, String transactionType, BigDecimal price) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        Map<String, Object> data = new HashMap<>();
        data.put("code", 0);
        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price,
                UUID.randomUUID().toString().replace("-", ""),
                PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(transactionType));
        data.put("orderInfo", payResponse.getService().orderInfo(order));
        return data;
    }


    /**
     * 支付回调地址 方式一
     * <p> http://yu7v4p.natappfree.cc/pay/alipay_callback.do
     * 方式二，{@link #payBack(HttpServletRequest, Integer)} 是属于简化方式， 试用与简单的业务场景
     *
     * @param request
     * @param payId
     * @return 支付是否成功
     */
    @RequestMapping(value = "no_alipay_callback{payId}.do")
    @ResponseBody
    @Deprecated
    public String payBackOne(HttpServletRequest request, @PathVariable Integer payId) throws IOException {
        //根据账户id，获取对应的支付账户操作工具
        PayResponse payResponse = service.getPayResponse(payId);
        PayConfigStorage storage = payResponse.getStorage();
        //获取支付方返回的对应参数
        Map<String, Object> params = payResponse.getService().getParameter2Map(request.getParameterMap(),
                request.getInputStream());

        if (null == params) {
            return payResponse.getService().getPayOutMessage("fail", "失败").toMessage();
        }

        //支付宝校验需要单独处理
        if (payId == 1) {
            Map<String, String> params1 = (Map) params;
            log.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"),
                    params.toString());

            //校验
            params.remove("sign_type");
            try {
                boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params1, storage.getKeyPublic(),
                        "utf-8", storage.getSignType());
                log.info("[支付宝回调] RSA校验:{}", alipayRSACheckedV2);
                if (alipayRSACheckedV2) {
                    //这里处理业务逻辑
                    //......业务逻辑处理块........
                    // ServerResponse serverResponse = orderService.aliCallback(params);
                    /*if (serverResponse.isSuccess()) {
                        return "success";
                    }*/
                    return "success";

                }
            } catch (AlipayApiException e) {
                log.error("[支付宝回调] RSA校验失败,params={}", params);
            }

        } else {
            //校验
            if (payResponse.getService().verify(params)) {
                PayMessage message = new PayMessage(params, storage.getPayType(), storage.getMsgType().name());
                PayOutMessage outMessage = payResponse.getRouter().route(message);
                return outMessage.toMessage();
            }
        }

        return payResponse.getService().getPayOutMessage("fail", "失败").toMessage();
    }


    /**
     * 支付回调地址
     * 方式二
     *
     * @param request
     * @return 拦截器相关增加， 详情查看{@link com.egzosn.pay.common.api.PayService#addPayMessageInterceptor(PayMessageInterceptor)}
     * <p></p>
     * 业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler
     * ，详情查看
     * {@link com.egzosn.pay.common.api.PayService#setPayMessageHandler(com.egzosn.pay.common.api.PayMessageHandler)}
     * <p>
     * 如果未设置 {@link com.egzosn.pay.common.api.PayMessageHandler} 那么会使用默认的
     * {@link com.egzosn.pay.common.api.DefaultPayMessageHandler}
     */
    @RequestMapping(value = "alipay_callback{payId}.do")
    public String payBack(HttpServletRequest request, @PathVariable Integer payId) throws IOException {
        //业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看com.egzosn.pay.common.api.PayService
        // .setPayMessageHandler()
        PayResponse payResponse = service.getPayResponse(payId);

        //根据payId来设置对应的messageHandler
        if (payId == PayIdEnum.ALIPAY.getCode()) {
            payResponse.getService().setPayMessageHandler(new AliPayMessageHandler());
        }

        return payResponse.getService().payBack(request.getParameterMap(), request.getInputStream()).toMessage();
    }


    /**
     * 查询
     *
     * @param order 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
    @RequestMapping("query")
    public Map<String, Object> query(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getService().query(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 查询
     *
     * @param order 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
/*    @RequestMapping("unionRefundOrConsumeUndo")
    public Map<String, Object> unionQuery(UnionQueryOrder order,String transactionType) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        UnionPayService service =  (UnionPayService)payResponse.getService();

        return service.unionRefundOrConsumeUndo(order,UnionTransactionType.valueOf(transactionType));
    }*/

    /**
     * 交易关闭接口
     *
     * @param order 订单的请求体
     * @return 返回支付方交易关闭后的结果
     */
    @RequestMapping("close")
    public Map<String, Object> close(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getService().close(order.getTradeNo(), order.getOutTradeNo());
    }

    /**
     * 申请退款接口
     *
     * @param order 订单的请求体
     * @return 返回支付方申请退款后的结果
     */
    @RequestMapping(value = "refund.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> refund(Integer payId, RefundOrder order) {
        PayResponse payResponse = service.getPayResponse(payId);

//        return payResponse.getService().refund(order.getTradeNo(), order.getOutTradeNo(), order.getRefundAmount(),
// order.getTotalAmount());
        return payResponse.getService().refund(order);
    }

    /**
     * 查询退款
     *
     * @param order 订单的请求体
     * @return 返回支付方查询退款后的结果
     */
    @RequestMapping("refundquery")
    public Map<String, Object> refundquery(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getService().refundquery(order.getTradeNo(), order.getOutTradeNo());
    }

    /**
     * 下载对账单
     *
     * @param order 订单的请求体
     * @return 返回支付方下载对账单的结果
     */
    @RequestMapping("downloadbill")
    public Object downloadbill(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());

        return payResponse.getService().downloadbill(order.getBillDate(), order.getBillType());
    }


    /**
     * 通用查询接口，根据 TransactionType 类型进行实现,此接口不包括退款
     *
     * @param order 订单的请求体
     * @return 返回支付方对应接口的结果
     */
    @RequestMapping(value = "secondaryInterface.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> secondaryInterface(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        TransactionType type =
                PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(order.getTransactionType());
        // 银联查询方法单独处理
        if (order.getPayId() == PayIdEnum.UNION.getCode()) {
            return payResponse.getService().query("", order.getOutTradeNo());
        }
        return payResponse.getService().secondaryInterface(order.getTradeNoOrBillDate(),
                order.getOutTradeNoBillType(), type);
    }


    /**
     * 转账
     *
     * @param order 转账订单
     * @return 对应的转账结果
     */
    @RequestMapping("transfer")
    public Map<String, Object> transfer(int payId, TransferOrder order) {
        PayService service = this.service.getPayResponse(payId).getService();
        return service.transfer(order);
    }

    /**
     * 转账查询
     *
     * @param outNo   商户转账订单号
     * @param tradeNo 支付平台转账订单号
     * @return 对应的转账订单
     */
    @RequestMapping("transferQuery")
    public Map<String, Object> transferQuery(int payId, String outNo, String tradeNo) {
        PayService service = this.service.getPayResponse(payId).getService();
        return service.transferQuery(outNo, tradeNo);
    }

}
