package service;

import dao.PaymentDAO;
import model.Payment;

import java.sql.SQLException;
import java.util.Date;

public class PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }


    public boolean processPayment(int fineId, double amount, String paymentMode) throws SQLException {
        Payment payment = new Payment();
        payment.setFineId(fineId);
        payment.setPaymentDate(new Date());
        payment.setPaymentMode(paymentMode);
        payment.setPaymentStatus("Success");
        payment.setAmountPaid(amount);

        return paymentDAO.insertPayment(payment);
    }
}
