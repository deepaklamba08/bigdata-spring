package com.bigdata.controller;

import com.bigdata.model.DataRequest;
import com.bigdata.service.DataService;
import com.bigdata.model.RequestResult;
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

    @RequestMapping(path = "/data", method = RequestMethod.POST)
    public RequestResult<Object> lookupData(@RequestBody DataRequest dataRequest) {
        logger.debug("Executing : DataController.lookupData()");
        return new RequestResult<>(200, "success", this.dataService.lookupData(dataRequest.getQuery(), dataRequest.getParameters()));
    }
}
