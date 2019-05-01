package net.suteren.ataccama.dbbrowser.entity.connections;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.AUTO;

/**
 * Information required to successfully connect to MySQL database.
 */
@Entity
@Table(name = "connections")
@Data
@ApiModel(description = "All details about the connection. ")
public class ConnectionRecord {

    /**
     * Unique record identitfier.
     */
    @Id
    @GeneratedValue(strategy = AUTO)
    @ApiModelProperty
    private Long id;
    /**
     * Reference name of database connection.
     */
    @ApiModelProperty
    private String name;
    /**
     * Hostname of the MySQL server.
     */
    @ApiModelProperty
    private String hostname;
    /**
     * Port of the MySQL server.
     */
    @ApiModelProperty
    private Integer port;
    /**
     * Database to connect to..
     */
    @ApiModelProperty
    private String databaseName;
    /**
     * Database user.
     */
    @ApiModelProperty
    private String username;
    /**
     * Database password.
     */
    @ApiModelProperty
    private String password;
}
