package com.datn.service;

import com.datn.crypto.HMACUtil;
import com.datn.entity.OrderItem;
import com.datn.entity.Orders;
import com.datn.entity.ZaloPayTransaction;
import com.datn.repository.ZaloPayRepository;
import com.datn.response.ZaloPayResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ZaloPayServiceImpl implements ZaloPayService {

    @Value("${zalopay.app_id}")
    private String appId;

    @Value("${zalopay.key1}")
    private String key1;

    @Value("${zalopay.endpoint}")
    private String endpoint;

    private final ZaloPayRepository zaloPayRepository;

    public ZaloPayServiceImpl(ZaloPayRepository zaloPayRepository) {
        this.zaloPayRepository = zaloPayRepository;
    }

    @Override
    public ZaloPayResponse createZaloPayLink(Orders orders) throws Exception {
        Random rand = new Random();
        int randomId = rand.nextInt(1000000);

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("app_id", appId);
        String appTransId = getCurrentTimeString("yyMMdd") + "_" + orders.getId();
        orderData.put("app_trans_id", appTransId);
        long appTime = System.currentTimeMillis();
        orderData.put("app_time", appTime);
        String appUser = "user123";
        orderData.put("app_user", appUser);

        orderData.put("description", "Payment for the order #" + orders.getId());
        orderData.put("bank_code", "");

        JSONArray itemsJsonArray = new JSONArray();
        long totalPrice = 0;

        for (OrderItem item : orders.getItems()) {
            JSONObject itemJson = new JSONObject();
            totalPrice += item.getTotalPrice() + 30000;
            itemsJsonArray.put(itemJson);
        }
        orderData.put("amount", totalPrice);

        Map<String, String> embedData = new HashMap<>();
        embedData.put("redirecturl", "http://localhost:3001/payment/zalopay/result/");

        String itemsJson = itemsJsonArray.toString();
        String embedDataJson = new JSONObject(embedData).toString();

        orderData.put("item", itemsJson);
        orderData.put("embed_data", embedDataJson);

        String data = orderData.get("app_id") + "|" + orderData.get("app_trans_id") + "|" + orderData.get("app_user") + "|" +
                orderData.get("amount") + "|" + orderData.get("app_time") + "|" + orderData.get("embed_data") + "|" + orderData.get("item");

        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);
        orderData.put("mac", mac);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(endpoint);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : orderData.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());

        // Lưu thông tin giao dịch vào database
        // Kiểm tra xem giao dịch đã tồn tại chưa
        Optional<ZaloPayTransaction> existingTransaction = zaloPayRepository.findTopByAppTransIdOrderByIdDesc(appTransId);


        if (existingTransaction.isPresent()) {
            // Nếu đã tồn tại, cập nhật trạng thái thay vì tạo mới
            ZaloPayTransaction transaction = existingTransaction.get();
            transaction.setStatus(0); // Reset trạng thái về "Chưa thanh toán"
            transaction.setCreatedAt(LocalDateTime.now());
            zaloPayRepository.save(transaction);
        } else {
            // Nếu chưa tồn tại, tạo giao dịch mới
            ZaloPayTransaction transaction = ZaloPayTransaction.builder()
                    .appTransId(appTransId)
                    .appId(Long.parseLong(appId))
                    .status(0) // Ban đầu đặt là chưa thanh toán
                    .amount(totalPrice)
                    .bankCode("")
                    .pmcId("")
                    .discountAmount(0L)
                    .checksum(mac)
                    .createdAt(LocalDateTime.now())
                    .build();

            zaloPayRepository.save(transaction);
        }



        ZaloPayResponse response = new ZaloPayResponse();
        response.setOrderUrl(result.getString("order_url"));

        return response;
    }
    public String generateNewAppTransId(Long orderId) {
        return getCurrentTimeString("yyMMdd") + "_" + orderId + "_" + System.currentTimeMillis();
    }


    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}
