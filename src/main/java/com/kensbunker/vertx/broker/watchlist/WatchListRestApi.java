package com.kensbunker.vertx.broker.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(final Router parent) {

    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    final String path = "/account/watchlist/:accountId";

    parent
        .get(path)
        .handler(
            context -> {
              var accountId = context.pathParam("accountId");
              LOG.debug("{} for account {}", context.normalizedPath(), accountId);
              var maybeWatchList =
                  Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
              if (maybeWatchList.isEmpty()) {
                context
                    .response()
                    .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                    .end(
                        new JsonObject()
                            .put(
                                "message", "watchlist for account " + accountId + " not available!")
                            .put("path", context.normalizedPath())
                            .toBuffer());
              }
              context.response().end(maybeWatchList.get().toJsonObject().toBuffer());
            });
    parent.put(path).handler(context -> {
      var accountId = context.pathParam("accountId");
      LOG.debug("{} for account {}", context.normalizedPath(), accountId);

      var json = context.getBodyAsJson();
      var watchList = json.mapTo(WatchList.class);
      watchListPerAccount.put(UUID.fromString(accountId), watchList);
      context.response().end(json.toBuffer());
    });

    parent.delete(path).handler(context -> {});
  }
}
