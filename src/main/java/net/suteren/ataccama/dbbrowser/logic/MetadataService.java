package net.suteren.ataccama.dbbrowser.logic;

import net.suteren.ataccama.dbbrowser.entity.mysql.SchemaRecord;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.internal.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for obtaining metadata of MySQL database.
 */
@Service
public class MetadataService {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataService.class);

    /**
     * Obtain metadata information from MySQL by supplied parameters.
     *
     * @param type          Entity type to get metadata for. Could be
     *                      {@link net.suteren.ataccama.dbbrowser.entity.mysql.ColumnRecord},
     *                      {@link net.suteren.ataccama.dbbrowser.entity.mysql.TableRecord}               or
     *                      {@link net.suteren.ataccama.dbbrowser.entity.mysql.SchemaRecord}
     * @param schemaName    Optional schema name to distinguish tables with the same names in different schemas
     * @param tableName     Obtain metadata only for this table
     * @param columnName    Obtain metadata only for this column
     * @param entityManager Entity manager is supplied from outside.
     * @param <T>           Type of entity to get metadata for. See type parameter.
     * @return List of entities found
     */
    public <T> List<T> getMetaData(@NotNull Class<T> type, @Null String schemaName, @Null String tableName,
                                   @Null String columnName, @NotNull EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> en = cq.from(type);
        Set<Predicate> predicates = new HashSet<>();
        if (tableName != null && !tableName.isBlank()) {
            predicates.add(cb.equal(en.get("tableName"), tableName));
        }
        if (columnName != null && !columnName.isBlank()) {
            predicates.add(cb.equal(en.get("columnName"), columnName));
        }
        if (type.isAssignableFrom(SchemaRecord.class)) {
            if (schemaName != null && !schemaName.isBlank()) {
                predicates.add(cb.equal(en.get("schemaName"), schemaName));
            }
        } else {
            try {
                String defaultSchema = entityManager.unwrap(SessionImpl.class).connection().getCatalog();
                predicates.add(cb.equal(en.get("tableSchema"), (schemaName != null && !schemaName.isBlank()) ? schemaName : defaultSchema));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        cq.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<T> query = entityManager.createQuery(cq);
        QueryImpl q = query.unwrap(QueryImpl.class);
        LOG.debug(q.getQueryString());
        return query.getResultList();
    }
}
