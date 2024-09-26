package me.siansxint.hhrr.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByOwnerId(long id);

    List<Employee> findEmployeesByStartedAtBetween(LocalDate start, LocalDate end);
}