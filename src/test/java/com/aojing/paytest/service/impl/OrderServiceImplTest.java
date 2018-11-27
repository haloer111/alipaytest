package com.aojing.paytest.service.impl;

import com.aojing.paytest.pojo.OrderPojo;
import com.aojing.paytest.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author gexiao
 * @date 2018/11/27 14:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void findById() {
        OrderPojo pojo = orderService.findById(103);
        Assert.assertNotNull(pojo);

    }
}