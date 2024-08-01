package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationByIdUrl;
    private String employeeUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup(){
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationByIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateReadCompensation() {
        // We need a test employee here, since we are mainly testing compensation
        // we will initialize our employee id here
        Employee givenEmployee = new Employee();
        givenEmployee.setFirstName("John");
        givenEmployee.setLastName("Doe");
        givenEmployee.setDepartment("Engineering");
        givenEmployee.setPosition("Developer");

        // Create our given employee with the employee API endpoint, to obtain the employee id
        Employee createdGivenEmployee = restTemplate.postForEntity(employeeUrl, givenEmployee, Employee.class).getBody();
        assertNotNull(createdGivenEmployee.getEmployeeId());
        givenEmployee.setEmployeeId(createdGivenEmployee.getEmployeeId());

        // Creating our expected compensation object for testing
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployee(givenEmployee);
        testCompensation.setSalary(100000);
        testCompensation.setEffectiveDate(new Date());

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
        assertCompensationEquivalence(testCompensation, createdCompensation);

        // Read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationByIdUrl, Compensation.class, givenEmployee.getEmployeeId()).getBody();
        assertEquals(givenEmployee.getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
        assertCompensationEquivalence(testCompensation, readCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }

}
