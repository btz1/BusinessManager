package pk.temp.bm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sale_payments")
public class SalePaymentsModel implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SalesModel sale;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(name = "paid_on")
    private Date paidOn;

    @Column(name = "sale_payment_cleared")
    private Boolean salePaymentCleared;

    @Column(name = "cash_payment")
    private Boolean cashPayment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SalesModel getSale() {
        return sale;
    }

    public void setSale(SalesModel sale) {
        this.sale = sale;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(Date paidOn) {
        this.paidOn = paidOn;
    }

    public Boolean getSalePaymentCleared() {
        return salePaymentCleared;
    }

    public void setSalePaymentCleared(Boolean salePaymentCleared) {
        this.salePaymentCleared = salePaymentCleared;
    }

    public Boolean getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        this.cashPayment = cashPayment;
    }
}
