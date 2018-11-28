package com.aojing.paytest.service.impl;

import com.aojing.paytest.PaytestApplicationTests;
import com.aojing.paytest.common.PayResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author gexiao
 * @date 2018/11/28 17:11
 */
public class ApyAccountServiceImplTest extends PaytestApplicationTests {

    @Autowired
    ApyAccountServiceImpl apyAccountService;
    @Test
    public void getPayResponse() {
        PayResponse payResponse = apyAccountService.getPayResponse(1);
        Assert.assertNotNull(payResponse);
    }
}