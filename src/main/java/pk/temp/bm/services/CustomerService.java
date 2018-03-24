package pk.temp.bm.services;


import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.LedgerModel;
import pk.temp.bm.repositories.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LedgerService ledgerService;

    @Transactional
    public List<CustomerModel> getAllCustomersSummary(){
        List<CustomerModel> customerModelList = (List<CustomerModel>) customerRepository.findAll();
        for(CustomerModel customerModel : customerModelList){
            customerModel.setBalance(ledgerService.getCustomerBalance(customerModel));
        }
        return customerModelList;
    }

    public List<CustomerModel> getAllCustomers(){
        return  (List<CustomerModel>) customerRepository.findAll();
    }


    public void createNewCustomer(CustomerModel customerModel){
        customerRepository.save(customerModel);
    }
}
