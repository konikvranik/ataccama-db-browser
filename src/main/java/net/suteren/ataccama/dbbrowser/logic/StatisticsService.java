package net.suteren.ataccama.dbbrowser.logic;

import net.suteren.ataccama.dbbrowser.entity.mysql.ColumnRecord;
import net.suteren.ataccama.dbbrowser.entity.mysql.TableRecord;
import net.suteren.ataccama.dbbrowser.repository.CustomEntityManagerFactory;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Service for computing statistics about table or column.
 */
@Service
public class StatisticsService {
    private static final String SEPARATOR = "_";
    private static final List<String> FUNCS = List.of("min", "max", "avg");
    private static final String MEDIAN_QUERY = "SELECT x.%s from %s x, %s y GROUP BY x.%s" +
            " HAVING SUM(SIGN(1-SIGN(y.%s-x.%s))) = (COUNT(*)+1)/2";

    private final CustomEntityManagerFactory entityManagerFactory;
    private final MetadataService metadataService;

    public StatisticsService(CustomEntityManagerFactory entityManagerFactory, MetadataService metadataService) {
        this.entityManagerFactory = entityManagerFactory;
        this.metadataService = metadataService;
    }

    /**
     * Get rows in table and count of table columns.
     *
     * @param databaseName Reference name of database from {@link net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord}
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain statistics only for this table
     * @return Count of columns and rows for table
     */
    public Map<String, Number> getTableStatistics(@NotNull String databaseName, @Null String schemaName, @NotNull String tableName) {
        EntityManager entityManager = entityManagerFactory.getEntityManager(databaseName);
        TableRecord table = metadataService.getMetaData(TableRecord.class, schemaName, tableName, null, entityManager).stream().findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "table not found"));
        int columnCount = getColumnsCount(entityManager, table);
        return Map.of("columns", columnCount, "rows", getTableRows(schemaName, tableName, entityManager));
    }

    /**
     * Count min, max, avg and median of data in table.
     *
     * @param databaseName Reference name of database from {@link net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord}
     * @param schemaName   Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName    Obtain statistics only for this table
     * @param columnName   Obtain statistics only for this column
     * @return min, max, avg and median for table data
     */
    public Map<String, Map<String, Object>> getStatistics(@NotNull String databaseName, @Null String schemaName,
                                                          @NotNull String tableName, @Null String columnName) {
        EntityManager entityManager = entityManagerFactory.getEntityManager(databaseName);
        List<ColumnRecord> col = metadataService.getMetaData(ColumnRecord.class, schemaName, tableName, columnName, entityManager);
        StringBuilder sb = prepareStatisticalQuery(schemaName, tableName, col);
        Query nativeQuery = entityManager.createNativeQuery(sb.toString());
        @SuppressWarnings("unchecked")
        Map<String, Object> singleResult = (Map<String, Object>) nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getSingleResult();

        return Stream.concat(getMedians(tableName, entityManager, col).entrySet().stream(), singleResult.entrySet().stream()
                .map(e -> {
                    String[] k = e.getKey().split(SEPARATOR, 2);
                    return new AbstractMap.SimpleImmutableEntry<>(k[1], new AbstractMap.SimpleImmutableEntry<>(k[0], e.getValue()));
                }))
                .collect(toMap(Map.Entry::getKey, e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put(e.getValue().getKey(), e.getValue().getValue());
                    return m;
                }, (v1, v2) -> {
                    v1.putAll(v2);
                    return v1;
                }));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map.Entry<String, Object>> getMedians(String tableName, EntityManager entityManager, List<ColumnRecord> col) {
        return col.stream()
                .map(ColumnRecord::getColumnName)
                .map(c -> new AbstractMap.SimpleImmutableEntry<>(c,
                        new AbstractMap.SimpleImmutableEntry<>("median",
                                entityManager.createNativeQuery(getMedianQuery(tableName, c))
                                        .getResultStream().findAny().orElse(null))))
                .collect(toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));
    }

    private int getColumnsCount(EntityManager entityManager, TableRecord table) {
        return metadataService.getMetaData(ColumnRecord.class, table.getTableSchema(), table.getTableName(), null, entityManager).size();
    }

    private Long getTableRows(@RequestParam(value = "schema", required = false) String schemaName, @RequestParam("table") String tableName, EntityManager entityManager) {
        return metadataService.getMetaData(TableRecord.class, schemaName, tableName, null, entityManager).stream()
                .findAny()
                .map(TableRecord::getTableRows).orElse(null);
    }

    private String getMedianQuery(@RequestParam("table") String tableName, String c) {
        return String.format(MEDIAN_QUERY, c, tableName, tableName, c, c, c);
    }

    private StringBuilder prepareStatisticalQuery(@RequestParam(value = "schema", required = false) String schemaName, @RequestParam("table") String tableName, List<ColumnRecord> col) {
        StringBuilder sb = new StringBuilder("select ");
        for (int i = 0; i < col.size(); i++) {
            for (int j = 0; j < FUNCS.size(); j++) {
                if (i + j > 0) {
                    sb.append(",");
                }
                sb.append(FUNCS.get(j));
                sb.append("(");
                sb.append(col.get(i).getColumnName());
                sb.append(") as ");
                sb.append(FUNCS.get(j));
                sb.append(SEPARATOR);
                sb.append(col.get(i).getColumnName());
            }
        }
        sb.append(" from ");
        if (schemaName != null && !schemaName.isBlank()) {
            sb.append(schemaName);
            sb.append(".");
        }
        sb.append(tableName);
        return sb;
    }
}
