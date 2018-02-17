package pk.temp.bm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.models.SalePaymentsModel;
import pk.temp.bm.services.SalePaymentsService;
import pk.temp.bm.services.SalesService;

import java.util.List;

@RestController
public class SalePaymentsController {

    @Autowired
    private SalePaymentsService salePaymentsService;

    @RequestMapping(value = "/addSalePayment", method = RequestMethod.POST)
    public String addSalePayment(@RequestParam("salePaymentJSON") String salePaymentJSON){
        if(salePaymentJSON.isEmpty()){
            return "Empty Request.";
        }else {
            if(salePaymentsService.addSalePayment(salePaymentJSON)){
                return "Sale Payment Added Successfully";
            }else {
                return "Error adding sale payment.";
            }
        }
    }

    @RequestMapping(value = "/getCustomerPaymentHistory")
    public List<SalePaymentsModel> getCustomerPaymentHistory(@RequestParam("customerId") Long customerId){
        return salePaymentsService.findByCustomer(customerId);
    }

}
