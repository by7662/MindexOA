package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationRepository compensationRepository;

    /**
     * Create new Compensation record for the given employee
     * @param compensation Compensation record received to create
     * @return The created Compensation object
     * @throws RuntimeException if the association employee is not found
     */
    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation: [{}]", compensation);
        Employee employee = employeeService.read(compensation.getEmployee().getEmployeeId());
        compensation.setEmployee(employee);
        compensationRepository.insert(compensation);

        return compensation;
    }

    /**
     * Retrieves/Read the Compensation record for the given employee id
     * @param id the employee id given to look up compensation
     * @return The compensation record from given employee id
     * @throws RuntimeException if no compensation record is found for the given employee id
     */
    @Override
    public Compensation read(String id) {
        LOG.debug("Reading compensation for employee with id: [{}]", id);
        Employee employee = employeeService.read(id);
        Compensation compensation = compensationRepository.findCompensationByEmployee(employee);
        // Catch cases if we never input compensation for given employee, we are logging
        if (compensation == null){
            throw new RuntimeException("No compensation was added for employee with id: " + id);
        }
        return compensation;
    }
}