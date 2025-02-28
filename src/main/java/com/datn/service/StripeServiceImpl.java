package com.datn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.datn.entity.Orders;
import com.datn.response.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeServiceImpl implements StripeService {

	@Value("${stripe.api.key}")
	private String stripeSecretKey;
	@Override
	public StripeResponse createPaymentLink(Orders order) throws StripeException {
		// TODO Auto-generated method stub
		
		Stripe.apiKey = stripeSecretKey;
		SessionCreateParams params = SessionCreateParams.builder().addPaymentMethodType(
				SessionCreateParams.
				PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl("http://localhost:3007/payment/success/"+order.getId())
				.setCancelUrl("http://localhost:3007/payment/fail")
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder()
						.setCurrency("vnd")	
						.setUnitAmount((long) order.getTotalPrice())
						.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
								.setName("IOT")
								.build() )
						.build()
							)
						.build()
				)
				.build();
		
		Session session = Session.create(params);
		
		StripeResponse res = new StripeResponse();
		res.setStripe_url(session.getUrl());
				
		return res;
	}

}
