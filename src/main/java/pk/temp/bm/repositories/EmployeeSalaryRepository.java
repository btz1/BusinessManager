package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.EmployeeSalaryModel;

import java.util.List;

@Repository
public interface EmployeeSalaryRepository extends CrudRepository<EmployeeSalaryModel, Long> {

    List<EmployeeSalaryModel> findByEmployee();
}
