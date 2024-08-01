package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompensationRepository extends MongoRepository<Compensation, String> {

    /**
     * Get related compensation record based on given employee
     *
     * @param employee Employee object (id)
     * @return Compensation of given employee
     */
    Compensation findCompensationByEmployee(Employee employee);
}
