package net.suteren.ataccama.dbbrowser.entity.mysql;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing schema in MySQL information schema.
 */
@Entity
@Table(name = "schemata", catalog = "information_schema")
@IdClass(SchemaRecord.SchemaId.class)
@Data
public class SchemaRecord {
    @Id
    @Column(name = "CATALOG_NAME")
    private String catalogName;
    @Id
    @Column(name = "SCHEMA_NAME")
    private String schemaName;
    @Column(name = "DEFAULT_CHARACTER_SET_NAME")
    private String defaultCharacterSetName;
    @Column(name = "DEFAULT_COLLATION_NAME")
    private String defaultCollationName;
    @Column(name = "SQL_PATH")
    private String sqlPath;

    @Data
    static class SchemaId implements Serializable {
        private String catalogName;
        private String schemaName;
    }
}
