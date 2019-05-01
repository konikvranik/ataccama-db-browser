package net.suteren.ataccama.dbbrowser.repository.connections;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CRUD repository of database connection used for browsing data, metadata and counting statistics.
 */
@Api
@RepositoryRestResource
@Transactional
public interface DbConnectionRepository extends CrudRepository<ConnectionRecord, Long> {
    @ApiOperation("find connection by name")
    Optional<ConnectionRecord> findByName(String name);
}
