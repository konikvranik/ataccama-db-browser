package net.suteren.ataccama.dbbrowser.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.suteren.ataccama.dbbrowser.entity.mysql.ColumnRecord;
import net.suteren.ataccama.dbbrowser.entity.mysql.SchemaRecord;
import net.suteren.ataccama.dbbrowser.entity.mysql.TableRecord;
import net.suteren.ataccama.dbbrowser.logic.MetadataService;
import net.suteren.ataccama.dbbrowser.repository.CustomEntityManagerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoint for operation with database metadata;
 */
@Api("Database metadata browser")
@RestController
@RequestMapping("/meta")
public class MetadataController {

    private final CustomEntityManagerFactory entityManagerFactory;
    private final MetadataService metadataService;

    public MetadataController(CustomEntityManagerFactory entityManagerFactory, MetadataService metadataService) {
        this.entityManagerFactory = entityManagerFactory;
        this.metadataService = metadataService;
    }

    /**
     * @param databaseName Reference name of database to connect to
     * @return Schemata in database with detailed information
     */
    @ApiOperation("Browse schemas")
    @GetMapping("/schemas")
    public @ResponseBody
    List<SchemaRecord> getSchemas(@ApiParam @RequestParam("database") String databaseName) {
        return metadataService.getMetaData(SchemaRecord.class, null, null, null, entityManagerFactory.getEntityManager(databaseName));
    }

    /**
     * @param databaseName Reference name of database to connect to
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @return Tables in database with detailed information
     */
    @ApiOperation("Browse tables")
    @GetMapping("/tables")
    public @ResponseBody
    List<TableRecord> getTables(@ApiParam @RequestParam("database") String databaseName,
                                @ApiParam @RequestParam(value = "schema", required = false) String schemaName) {
        return metadataService.getMetaData(TableRecord.class, schemaName, null, null, entityManagerFactory.getEntityManager(databaseName));
    }

    /**
     * @param databaseName Reference name of database to connect to
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain metadata only for this table. This field is required.
     * @return Columns in database with detailed information
     */
    @ApiOperation("Browse columns of table")
    @GetMapping("/columns")
    public @ResponseBody
    List<ColumnRecord> getColumns(@ApiParam @RequestParam("database") String databaseName,
                                  @ApiParam @RequestParam(value = "schema", required = false) String schemaName,
                                  @ApiParam @RequestParam("table") String tableName) {
        return metadataService.getMetaData(ColumnRecord.class, schemaName, tableName, null, entityManagerFactory.getEntityManager(databaseName));
    }
}