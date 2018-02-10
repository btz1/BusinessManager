package pk.temp.bm.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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

    @OneToMany()
    @JoinColumn(name="id")
    private List<CustomerModel> customerId;

    @ManyToMany
    @JoinColumn(name="id")
    private List<SalesProductsModel> saleProductId;



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

    public List<CustomerModel> getCustomerId() {
        return customerId;
    }

    public void setCustomerId(List<CustomerModel> customerId) {
        this.customerId = customerId;
    }

    public List<SalesProductsModel> getSaleProductId() {
        return saleProductId;
    }

    public void setSaleProductId(List<SalesProductsModel> saleProductId) {
        this.saleProductId = saleProductId;
    }
}
