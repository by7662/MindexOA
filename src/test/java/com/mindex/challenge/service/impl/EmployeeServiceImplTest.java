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

        // Create test employee that is top at the tree, ready to stub two test employee in direct reports
        Employee topEmployee = new Employee();
        topEmployee.setFirstName("Ringo");
        topEmployee.setLastName("Starr");
        topEmployee.setDepartment("Engineering");
        topEmployee.setPosition("Developer V");

        // Create test employee that is foot node of the direct report tree
        Employee belowEmployee2 = new Employee();
        belowEmployee2.setFirstName("Paul");
        belowEmployee2.setLastName("McCartney");
        belowEmployee2.setDepartment("Engineering");
        belowEmployee2.setPosition("Developer I");
        // Create this employee to obtain employeeId and make existence in the database
        Employee createdBelowEmployee2 = restTemplate.postForEntity(employeeUrl, belowEmployee2, Employee.class).getBody();
        assertNotNull(createdBelowEmployee2.getEmployeeId());
        // Add this employee to a list, ready to add into its parent direct reports
        ArrayList<Employee> reportList2 = new ArrayList<>();
        reportList2.add(createdBelowEmployee2);

        // Create test employee that is child of "topEmployee" and parent of "belowEmployee2"
        Employee belowEmployee1 = new Employee();
        belowEmployee1.setFirstName("John");
        belowEmployee1.setLastName("Lennon");
        belowEmployee1.setDepartment("Engineering");
        belowEmployee1.setPosition("Development Manager");
        // Add child node to direct report list
        belowEmployee1.setDirectReports(reportList2);
        // Create this employee along with stubbed direct report list for the same reason
        // (obtain employee id and make existence in the database)
        Employee createdBelowEmployee1 = restTemplate.postForEntity(employeeUrl, belowEmployee1, Employee.class).getBody();
        assertNotNull(createdBelowEmployee1.getEmployeeId());

        // Add "belowEmployee" to a list, ready to stub "topEmployee" direct reports list
        ArrayList<Employee> reportList1 = new ArrayList<>();
        reportList1.add(createdBelowEmployee1);
        topEmployee.setDirectReports(reportList1);

        // Created new employee with endpoint for the testing report feature and obtain unique employeeId
        Employee createdTopEmployee = restTemplate.postForEntity(employeeUrl, topEmployee, Employee.class).getBody();
        assertNotNull(createdTopEmployee.getEmployeeId());

        // Run getNumberOfReports API endpoint for testing purpose
        ReportingStructure reports = restTemplate.getForEntity(reportUrl, ReportingStructure.class, createdTopEmployee.getEmployeeId()).getBody();
        assertEquals(reports.getNumberOfReports(), 2);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}