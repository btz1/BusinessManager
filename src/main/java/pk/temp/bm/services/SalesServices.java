package pk.temp.bm.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.models.SalesProductsModel;
import pk.temp.bm.repositories.CustomerRepository;
import pk.temp.bm.repositories.SalesProductRepository;
import pk.temp.bm.repositories.SalesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesServices {

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SalesProductRepository salesProductRepository;

    public String saveSalesData(String jsonObject){
        SalesModel sales =null;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            sales = objectMapper.readValue(jsonObject,SalesModel.class);
            if (sales != null){

                List<CustomerModel> customerModelList = new ArrayList<>();
                CustomerModel customerModel = new CustomerModel();
                System.out.println(sales.getCustomerId().get(0));
                customerModel = customerRepository.save(sales.getCustomerId().get(0));
                customerModelList.add(customerModel);
                sales.setCustomerId(customerModelList);

                if (sales.getSaleProductId() != null){
                    List<SalesProductsModel> salesProductsList = new ArrayList<>();
                    SalesProductsModel salesProductsModel = new SalesProductsModel();
                    salesProductsModel = salesProductRepository.save(sales.getSaleProductId().get(0));
                    salesProductsList.add(salesProductsModel);
                    sales.setSaleProductId(salesProductsList);

                }

                salesRepository.save(sales);
                System.out.println("data saved");
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "okkkkkkkkk";
    }


}
