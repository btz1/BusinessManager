package pk.temp.bm.services;


import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public List<CustomerModel> getAllCustomersSummary(){
        List<CustomerModel> customerModelList = (List<CustomerModel>) customerRepository.findAll();

        for(CustomerModel customerModel : customerModelList){
            List<LedgerModel> customerLedgerEntries = customerModel.getLedgerEntries();
            List<Double> debitList = (List<Double>) CollectionUtils.collect(customerLedgerEntries, new BeanToPropertyValueTransformer("debitAmount"));
            Double sumOfDebits = debitList.stream().mapToDouble(Double::doubleValue).sum();

            List<Double> creditList = (List<Double>) CollectionUtils.collect(customerLedgerEntries, new BeanToPropertyValueTransformer("creditAmount"));
            Double sumOfCredits = creditList.stream().mapToDouble(Double::doubleValue).sum();

            /*
            * take a difference of creditAmount from debitAmount i.e. (sumOfDebits - sumOfCredits)
            * 0 -> accounts are level
            * +ve -> company has to pay the customer
            * -ve -> customer has to pay the company
            * */
            Double balance = sumOfDebits - sumOfCredits;
            customerModel.setBalance(balance);
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
