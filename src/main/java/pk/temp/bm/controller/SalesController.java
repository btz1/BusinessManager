package pk.temp.bm.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.services.SalesService;
import pk.temp.bm.utilities.BMDateUtils;
import pk.temp.bm.utilities.DeadlineFinder;

import java.util.Date;
import java.util.List;

@RestController
public class SalesController {

    @Autowired
    private SalesService salesService;
    @Autowired
    private DeadlineFinder deadlineFinder;

    @RequestMapping(value = "/saveSalesData",method = RequestMethod.POST)
    public String saveSalesData(@RequestParam("jsonObject") String jsonObject){
        salesService.saveSalesData(jsonObject);
        return "";
    }

    @RequestMapping(value = "/Invoice", method = RequestMethod.GET)
    public String getDataForInvoice(@RequestParam("customerId")CustomerModel customer){

        return "";
    }

    @RequestMapping(value = "/getAllSales", method = RequestMethod.GET)
    @ResponseBody
    public List<SalesModel> getAllSales(){

        return salesService.getAllSalesForPortal();
    }

    @RequestMapping(value = "/getSalesDeadlines", method = RequestMethod.GET)
    public String getSalesDeadlines(@RequestParam("date") String stringDate) throws Exception {
        Date date = BMDateUtils.parseAnyStringToDate(stringDate);
//        Date date = new Date();
        return deadlineFinder.checkForDeadLines(date);
    }

    @RequestMapping(value = "/getDashBoardStats")
    public JSONObject getDashBoardStats(){
        return salesService.getDashBoardStats();
    }

}
