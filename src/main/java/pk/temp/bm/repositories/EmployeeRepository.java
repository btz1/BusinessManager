package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.EmployeeModel;

import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeModel, Long>{

    List<EmployeeModel> findAllByActive(Boolean active);
}
