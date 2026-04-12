package com.medical.controller;

import com.medical.model.Payment;
import com.medical.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final com.medical.service.VnPayService vnPayService;

    @GetMapping("/vnpay/create-url/{appointmentId}")
    public ResponseEntity<?> createVnPayUrl(@PathVariable Long appointmentId, jakarta.servlet.http.HttpServletRequest request) {
        try {
            String url = vnPayService.createPaymentUrl(appointmentId, request);
            return ResponseEntity.ok(java.util.Map.of("url", url));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(@RequestParam Map<String, String> queryParams, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        boolean success = vnPayService.processCallback(queryParams);
        String appointmentId = queryParams.get("vnp_TxnRef").split("_")[0];
        
        String frontendUrl = "http://localhost:5173/booking-flow?payment_status=" + (success ? "success" : "failed") + "&appointmentId=" + appointmentId;
        response.sendRedirect(frontendUrl);
    }

    @PostMapping("/cod/{appointmentId}")
    public ResponseEntity<?> createCodPayment(@PathVariable Long appointmentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Payment payment = paymentService.processCodPayment(appointmentId, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã chọn phương thức thanh toán tại quầy thành công.");
            response.put("amount", payment.getAmount());
            response.put("method", payment.getMethod());
            response.put("status", payment.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
