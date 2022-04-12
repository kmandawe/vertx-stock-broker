package com.kensbunker.vertx.broker.watchlist;

import com.kensbunker.vertx.broker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  public PutWatchListDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    var json = context.getBodyAsJson();
    var watchList = json.mapTo(WatchList.class);

    watchList
        .getAssets()
        .forEach(
            asset -> {
              Map<String, Object> parameters = new HashMap<>();
              parameters.put("account_id", accountId);
              parameters.put("asset", asset.getName());
              SqlTemplate.forUpdate(
                      db, "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset})")
                  .execute(parameters)
                  .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"))
                  .onSuccess(
                      result -> {
                        if (!context.response().ended()) {
                          context
                              .response()
                              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                              .end();
                        }
                      });
            });
  }
}
