package com.datn.service;

import java.io.UnsupportedEncodingException;

import com.datn.entity.Orders;
import com.datn.response.VnPayResponse;

public interface VnPayService {
	public VnPayResponse createVNPayLink(Orders order) throws UnsupportedEncodingException;
}
