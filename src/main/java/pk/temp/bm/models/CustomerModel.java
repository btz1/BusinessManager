package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="customer")
@NamedQuery(name="CustomerModel.findAll", query="SELECT c FROM CustomerModel c")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class CustomerModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name="first_name", nullable=false, length=100)
    private String firstName;

    @Column(name="last_name", length=100)
    private String lastName;

    @Column(name="email", length=100)
    private String email;

    @Column( length=200)
    private String address1;

    @Column(name="address2", length=200)
    private String address2;

    @Column(length=100)
    private String city;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<LedgerModel> ledgerEntries;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<SalesModel> salesList;

    @Column(name = "phone")
    private String phoneNumber;

    @Transient
    private Double balance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<SalesModel> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<SalesModel> salesList) {
        this.salesList = salesList;
    }

    public List<LedgerModel> getLedgerEntries() {
        return ledgerEntries;
    }

    public void setLedgerEntries(List<LedgerModel> ledgerEntries) {
        this.ledgerEntries = ledgerEntries;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
