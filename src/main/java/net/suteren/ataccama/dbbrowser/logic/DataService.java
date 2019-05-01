package net.suteren.ataccama.dbbrowser.logic;

import net.suteren.ataccama.dbbrowser.entity.mysql.ColumnRecord;
import net.suteren.ataccama.dbbrowser.repository.CustomEntityManagerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Service for obtaining data of MySQL database.
 */
@Service
public class DataService {

    private final CustomEntityManagerFactory entityManagerFactory;
    private final MetadataService metadataService;

    public DataService(CustomEntityManagerFactory entityManagerFactory, MetadataService metadataService) {
        this.entityManagerFactory = entityManagerFactory;
        this.metadataService = metadataService;
    }

    /**
     * Get grid of table data and column names.
     *
     * @param databaseName Reference name of database from {@link net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord}
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain metadata only for this table
     * @return table data and column names
     */
    public Map<String, List> getData(String databaseName, String schemaName, String tableName) {
        EntityManager entityManager = entityManagerFactory.getEntityManager(databaseName);
        List<String> columns = metadataService.getMetaData(ColumnRecord.class, schemaName, tableName, null, entityManager).stream()
                .map(ColumnRecord::getColumnName)
                .collect(toList());
        List data = entityManager.createNativeQuery("select * from " + (schemaName == null ? "" : schemaName + ".") + tableName + " e")
                .getResultList();
        return Map.of("columns", columns, "data", data);
    }
}
