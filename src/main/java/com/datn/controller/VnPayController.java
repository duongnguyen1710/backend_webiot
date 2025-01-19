package com.datn.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Orders;
import com.datn.entity.VnPayTransaction;
import com.datn.repository.OrdersRepository;
import com.datn.repository.VnPayTransactionRepository;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VnPayController {
	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private VnPayTransactionRepository vnPayTransactionRepository;

	@GetMapping("/payment/success")
	public ResponseEntity<String> handleVNPayCallback(@RequestParam Map<String, String> params) {
	    try {
	        String txnRef = params.get("vnp_TxnRef");
	        String responseCode = params.get("vnp_ResponseCode");
	        String transactionStatus = params.get("vnp_TransactionStatus");
	        String bankCode = params.get("vnp_BankCode");
	        String bankTranNo = params.get("vnp_BankTranNo");
	        String cardType = params.get("vnp_CardType");
	        String payDate = params.get("vnp_PayDate");

	        Optional<VnPayTransaction> optionalTransaction = vnPayTransactionRepository.findByTxnRef(txnRef);

	        if (optionalTransaction.isPresent()) {
	            VnPayTransaction transaction = optionalTransaction.get();

	            // Cập nhật thông tin giao dịch
	            transaction.setResponseCode(responseCode);
	            transaction.setTransactionStatus(transactionStatus.equals("00") ? "SUCCESS" : "FAILED");
	            transaction.setBankCode(bankCode);
	            transaction.setBankTranNo(bankTranNo);
	            transaction.setCardType(cardType);
	            transaction.setPayDate(
	                payDate != null ? 
	                LocalDateTime.parse(payDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) : null
	            );

	            vnPayTransactionRepository.save(transaction);

	            // Tìm đơn hàng tương ứng
	            Optional<Orders> optionalOrder = ordersRepository.findById(Long.parseLong(txnRef));
	            if (optionalOrder.isPresent()) {
	                Orders order = optionalOrder.get();

	                if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
	                    order.updateStatusPayment(1); // Thanh toán thành công
	                } else {
	                    order.updateStatusPayment(0); // Thanh toán thất bại
	                }

	                ordersRepository.save(order);
	                return ResponseEntity.ok("Đơn hàng đã được cập nhật trạng thái: " + order.getStatusPayment());
	            } else {
	                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng với ID: " + txnRef);
	            }
	        } else {
	            return ResponseEntity.badRequest().body("Không tìm thấy giao dịch với mã tham chiếu: " + txnRef);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
	    }
	}

}
