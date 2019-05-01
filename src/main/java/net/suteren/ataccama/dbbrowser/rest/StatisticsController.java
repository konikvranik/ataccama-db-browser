package net.suteren.ataccama.dbbrowser.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.suteren.ataccama.dbbrowser.logic.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST endpoint for generating database statistics;
 */
@Api("Database statistics")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Return rows in table and count of table columns.
     *
     * @param databaseName Reference name of database from {@link net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord}
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain statistics only for this table. This field is required.
     * @return Count of columns and rows for table
     */
    @ApiOperation("Statistics per table")
    @GetMapping("/table")
    public @ResponseBody
    Map<String, Number> getStatistics(@ApiParam @RequestParam("database") String databaseName,
                                      @ApiParam @RequestParam(value = "schema", required = false) String schemaName,
                                      @ApiParam @RequestParam("table") String tableName) {
        return statisticsService.getTableStatistics(databaseName, schemaName, tableName);
    }

    /**
     * Return min, max, avg and median of data in table.
     *
     * @param databaseName Reference name of database from {@link net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord}
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain statistics only for this table. This field is required.
     * @param columnName   Obtain statistics only for this column
     * @return min, max, avg and median for table data
     */
    @ApiOperation("Statistics per column")
    @GetMapping("/column")
    public @ResponseBody
    Map<String, Map<String, Object>> getStatistics(@ApiParam @RequestParam("database") String databaseName,
                                                   @ApiParam @RequestParam(value = "schema", required = false) String schemaName,
                                                   @ApiParam @RequestParam("table") String tableName,
                                                   @ApiParam @RequestParam(value = "column", required = false) String columnName) {
        return statisticsService.getStatistics(databaseName, schemaName, tableName, columnName);
    }
}
