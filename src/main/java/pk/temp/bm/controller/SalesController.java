package pk.temp.bm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.services.SalesServices;

import java.util.List;

@RestController
public class SalesController {

    @Autowired
    private SalesServices salesServices;

    @RequestMapping(value = "/salesData",method = RequestMethod.POST)
    public String saveSalesData(@RequestParam("jsonObject") String jsonObject){
        salesServices.saveSalesData(jsonObject);
        return "";
    }

    @RequestMapping(value = "/Invoice", method = RequestMethod.GET)
    public String getDataForInvoice(@RequestParam("customerId")CustomerModel customer){

        return "";
    }

    @RequestMapping(value = "/getAllSales", method = RequestMethod.GET)
    public List<SalesModel> getAllSales(){
        return salesServices.getAllSalesForPortal();
    }

}
