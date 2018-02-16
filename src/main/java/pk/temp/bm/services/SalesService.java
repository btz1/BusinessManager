package pk.temp.bm.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalePaymentsModel;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.models.SalesProductsModel;
import pk.temp.bm.repositories.CustomerRepository;
import pk.temp.bm.repositories.SalePaymentsRepository;
import pk.temp.bm.repositories.SalesProductRepository;
import pk.temp.bm.repositories.SalesRepository;
import pk.temp.bm.utilities.BMDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SalesProductRepository salesProductRepository;
    @Autowired
    private SalePaymentsRepository salePaymentsRepository;

    public String saveSalesData(String jsonObject){
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
                System.out.println("data saved");

                Double advancePayment = sales.getAdvancePayment();
                SalePaymentsModel salePaymentsModel = new SalePaymentsModel();
                salePaymentsModel.setAmountPaid(advancePayment);
                salePaymentsModel.setPaidOn(new Date());
                salePaymentsModel.setSale(sales);
                salePaymentsModel.setSalePaymentCleared(advancePayment.equals(sales.getTotalAmount()));
                salePaymentsRepository.save(salePaymentsModel);
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "okkkkkkkkk";
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


}
