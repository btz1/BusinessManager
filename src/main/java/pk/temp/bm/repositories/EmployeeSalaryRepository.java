package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.models.EmployeeSalaryModel;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeSalaryRepository extends CrudRepository<EmployeeSalaryModel, Long> {

    List<EmployeeSalaryModel> findByEmployee(EmployeeModel employeeModel);

    List<EmployeeSalaryModel> findByDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
