package com.aojing.paytest.mapper;

import com.aojing.paytest.pojo.OrderPojo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author gexiao
 * @date 2018/11/27 14:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderPojoMapperTest {
    @Autowired
    OrderPojoMapper orderPojoMapper;

    @Test
    public void deleteByPrimaryKey() {
    }

    @Test
    public void insert() {
    }

    @Test
    public void insertSelective() {
    }

    @Test
    public void selectByPrimaryKey() {
        OrderPojo orderPojo = orderPojoMapper.selectByPrimaryKey(103);
        Assert.assertNotNull(orderPojo);
    }

    @Test
    public void updateByPrimaryKeySelective() {
    }

    @Test
    public void updateByPrimaryKey() {
    }
}