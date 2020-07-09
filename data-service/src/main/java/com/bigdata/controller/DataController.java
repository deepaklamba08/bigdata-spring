package com.bigdata.controller;

import com.bigdata.model.DataRequest;
import com.bigdata.service.DataService;
import com.bigdata.model.RequestResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bigdata/v1/")
public class DataController {
    @Autowired
    private DataService dataService;
    private Logger logger;

    public DataController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public String checkStatus() {
        return "running...!!";
    }

    @RequestMapping(path = "/data", method = RequestMethod.POST)
    public RequestResult<JsonNode> lookupData(@RequestBody DataRequest dataRequest) {
        logger.debug("Executing : DataController.lookupData()");
        return  this.dataService.lookupData(dataRequest.getQuery(), dataRequest.getParameters());
    }
}
