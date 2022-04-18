package com.kensbunker.vertx.broker.db;

import com.kensbunker.vertx.broker.config.BrokerConfig;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DBPools {

  public static Pool createMySQLPool(final BrokerConfig configuration, final Vertx vertx) {
    final var connectionOptions =
        new MySQLConnectOptions()
            .setHost(configuration.getDbConfig().getHost())
            .setPort(configuration.getDbConfig().getPort())
            .setDatabase(configuration.getDbConfig().getDatabase())
            .setUser(configuration.getDbConfig().getUser())
            .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions().setMaxSize(4);

    return MySQLPool.pool(vertx, connectionOptions, poolOptions);
  }

  public static Pool createPgPool(final BrokerConfig configuration, final Vertx vertx) {
    final var connectionOptions =
        new PgConnectOptions()
            .setHost(configuration.getDbConfig().getHost())
            .setPort(configuration.getDbConfig().getPort())
            .setDatabase(configuration.getDbConfig().getDatabase())
            .setUser(configuration.getDbConfig().getUser())
            .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions().setMaxSize(4);

    return PgPool.pool(vertx, connectionOptions, poolOptions);
  }
}
