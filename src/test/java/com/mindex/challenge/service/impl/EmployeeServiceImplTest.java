package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportUrl;
    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportUrl = "http://localhost:" + port + "/employee/reports/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        testEmployee.setEmployeeId(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);


        // Report checks
        // Create two local employee object
        // Call create API to add them to database
        // Read to make sure
        // Update and get a remote employee object. at the same time we update one of our local employee object then compare
        Employee belowEmployee = new Employee();
        belowEmployee.setFirstName("Ringo");
        belowEmployee.setLastName("Starr");
        belowEmployee.setDepartment("Engineering");
        belowEmployee.setPosition("Developer V");

        // Created new employee with endpoint for the testing report feature and obtain unique employeeId
        Employee createdBelowEmployee = restTemplate.postForEntity(employeeUrl, belowEmployee, Employee.class).getBody();
        assertNotNull(createdBelowEmployee.getEmployeeId());

        belowEmployee.setEmployeeId(createdBelowEmployee.getEmployeeId());
        ArrayList<Employee> reportList = new ArrayList<>();
        reportList.add(createdBelowEmployee);
        testEmployee.setDirectReports(reportList);

        Employee updatedTestEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(testEmployee, headers),
                        Employee.class,
                        testEmployee.getEmployeeId()).getBody();

        System.out.println(updatedTestEmployee.getEmployeeId());
        assertNotNull(updatedTestEmployee.getDirectReports());
        System.out.println(updatedTestEmployee.getDirectReports());
        ReportingStructure reports = restTemplate.getForEntity(reportUrl, ReportingStructure.class, updatedTestEmployee.getEmployeeId()).getBody();
        assertEquals(reports.getNumberOfReports(), 1);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
