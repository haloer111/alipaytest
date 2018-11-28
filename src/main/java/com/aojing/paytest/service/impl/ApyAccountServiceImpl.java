package com.aojing.paytest.service.impl;

import com.aojing.paytest.common.PayResponse;
import com.aojing.paytest.mapper.PayAccountMapper;
import com.aojing.paytest.pojo.ApyAccount;
import com.aojing.paytest.service.ApyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author gexiao
 * @date 2018/11/28 16:44
 */
@Service
public class ApyAccountServiceImpl implements ApyAccountService {
    @Resource
    private AutowireCapableBeanFactory spring;

    @Autowired
    private PayAccountMapper payAccountMapper;

    @Override
    public PayResponse getPayResponse(Integer id) {
        ApyAccount apyAccount = payAccountMapper.findByPayId(id);
        if (apyAccount == null) {
            throw new IllegalArgumentException("无法查询");
        }
        PayResponse payResponse = new PayResponse();
        spring.autowireBean(payResponse);
        payResponse.init(apyAccount);
        // 查询
        return payResponse;

    }
}
