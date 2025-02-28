package com.datn.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.config.ConfigVnPay;
import com.datn.entity.Orders;
import com.datn.entity.VnPayTransaction;
import com.datn.repository.VnPayTransactionRepository;
import com.datn.response.VnPayResponse;

@Service
public class VnPayServiceImpl implements VnPayService {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    private Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

    @Autowired
    private VnPayTransactionRepository vnPayTransactionRepository;

    @Override
    public VnPayResponse createVNPayLink(Orders order) throws UnsupportedEncodingException {
        String vnpCommand = "pay";
        String vnpOrderInfo = "Thanh toan hoa don #" + order.getId();
        String orderType = "other";
        String vnpTxnRef = order.getId().toString();
        String vnpIpAddr = "127.0.0.1";

        Long amount = order.getTotalPrice() * 100;

        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền trong hóa đơn không hợp lệ");
        }

        // Khởi tạo tham số VNPAY
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", ConfigVnPay.vnp_Version);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", ConfigVnPay.vnp_TmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", vnpOrderInfo);
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", ConfigVnPay.vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", vnpIpAddr);
        cld.setTime(order.getCreateAt());
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build dữ liệu để hash và querystring
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = ConfigVnPay.hmacSHA512(ConfigVnPay.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        String paymentUrl = ConfigVnPay.vnp_PayUrl + "?" + query;

        // 🌟 Lưu thông tin giao dịch vào bảng VnPayTransaction
        VnPayTransaction transaction = new VnPayTransaction();
        transaction.setTxnRef(vnpTxnRef);
        transaction.setAmount(amount);
        transaction.setOrderInfo(vnpOrderInfo);
        transaction.setTransactionNo("PENDING");
        transaction.setSecureHash(vnp_SecureHash);
        transaction.setPayDate(null); // Chưa thanh toán
        transaction.setCreatedAt(new Date().toInstant().atZone(TimeZone.getTimeZone("Etc/GMT+7").toZoneId()).toLocalDateTime());

        vnPayTransactionRepository.save(transaction);

        // 🌟 Tạo response
        VnPayResponse response = new VnPayResponse();
        response.setVnpay_url(paymentUrl);
//        response.setMessage("VNPay payment link created successfully.");

        return response;
    }
}
