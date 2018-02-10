package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Tanzeel on 10/2/2018.
 */

@Entity
@Table(name="accounts")
@NamedQuery(name="AccountsModel.findAll", query="SELECT a FROM AccountsModel a")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class AccountsModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @Column(name="debit" , nullable=false, length=100)
    private Integer debitAmount;

    @Column(name="credit", nullable=false, length=100)
    private String creditAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private String date;

    @OneToMany
    @JoinColumn(name="customer_id")
    private List<CustomerModel> customerList;

    public Integer getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Integer debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CustomerModel> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<CustomerModel> customerList) {
        this.customerList = customerList;
    }
}
