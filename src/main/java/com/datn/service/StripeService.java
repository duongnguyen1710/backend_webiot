package com.datn.service;

import com.datn.entity.Orders;
import com.datn.response.StripeResponse;
import com.stripe.exception.StripeException;

public interface StripeService {
	public StripeResponse createPaymentLink(Orders order) throws StripeException;
}
