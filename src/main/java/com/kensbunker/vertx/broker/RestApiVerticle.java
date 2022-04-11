package com.kensbunker.vertx.broker;

import com.kensbunker.vertx.broker.assets.AssetsRestApi;
import com.kensbunker.vertx.broker.config.BrokerConfig;
import com.kensbunker.vertx.broker.config.ConfigLoader;
import com.kensbunker.vertx.broker.quotes.QuotesRestApi;
import com.kensbunker.vertx.broker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
        .onFailure(startPromise::fail)
        .onSuccess(
            configuration -> {
              LOG.info("Retrieved configuration {}", configuration);
              startHttpServerAndAttachRoutes(startPromise, configuration);
            });
  }

  private void startHttpServerAndAttachRoutes(
      final Promise<Void> startPromise, final BrokerConfig configuration) {
    // One pool for each Rest API Verticle
    final Pool db = createDbPool(configuration);

    final Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create()).failureHandler(handleFailure());

    AssetsRestApi.attach(restApi, db);
    QuotesRestApi.attach(restApi, db);
    WatchListRestApi.attach(restApi);

    vertx
        .createHttpServer()
        .requestHandler(restApi)
        .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
        .listen(
            configuration.getServerPort(),
            http -> {
              if (http.succeeded()) {
                startPromise.complete();
                LOG.info("HTTP server started on port {}", configuration.getServerPort());
              } else {
                startPromise.fail(http.cause());
              }
            });
  }

  private PgPool createDbPool(final BrokerConfig configuration) {
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

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        // Ignore completed response
        return;
      }
      LOG.error("Route Error:", errorContext.failure());
      errorContext
          .response()
          .setStatusCode(500)
          .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
