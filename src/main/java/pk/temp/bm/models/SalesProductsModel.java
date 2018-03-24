package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;

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

/*    @ManyToOne
    @JoinColumn(name = "salesId")
    private SalesModel saleModel;*/

    @OneToOne
    @JoinColumn(name="product_id")
    private ProductModel productModel;

    @Column(name = "sale_price")
    private Double salePrice;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

/*
    public SalesModel getSaleModel() {
        return saleModel;
    }

    public void setSaleModel(SalesModel saleModel) {
        this.saleModel = saleModel;
    }
*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
}
