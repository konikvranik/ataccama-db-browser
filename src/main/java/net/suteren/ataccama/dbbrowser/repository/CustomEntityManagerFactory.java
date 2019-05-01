package net.suteren.ataccama.dbbrowser.repository;

import net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord;
import net.suteren.ataccama.dbbrowser.repository.connections.DbConnectionRepository;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.*;

@Component
public class CustomEntityManagerFactory {

    private final DbConnectionRepository repository;

    /**
     * Factory creating entity manager by database reference for connections stored in configuration database.
     *
     * @param repository CRUD repository with database connection information
     */
    public CustomEntityManagerFactory(DbConnectionRepository repository) {
        this.repository = repository;
    }

    /**
     * Create entity manager for database stored in configuration database.
     *
     * @param databaseName Reference name of database to connect to
     * @return Entity manager for database specified by databaseName
     */
    public EntityManager getEntityManager(String databaseName){
        ConnectionRecord connDef = repository.findByName(databaseName).orElseThrow(IllegalArgumentException::new);
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", connDef.getHostname(), connDef.getPort(), connDef.getDatabaseName());
        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(
                archiverPersistenceUnitInfo(),
                Map.of(
                        JPA_JDBC_URL, jdbcUrl,
                        JPA_JDBC_USER, connDef.getUsername(),
                        JPA_JDBC_PASSWORD, connDef.getPassword()
                ));

        return entityManagerFactory.createEntityManager();
    }

    private static PersistenceUnitInfo archiverPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "ApplicationPersistenceUnit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<java.net.URL> getJarFileUrls() {
                try {
                    return Collections.list(this.getClass()
                            .getClassLoader()
                            .getResources(""));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                return Collections.emptyList();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }
}
