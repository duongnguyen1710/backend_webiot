package com.datn.service;

import com.datn.entity.Orders;
import com.datn.response.MomoResponse;

public interface MomoService {
    MomoResponse createMomoPayment(Orders order) throws Exception;
}
