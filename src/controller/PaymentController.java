package controller;

import service.PaymentService;

import java.sql.SQLException;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }


    public boolean payFine(int fineId, double amount, String paymentMode) {
        try {
            return paymentService.processPayment(fineId, amount, paymentMode);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
