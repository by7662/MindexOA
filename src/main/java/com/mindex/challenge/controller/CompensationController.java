package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;


    /**
     * Creates a new Compensation record.
     *
     * @param compensation The Compensation object to be created.
     * @return The created Compensation object.
     */
    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);
        return compensationService.create(compensation);
    }

    /**
     * Retrieves a Compensation record based on the specified employee ID.
     *
     * @param id The employee ID for which to retrieve the Compensation record.
     * @return The Compensation record associated with the provided employee ID.
     */
    @GetMapping("/compensation/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee with id [{}]", id);
        return compensationService.read(id);
    }

}
