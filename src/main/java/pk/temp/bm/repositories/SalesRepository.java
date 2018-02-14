package pk.temp.bm.repositories;

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

}
