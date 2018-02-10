package pk.temp.bm.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="sales")
@NamedQuery(name="SalesModel.findAll", query="SELECT s FROM SalesModel s")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "sales_id")
public class SalesModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long sales_id;

    @Column(name="total_amount", nullable=false, length=100)
    private String totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name="sale_date", nullable = false)
    private String saleDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name="deliver_date", nullable = false)
    private String deliverDate;

    @Column(name="advance_payment", nullable=false, length=100)
    private String advancePayment;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private CustomerModel customer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="id")
    @JsonIgnore
    private List<SalesProductsModel> saleProductList;

    @Transient
    private String customerName;



    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Long getSales_id() {
        return sales_id;
    }

    public void setSales_id(Long sales_id) {
        this.sales_id = sales_id;
    }

    public String getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(String advancePayment) {
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

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
