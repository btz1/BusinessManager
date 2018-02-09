package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.SalesModel;

@Repository
public interface SalesRepository extends CrudRepository<SalesModel, Integer> {
}
