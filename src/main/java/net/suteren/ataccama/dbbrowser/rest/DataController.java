package net.suteren.ataccama.dbbrowser.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.suteren.ataccama.dbbrowser.logic.DataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for operation with table data;
 */
@Api("Database data browser")
@RestController
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }


    @ApiOperation("Browse data")
    @GetMapping("/data")
    public @ResponseBody
    Map<String, List> getData(@ApiParam @RequestParam("database") String databaseName,
                              @ApiParam @RequestParam(value = "schema", required = false) String schemaName,
                              @ApiParam @RequestParam("table") String tableName) {
        return dataService.getData(databaseName, schemaName, tableName);
    }


}