package pk.temp.bm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.models.ExpenseModel;
import pk.temp.bm.services.SalesService;

import java.io.IOException;

/**
 * Created by Abubakar on 4/9/2018
 */
@RestController
public class ExpenseController {

    @Autowired
    private SalesService salesService;

    @RequestMapping(value = "/addExpense")
    public void addExpense(@RequestParam("expenseJson") String expenseJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExpenseModel model = objectMapper.readValue(expenseJson,ExpenseModel.class);
        salesService.addExpense(model);
    }

}
