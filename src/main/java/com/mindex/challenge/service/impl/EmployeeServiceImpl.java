package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /**
     * Retrieves/Calculate the number of reports from given employee id
     * @param id the given employee id that we want to calculate number of reports
     * @return the total number of reports associated with the given employee
     */
    @Override
    public ReportingStructure getNumberOfReports(String id) {
        LOG.debug("Getting number of reports for employee with id [{}]", id);
        // Get the employee object that we want to perform number of reports
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        // Creating our reporting structure object
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(calculateNumberOfReports(employee));

        return reportingStructure;
    }

    /**
     * Helper method that calculates total reports from employee recursively
     * @param employee the given employee that we want to calculate from
     * @return the total number of reports from given employee
     */
    private int calculateNumberOfReports(Employee employee) {
        // Calculation should happen here

        // Base case: if employee has null or is empty for direct reports, we want to return 0
        if (employee.getDirectReports() == null || employee.getDirectReports().isEmpty()) {
            return 0;
        }

        // If list is not empty, we store the size of list
        int totalReports = employee.getDirectReports().size();
        // For every employee from the list, we recursively compute the calculation
        // of number of reports and add to total reports
        for (Employee report : employee.getDirectReports()) {
            Employee nextEmployee = employeeRepository.findByEmployeeId(report.getEmployeeId());
            totalReports += calculateNumberOfReports(nextEmployee);
        }

        return totalReports;

    }
}
