package pk.temp.bm.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.*;
import pk.temp.bm.repositories.*;
import pk.temp.bm.utilities.BMDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SalesProductRepository salesProductRepository;
    @Autowired
    private SalePaymentsRepository salePaymentsRepository;
    @Autowired
    private LedgerRepository ledgerRepository;

    public Boolean saveSalesData(String jsonObject){
        Boolean actionStatus = false;
        SalesModel sales =null;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            sales = objectMapper.readValue(jsonObject,SalesModel.class);
            if (sales != null){

                CustomerModel customerModel = new CustomerModel();
                customerModel = sales.getCustomer();
                if(null == customerModel.getId()){
                    customerModel = customerRepository.save(sales.getCustomer());
                }
                sales.setCustomer(customerModel);

                if (sales.getSaleProductList() != null){
                    List<SalesProductsModel> salesProductsList = new ArrayList<>();
                    SalesProductsModel salesProductsModel = new SalesProductsModel();
                    salesProductsModel = salesProductRepository.save(sales.getSaleProductList().get(0));
                    salesProductsList.add(salesProductsModel);
                    sales.setSaleProductList(salesProductsList);

                }
                salesRepository.save(sales);

                // credit entry, in any case
                LedgerModel ledgerModel = new LedgerModel();
                ledgerModel.setCustomer(customerModel);
                ledgerModel.setDate(new Date());
                ledgerModel.setCreditAmount(sales.getTotalAmount());
                ledgerRepository.save(ledgerModel);

                actionStatus = true;

                Double advancePayment = sales.getAdvancePayment();
                if(null != advancePayment && 0 != advancePayment){
                    SalePaymentsModel salePaymentsModel = new SalePaymentsModel();
                    salePaymentsModel.setAmountPaid(advancePayment);
                    salePaymentsModel.setPaidOn(new Date());
                    salePaymentsModel.setSale(sales);
                    salePaymentsModel.setSalePaymentCleared(advancePayment.equals(sales.getTotalAmount()));
                    salePaymentsRepository.save(salePaymentsModel);

                    // debit entry, only in case of advance payment
                    ledgerModel = new LedgerModel();
                    ledgerModel.setCustomer(customerModel);
                    ledgerModel.setDate(new Date());
                    ledgerModel.setDebitAmount(sales.getAdvancePayment());
                    ledgerRepository.save(ledgerModel);
                }

            }
        }catch (Exception ex){
            logger.error("Error While Creating a Sale Entry. "+ex.getMessage(),ex);
        }
        return actionStatus;
    }

    public List<SalesModel> getAllByDeliverDateBetween(String startDate, String endDate){
        return salesRepository.getAllByDeliverDateBetween(startDate,endDate);
    }


    public List<SalesModel> getAllSalesForPortal(){
        return (List<SalesModel>) salesRepository.findAll();
    }

    public SalesModel findById(Long empId){
        return salesRepository.findOne(empId);
    }

    public JSONObject getDashBoardStats(){
        Date currentDate = new Date();
        Date startDate = BMDateUtils.getDateForStartOfDay(BMDateUtils.getStartDateOfMonth(currentDate));
        Date endDate = BMDateUtils.getDateForEndOfDay(BMDateUtils.getEndDateOfMonth(currentDate));
        Integer saleCount = salesRepository.getCurrentMonthSales(startDate,endDate);
        Integer deliveredCount = salesRepository.getCurrentDelivered(startDate,endDate);
        Integer deliverableCount = salesRepository.getCurrentDeliverable(startDate,endDate);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("saleCount",saleCount);
        jsonObject.put("deliveredCount",deliveredCount);
        jsonObject.put("deliverableCount",deliverableCount);
        jsonObject.put("returnedCount",deliveredCount);
        return jsonObject;
    }


}
