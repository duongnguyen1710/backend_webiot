package com.datn.controller;

import com.datn.entity.MomoTransaction;
import com.datn.entity.Orders;
import com.datn.repository.MomoRepository;
import com.datn.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MomoController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private MomoRepository momoRepository;

    @GetMapping("/momo/callback")
    public ResponseEntity<String> handleMomoCallback(@RequestParam Map<String, String> params) {
        try {
            String orderId = params.get("orderId"); // MoMo Order ID (Không phải order ID thực tế)
            String orderInfo = params.get("orderInfo"); // Chứa thông tin đơn hàng thật
            String requestId = params.get("requestId");
            Long amount = Long.parseLong(params.get("amount"));
            String transId = params.get("transId");
            Integer resultCode = Integer.parseInt(params.get("resultCode"));
            String message = params.get("message");
            String payType = params.get("payType");
            Long responseTime = Long.parseLong(params.get("responseTime"));
            String extraData = params.get("extraData");
            String signature = params.get("signature");

            // Trích xuất ID đơn hàng từ orderInfo
            String orderNumber = orderInfo.replaceAll("[^0-9]", "");

            Optional<MomoTransaction> optionalTransaction = momoRepository.findTopByOrderIdOrderByIdDesc(orderId);
            if (optionalTransaction.isPresent()) {
                MomoTransaction transaction = optionalTransaction.get();

                // Cập nhật thông tin giao dịch
                transaction.setTransId(transId);
                transaction.setResultCode(resultCode);
                transaction.setMessage(message);
                transaction.setPayType(payType);
                transaction.setResponseTime(responseTime);
                transaction.setExtraData(extraData);
                transaction.setSignature(signature);

                momoRepository.save(transaction);

                // Tìm đơn hàng trong database với orderNumber thực tế
                Optional<Orders> optionalOrder = ordersRepository.findById(Long.parseLong(orderNumber));
                if (optionalOrder.isPresent()) {
                    Orders order = optionalOrder.get();

                    if (resultCode == 0) {
                        order.updateStatusPayment(1); // Thanh toán thành công
                    } else {
                        order.updateStatusPayment(0); // Thanh toán thất bại
                    }

                    ordersRepository.save(order);
                    return ResponseEntity.ok("Đơn hàng đã được cập nhật trạng thái: " + order.getStatusPayment());
                } else {
                    return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng với ID: " + orderNumber);
                }
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy giao dịch với orderId: " + orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }
}
