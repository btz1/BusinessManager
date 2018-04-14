package pk.temp.bm.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "employee")
public class EmployeeModel implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column(name = "salary_per_day")
    private Long perDaySalary;

    @Column(name = "salary_per_month")
    private Long perMonthSalary;

    @Column
    private String address;

    @Column(name = "joining_date")
    private Date joinDate;

    @Column
    private Boolean active;

    @Column(name = "over_time_rate")
    private Long overTimeRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getPerDaySalary() {
        return perDaySalary;
    }

    public void setPerDaySalary(Long perDaySalary) {
        this.perDaySalary = perDaySalary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Long getOverTimeRate() {
        return overTimeRate;
    }

    public void setOverTimeRate(Long overTimeRate) {
        this.overTimeRate = overTimeRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getPerMonthSalary() {
        return perMonthSalary;
    }

    public void setPerMonthSalary(Long perMonthSalary) {
        this.perMonthSalary = perMonthSalary;
    }
}
