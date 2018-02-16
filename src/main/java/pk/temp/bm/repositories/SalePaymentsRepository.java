package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.SalePaymentsModel;
import pk.temp.bm.models.SalesModel;

import java.util.List;

@Repository
public interface SalePaymentsRepository extends CrudRepository<SalePaymentsModel,Long>{

    List<SalePaymentsModel> findBySale(@Param("salesModel") SalesModel salesModel);
}
