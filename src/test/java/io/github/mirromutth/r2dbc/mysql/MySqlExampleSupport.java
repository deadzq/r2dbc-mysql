/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mirromutth.r2dbc.mysql;

import com.zaxxer.hikari.HikariDataSource;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.test.Example;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

/**
 * Base class considers implementations of {@link Example}.
 */
abstract class MySqlExampleSupport implements Example<String> {

    private final MySqlConnectionFactory connectionFactory;

    private final JdbcOperations jdbcOperations;

    MySqlExampleSupport(MySqlConnectionConfiguration configuration) {
        this.connectionFactory = MySqlConnectionFactory.from(configuration);
        this.jdbcOperations = getJdbc(configuration);
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    @Override
    public String getCreateTableWithAutogeneratedKey() {
        return "CREATE TABLE test (id INT PRIMARY KEY AUTO_INCREMENT, value INT)";
    }

    @Override
    public String getIdentifier(int index) {
        return "v" + index;
    }

    @Override
    public String getPlaceholder(int index) {
        return "?v" + index;
    }

    @Override
    public String clobType() {
        return "TEXT";
    }

    private static JdbcOperations getJdbc(MySqlConnectionConfiguration configuration) {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(String.format("jdbc:mysql://%s:%d/r2dbc", configuration.getHost(), configuration.getPort()));
        dataSource.setUsername(configuration.getUsername());
        dataSource.setPassword(Optional.ofNullable(configuration.getPassword()).map(Object::toString).orElse(null));
        dataSource.setMaximumPoolSize(1);

        return new JdbcTemplate(dataSource);
    }
}