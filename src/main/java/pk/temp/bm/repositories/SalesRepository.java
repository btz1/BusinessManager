package pk.temp.bm.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalesModel;

import java.util.Date;
import java.util.List;

@Repository
public interface SalesRepository extends CrudRepository<SalesModel, Long> {

    List<SalesModel> getAllByDeliverDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "select count(*) from sales where sale_date >=:startDate and sale_date <=:endDate",nativeQuery = true)
    Integer getCurrentMonthSales(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select count(*) from sales where sale_date >=:startDate and sale_date <=:endDate and delivered = 1",nativeQuery = true)
    Integer getCurrentDelivered(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select count(*) from sales where sale_date >=:startDate and sale_date <=:endDate and delivered = 0",nativeQuery = true)
    Integer getCurrentDeliverable(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
