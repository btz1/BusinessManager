package pk.temp.bm.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.temp.bm.models.SalesModel;
import pk.temp.bm.models.SalesProductsModel;
import pk.temp.bm.services.SalesService;

import java.util.Date;
import java.util.List;

@Service
//@EnableScheduling
public class DeadlineFinder {

    @Autowired
    private SalesService salesService;

    private static final Logger logger = LoggerFactory.getLogger(DeadlineFinder.class);

//    @Scheduled(fixedDelay = 300000)
    @Transactional
    public String checkForDeadLines(Date currentDate) throws Exception {
        String startDate = BMDateUtils.getStringDateForStartOfDay(currentDate);
        String endDate = BMDateUtils.getStringDateForEndOfDay(currentDate);
        StringBuilder sb = new StringBuilder();
        try{
            List<SalesModel> salesModelList = salesService.getAllByDeliverDateBetween(startDate,endDate);
            if(!salesModelList.isEmpty()){
                sb.append("Dead Line Alert! \nyou have committed following orders to be delivered today. \n");
                for(SalesModel salesModel : salesModelList){
                  sb.append("Order ID : ");
                  sb.append(salesModel.getSalesId().toString());
                  sb.append(" For Customer : ");
                  sb.append(salesModel.getCustomer().getFirstName());
                  sb.append(" Contact : ");
                  sb.append(salesModel.getCustomer().getPhoneNumber());
                  List<SalesProductsModel> salesProductList = salesModel.getSaleProductList();
                  if(!salesProductList.isEmpty()){
                      sb.append("Order Detail : \n");
                      for(SalesProductsModel salesProducts : salesProductList){
                          sb.append(salesProducts.getProductModel().getName()).append(" : ");
                          sb.append(salesProducts.getQuantity()).append(" ").append(salesProducts.getProductModel().getUnit());
                      }
                  }
                  sb.append("Payment Detail : \n Order Total : ");
                  Double orderTotal = salesModel.getTotalAmount();
                  Double advancePayment = salesModel.getAdvancePayment();
                  Double remainingAmount = orderTotal - advancePayment;
                  sb.append(orderTotal);
                  sb.append("\n Advance Payment : ").append(advancePayment);
                  sb.append("\n Remaining Receivable : ").append(remainingAmount).append("\n");
                }
                logger.info(sb.toString());
            }
        }
        catch (Exception e){
            logger.error("Exception in DealLine reminder : "+e.getMessage(),e);
        }
        return sb.toString();
    }
}
