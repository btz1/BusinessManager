package pk.temp.bm.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.SalePaymentsModel;
import pk.temp.bm.models.SalesModel;

import java.util.Date;
import java.util.List;

@Repository
public interface SalePaymentsRepository extends CrudRepository<SalePaymentsModel,Long>{

    List<SalePaymentsModel> findBySale(@Param("salesModel") SalesModel salesModel);

    @Query(value = "select * from sale_payments where sale_id in (select sales_id from sales where customer_id = ?)",nativeQuery = true)
    List<SalePaymentsModel> findByCustomer(@Param("customerId") Long customerId);

    List<SalePaymentsModel> findAllByPaidOnBetween(Date startDate, Date endDate);
}
