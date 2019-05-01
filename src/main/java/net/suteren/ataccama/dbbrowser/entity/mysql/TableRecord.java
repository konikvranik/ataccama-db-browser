package net.suteren.ataccama.dbbrowser.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

/**
 * Entity representing tale in MySQL information schema.
 */
@Entity
@Table(name = "tables", catalog = "information_schema")
@IdClass(TableRecord.TableId.class)
@Data
public class TableRecord {
    @Id
    @Column(name = "TABLE_CATALOG")
    private String tableCatalog;
    @Id
    @Column(name = "TABLE_SCHEMA")
    private String tableSchema;
    @Id
    @Column(name = "TABLE_NAME")
    private String tableName;
    @Column(name = "TABLE_TYPE")
    private String tableType;
    private String engine;
    private Long version;
    @Column(name = "ROW_FORMAT")
    private String rowFormat;
    @Column(name = "TABLE_ROWS")
    private Long tableRows;
    @Column(name = "AVG_ROW_LENGTH")
    private Long avgRowLength;
    @Column(name = "DATA_LENGTH")
    private Long dataLength;
    @Column(name = "MAX_DATA_LENGTH")
    private Long maxDataLength;
    @Column(name = "INDEX_LENGTH")
    private Long indexLength;
    @Column(name = "DATA_FREE")
    private Long dataFree;
    @Column(name = "AUTO_INCREMENT")
    private Long autoIncrement;
    @Column(name = "CREATE_TIME")
    private Instant createTime;
    @Column(name = "UPDATE_TIME")
    private Instant updateTime;
    @Column(name = "CHECK_TIME")
    private Instant checkTime;
    @Column(name = "TABLE_COLLATION")
    private String tableCollation;
    private Long checksum;
    @Column(name = "CREATE_OPTIONS")
    private String createOptions;
    @Column(name = "TABLE_COMMENT")
    private String tableComment;

    @Data
    static class TableId implements Serializable {
        private String tableCatalog;
        private String tableSchema;
        private String tableName;
    }
}
