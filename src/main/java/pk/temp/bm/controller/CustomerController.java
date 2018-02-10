package pk.temp.bm.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.services.CustomerService;

import java.io.IOException;
import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/getAllCustomersSummary", method = RequestMethod.GET)
    public List<CustomerModel> getAllCustomersSummary(){
        return customerService.getAllCustomersSummary();
    }

    @RequestMapping(value = "/createCustomer", method = RequestMethod.POST)
    public void createCustomer(@RequestParam("customerJson") String customerJson) throws IOException {

        if(!customerJson.isEmpty()){
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel customerModel = objectMapper.readValue(customerJson, CustomerModel.class);
            customerService.createNewCustomer(customerModel);
        }
    }
}
