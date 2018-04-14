package pk.temp.bm.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.LedgerModel;
import pk.temp.bm.models.SalePaymentsModel;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.repositories.LedgerRepository;
import pk.temp.bm.repositories.SalePaymentsRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class SalePaymentsService {

    private static final Logger logger = LoggerFactory.getLogger(SalePaymentsService.class);

    @Autowired
    private SalePaymentsRepository salePaymentsRepository;
    @Autowired
    private SalesService salesService;
    @Autowired
    private LedgerRepository ledgerRepository;


    public Boolean addSalePayment(String salePaymentJSON){
        Boolean actionStatus = false;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Double totalPaidByNow = 0d;
            SalePaymentsModel salePaymentsModel = objectMapper.readValue(salePaymentJSON,SalePaymentsModel.class);
            Long saleId = salePaymentsModel.getSale().getSalesId();
            SalesModel salesModel = salesService.findById(saleId);
            List<SalePaymentsModel> salePaymentsServiceList = salePaymentsRepository.findBySale(salesModel);
            for(SalePaymentsModel paymentsModel: salePaymentsServiceList){
                totalPaidByNow =+ paymentsModel.getAmountPaid();
            }
            salePaymentsModel.setSalePaymentCleared(totalPaidByNow.equals(salesModel.getTotalAmount()));
            salePaymentsModel.setPaidOn(new Date());
            salePaymentsRepository.save(salePaymentsModel);
            actionStatus = true;

            LedgerModel ledgerModel = new LedgerModel();
            ledgerModel.setCustomer(salesModel.getCustomer());
            ledgerModel.setDate(new Date());
            ledgerModel.setDebitAmount(salePaymentsModel.getAmountPaid());
            ledgerModel.setCreditAmount(0D);
            ledgerModel.setSalePaymentsModel(salePaymentsModel);
            ledgerRepository.save(ledgerModel);
        } catch (IOException e) {
            logger.error("Error while adding sale payment. "+e.getMessage(),e);
        }
        return actionStatus;
    }

    public List<SalePaymentsModel> findByCustomer(Long customerId){
        return salePaymentsRepository.findByCustomer(customerId);
    }
}
