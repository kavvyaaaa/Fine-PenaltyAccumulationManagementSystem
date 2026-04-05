package model;

import java.util.Date;

public class Payment {
    private int paymentId;
    private int fineId;
    private Date paymentDate;
    private String paymentMode;
    private String paymentStatus;
    private double amountPaid;

    public Payment() {}

    public Payment(int fineId, Date paymentDate, String paymentMode,
                   String paymentStatus, double amountPaid) {
        this.fineId = fineId;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
    }
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getFineId() { return fineId; }
    public void setFineId(int fineId) { this.fineId = fineId; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
}
