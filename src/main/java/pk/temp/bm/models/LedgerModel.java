package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Tanzeel on 10/2/2018.
 */

@Entity
@Table(name="ledger")
@NamedQuery(name="LedgerModel.findAll", query="SELECT a FROM LedgerModel a")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class LedgerModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @Column(name="debit" , nullable=false, length=100)
    private Double debitAmount;

    @Column(name="credit", nullable=false, length=100)
    private Double creditAmount;

    @Column(nullable = false)
    private Date date;

    @OneToOne
    private CustomerModel customer;

    @OneToOne
    @JoinColumn(name = "sale_id")
    private SalesModel salesModel;

    @OneToOne
    @JoinColumn(name = "sale_payment_id")
    private SalePaymentsModel salePaymentsModel;


    public Double getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Double debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public SalesModel getSalesModel() {
        return salesModel;
    }

    public void setSalesModel(SalesModel salesModel) {
        this.salesModel = salesModel;
    }

    public SalePaymentsModel getSalePaymentsModel() {
        return salePaymentsModel;
    }

    public void setSalePaymentsModel(SalePaymentsModel salePaymentsModel) {
        this.salePaymentsModel = salePaymentsModel;
    }
}
