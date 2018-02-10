package pk.temp.bm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.services.LedgerService;

@RestController
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @RequestMapping(value = "/getAllAccounts")
    public void getAllAccounts(){

    }
}
