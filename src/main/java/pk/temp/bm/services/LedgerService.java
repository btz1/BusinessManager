package pk.temp.bm.services;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.LedgerModel;
import pk.temp.bm.repositories.LedgerRepository;
import pk.temp.bm.utilities.GlobalAppUtil;

import java.util.List;
import java.util.Objects;

@Service
public class LedgerService {

    @Autowired
    LedgerRepository ledgerRepository;

    public void getAllAccounts(){

    }

    @Transactional
    public Double getCustomerBalance(CustomerModel customerModel){
        List<LedgerModel> customerLedgerEntries = customerModel.getLedgerEntries();
        List<Double> debitList = (List<Double>) CollectionUtils.collect(customerLedgerEntries, new BeanToPropertyValueTransformer("debitAmount"));
        Double sumOfDebits = debitList.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();

        List<Double> creditList = (List<Double>) CollectionUtils.collect(customerLedgerEntries, new BeanToPropertyValueTransformer("creditAmount"));
        Double sumOfCredits = creditList.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
            /*
            * take a difference of creditAmount from debitAmount i.e. (sumOfDebits - sumOfCredits)
            * 0 -> accounts are level
            * +ve -> company has to pay the customer
            * -ve -> customer has to pay the company
            * */
        return sumOfDebits - sumOfCredits;
    }

    public void addToLedger(LedgerModel ledgerModel){
        ledgerRepository.save(ledgerModel);
    }
}
