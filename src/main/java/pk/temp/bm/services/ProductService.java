package pk.temp.bm.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.ProductModel;
import pk.temp.bm.repositories.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public void createProduct(String productJSON){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            ProductModel productModel = objectMapper.readValue(productJSON,ProductModel.class);
            productRepository.save(productModel);
        }
        catch (Exception e){
            logger.error("Error while creating product : "+e.getMessage(),e);
        }
    }


    public List<ProductModel> getAllProducts(){
        return (List<ProductModel>) productRepository.findAll();
    }
}
