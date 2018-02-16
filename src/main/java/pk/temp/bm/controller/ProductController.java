package pk.temp.bm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pk.temp.bm.models.ProductModel;
import pk.temp.bm.services.ProductService;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/createProduct")
    public void createProduct(@RequestParam("productJSON") String productJSON){
        if (!productJSON.isEmpty()){
            productService.createProduct(productJSON);
        }
    }

    @RequestMapping(value = "/getAllProducts")
    public List<ProductModel> getAllProducts(){
        return productService.getAllProducts();
    }

}
