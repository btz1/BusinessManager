package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.AttendanceModel;
import pk.temp.bm.models.EmployeeModel;

import java.util.List;

@Repository
public interface AttendanceRepository extends CrudRepository<AttendanceModel, Long>{

    public List<AttendanceModel> findByEmployee(EmployeeModel employeeModel);
}
