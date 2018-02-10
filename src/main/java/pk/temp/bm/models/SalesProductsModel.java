package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Tanzeel on 10/2/2018.
 */

@Entity
@Table(name="sales_products")
@NamedQuery(name="SalesProductsModel.findAll", query="SELECT s FROM SalesProductsModel s")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class SalesProductsModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @Column(nullable=false, length=100)
    private String quantity;

    @ManyToMany
    @JoinColumn(name="id")
    private List<SalesModel> salesList;

    @OneToOne
    @JoinColumn(name="product_id")
    private ProductModel productId;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<SalesModel> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<SalesModel> salesList) {
        this.salesList = salesList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductModel getProductId() {
        return productId;
    }

    public void setProductId(ProductModel productId) {
        this.productId = productId;
    }
}
