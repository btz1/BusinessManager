package pk.temp.bm.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="sales")
@NamedQuery(name="SalesModel.findAll", query="SELECT s FROM SalesModel s")
public class SalesModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false, name = "sales_id")
    private Long salesId;

    @Column(name="total_amount", nullable=false, length=100)
    private Double totalAmount;

    @Column(name="sale_date", nullable = false)
    @JsonFormat(pattern="dd-MMMM-yyyy")
    private Date saleDate;

    @Column(name="deliver_date", nullable = false)
    @JsonFormat(pattern="dd-MMMM-yyyy")
    private Date deliverDate;

    @Column(name="advance_payment", nullable=false, length=100)
    private Double advancePayment;

    @OneToOne(fetch = FetchType.LAZY)
    private CustomerModel customer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sale_id")
    private List<SalesProductsModel> saleProductList;

    private Boolean delivered;


    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Long getSalesId() {
        return salesId;
    }

    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }

    public Double getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(Double advancePayment) {
        this.advancePayment = advancePayment;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public List<SalesProductsModel> getSaleProductList() {
        return saleProductList;
    }

    public void setSaleProductList(List<SalesProductsModel> saleProductList) {
        this.saleProductList = saleProductList;
    }

    public String getCustomerName() {
        return customer.getFirstName();
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
}
