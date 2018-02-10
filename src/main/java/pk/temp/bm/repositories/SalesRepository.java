package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.CustomerModel;
import pk.temp.bm.models.SalesModel;

import java.util.List;

@Repository
public interface SalesRepository extends CrudRepository<SalesModel, Long> {

}
