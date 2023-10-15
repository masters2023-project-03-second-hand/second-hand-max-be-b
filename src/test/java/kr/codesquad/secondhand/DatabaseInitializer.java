package kr.codesquad.secondhand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

@Profile("test")
@Component
public class DatabaseInitializer {

    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE %s";
    private static final String AUTO_INCREMENT_INIT_QUERY = "ALTER TABLE %s AUTO_INCREMENT = 1";

    private final Set<String> tableNames = new HashSet<>();

    @Autowired
    private EntityManager em;
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void collectTableNames() {
        try {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException("테이블 이름을 불러올 수 없습니다.");
        }
    }

    @Transactional
    public void truncateTables() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String tableName : tableNames) {
            em.createNativeQuery(String.format(TRUNCATE_QUERY, tableName)).executeUpdate();
            if (tableName.equals("RUNNING_TASK")) {
                return;
            }
            em.createNativeQuery(String.format(AUTO_INCREMENT_INIT_QUERY, tableName)).executeUpdate();
        }
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
