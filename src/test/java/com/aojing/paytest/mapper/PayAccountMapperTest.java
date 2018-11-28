package com.aojing.paytest.mapper;

import com.aojing.paytest.PaytestApplicationTests;
import com.aojing.paytest.pojo.ApyAccount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author gexiao
 * @date 2018/11/28 17:03
 */
public class PayAccountMapperTest extends PaytestApplicationTests {

    @Autowired
    PayAccountMapper payAccountMapper;

    @Test
    public void findByPayId() {
        ApyAccount byPayId = payAccountMapper.findByPayId(1);
        Assert.assertNotNull(byPayId);
    }
}