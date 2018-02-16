package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.SalePaymentsModel;

@Repository
public interface SalePaymentsRepository extends CrudRepository<SalePaymentsModel,Long>{
}
