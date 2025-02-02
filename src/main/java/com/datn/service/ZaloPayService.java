package com.datn.service;

import com.datn.entity.Orders;
import com.datn.response.ZaloPayResponse;

public interface ZaloPayService {
    ZaloPayResponse createZaloPayLink(Orders orders) throws Exception;
}
