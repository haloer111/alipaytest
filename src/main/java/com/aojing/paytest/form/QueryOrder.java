package com.aojing.paytest.form;

/**
 * @author gexiao
 * @date 2018/11/28 12:02
 */

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单辅助接口
 * @author: egan
 * @email egzosn@gmail.com
 * @date 2017/3/12 14:50
 */
@Data
public class QueryOrder {

    private Integer payId;
    //    支付平台订单号
    private String tradeNo;

    //    商户单号
    private String outTradeNo;
    //    退款金额
    private BigDecimal refundAmount;
    //    总金额
    private BigDecimal totalAmount;
    //    账单时间：具体请查看对应支付平台
    private Date billDate;
    //    账单时间：具体请查看对应支付平台
    private String billType;
    //    支付平台订单号或者账单日期
    private Object tradeNoOrBillDate;
    //    商户单号或者 账单类型
    private String outTradeNoBillType;
    //    交易类型
    private String transactionType;


}
