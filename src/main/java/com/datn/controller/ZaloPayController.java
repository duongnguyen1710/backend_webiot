package com.datn.controller;

import com.datn.entity.Orders;
import com.datn.entity.ZaloPayTransaction;
import com.datn.repository.OrdersRepository;
import com.datn.repository.ZaloPayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ZaloPayController {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ZaloPayRepository zaloPayRepository;

    @GetMapping("/zalopay")
    public ResponseEntity<String> handleZaloPayCallback(@RequestParam Map<String, String> params) {
        try {
            String appTransId = params.get("apptransid");
            String status = params.get("status");
            String bankCode = params.get("bankcode");
            String pmcId = params.get("pmcid");
            String checksum = params.get("checksum");

            Optional<ZaloPayTransaction> optionalTransaction = zaloPayRepository.findTopByAppTransIdOrderByIdDesc(appTransId);
            if (optionalTransaction.isPresent()) {
                ZaloPayTransaction transaction = optionalTransaction.get();

                // Cập nhật thông tin giao dịch
                transaction.setStatus("1".equals(status) ? 1 : 0);
                transaction.setBankCode(bankCode);
                transaction.setPmcId(pmcId);
                transaction.setChecksum(checksum);

                zaloPayRepository.save(transaction);

                // Tìm đơn hàng tương ứng
                String orderId = appTransId.split("_")[1]; // Lấy ID đơn hàng từ appTransId (VD: "250130_2002" -> lấy "2002")
                Optional<Orders> optionalOrder = ordersRepository.findById(Long.parseLong(orderId));

                if (optionalOrder.isPresent()) {
                    Orders order = optionalOrder.get();

                    if ("1".equals(status)) {
                        order.updateStatusPayment(1); // Thanh toán thành công
                    } else {
                        order.updateStatusPayment(0); // Thanh toán thất bại
                    }

                    ordersRepository.save(order);
                    return ResponseEntity.ok("Đơn hàng đã được cập nhật trạng thái: " + order.getStatusPayment());
                } else {
                    return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng với ID: " + orderId);
                }
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy giao dịch với appTransId: " + appTransId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }
}
