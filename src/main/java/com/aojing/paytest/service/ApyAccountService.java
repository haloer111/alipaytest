package com.aojing.paytest.service;

import com.aojing.paytest.common.PayResponse;

/**
 * @author gexiao
 * @date 2018/11/28 16:36
 */
public interface ApyAccountService {

    /**
     *  获取支付响应
     * @param id 账户id
     * @return
     */
    public PayResponse getPayResponse(Integer id);
}
