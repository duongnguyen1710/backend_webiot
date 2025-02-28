package com.datn.service;

import com.datn.entity.MomoTransaction;
import com.datn.entity.Orders;
import com.datn.repository.MomoRepository;
import com.datn.response.MomoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MomoServiceImpl implements  MomoService{

    @Autowired
    private MomoRepository momoRepository;

    private final String accessKey = "F8BBA842ECF85";
    private final String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private final String partnerCode = "MOMO";
    private final String ipnUrl = "https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b";
    private final String requestType = "captureWallet";
    private final boolean autoCapture = true;
    private final String lang = "vi";

    @Override
    @Transactional
    public MomoResponse createMomoPayment(Orders order) throws Exception {
        long totalPrice = order.getTotalPrice();
        String orderId = partnerCode + new Date().getTime();
        String requestId = orderId;
        String orderInfo = "Payment for the order #" + order.getId();
        String redirectUrl = "http://localhost:3000/payment/momo/result/" + order.getId();
        String extraData = "";

        // Tạo chữ ký (signature)
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + totalPrice +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = hmacSHA256(secretKey, rawSignature);

        // Tạo body request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("partnerName", "Test");
        requestBody.put("storeId", "MomoTestStore");
        requestBody.put("requestId", requestId);
        requestBody.put("amount", totalPrice);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("lang", lang);
        requestBody.put("requestType", requestType);
        requestBody.put("autoCapture", autoCapture);
        requestBody.put("extraData", extraData);
        requestBody.put("signature", signature);

        // Gửi request tới MoMo
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(requestBody), headers);

        ResponseEntity<MomoResponse> response = restTemplate.postForEntity(
                "https://test-payment.momo.vn/v2/gateway/api/create",
                entity,
                MomoResponse.class
        );

        MomoResponse momoResponse = response.getBody();

        // Lưu thông tin thanh toán vào database
        if (momoResponse != null) {
            MomoTransaction momoPayment = new MomoTransaction();
            momoPayment.setPartnerCode(momoResponse.getPartnerCode());
            momoPayment.setOrderId(momoResponse.getOrderId());
            momoPayment.setRequestId(momoResponse.getRequestId());
            momoPayment.setAmount(momoResponse.getAmount());
            momoPayment.setOrderInfo(orderInfo);
            momoPayment.setOrderType("momo_wallet");
            momoPayment.setTransId(null); // Sẽ cập nhật khi nhận callback từ MoMo
            momoPayment.setResultCode(Integer.parseInt(momoResponse.getResultCode()));
            momoPayment.setMessage(momoResponse.getMessage());
            momoPayment.setPayType(null);
            momoPayment.setResponseTime(Long.parseLong(momoResponse.getResponseTime()));
            momoPayment.setExtraData(extraData);
            momoPayment.setSignature(signature);

            momoRepository.save(momoPayment);
        }

        return momoResponse;
    }

    private static String hmacSHA256(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return bytesToHex(sha256_HMAC.doFinal(data.getBytes()));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
