package com.example;

import com.example.generated.enums.SomeType;
import com.example.generated.tables.records.PartitionedTableRecord;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Explain;
import org.jooq.SQLDialect;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import static com.example.generated.tables.PartitionedTable.PARTITIONED_TABLE;

public class TestApplication extends Application<TestConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(TestApplication.class);

    public static void main(String[] args) throws Exception {
        new TestApplication().run(args);
    }

    @Override
    public String getName() {
        return "test-app";
    }

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(TestConfiguration configuration,
                    Environment environment) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.getJdbcUrl());
        hikariConfig.setUsername(configuration.getJdbcUser());
        hikariConfig.setPassword(configuration.getJdbcPassword());

        final DataSource hikariDataSource = new HikariDataSource(hikariConfig);

        final DSLContext dslContext = DSL.using(
                new DefaultConfiguration()
                        .set(hikariDataSource)
                        .set(SQLDialect.POSTGRES)
                        .set(SettingsTools.defaultSettings()));

        final DeleteConditionStep<PartitionedTableRecord> deleteQuery = dslContext.deleteFrom(PARTITIONED_TABLE)
                .where(PARTITIONED_TABLE.SOME_TYPE.in(SomeType.type_1, SomeType.type_2)
                        .and(PARTITIONED_TABLE.TEXT_FIELD.eq("doesnt matter")));

        final Explain explain = dslContext.explain(deleteQuery);

        final DeleteConditionStep<PartitionedTableRecord> deleteQueryWithCustomCondition = dslContext.deleteFrom(PARTITIONED_TABLE)
                .where(DSL.condition(PARTITIONED_TABLE.SOME_TYPE.getQualifiedName() + " IN ('" + SomeType.type_1 + "', '" + SomeType.type_2 + "')")
                        .and(PARTITIONED_TABLE.TEXT_FIELD.eq("doesnt matter")));

        final Explain explainWithCustomCondition = dslContext.explain(deleteQueryWithCustomCondition);

        LOG.info("Fully generated delete query:\n{}", deleteQuery.getSQL(true));
        LOG.info("Explain of fully generated delete query:\n{}", explain);
        LOG.info("Delete query with custom condition:\n{}", deleteQueryWithCustomCondition.getSQL(true));
        LOG.info("Explain of delete query with custom condition:\n{}", explainWithCustomCondition);

        System.exit(0);
    }

}