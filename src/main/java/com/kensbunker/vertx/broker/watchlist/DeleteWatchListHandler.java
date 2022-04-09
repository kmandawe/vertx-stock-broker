package com.kensbunker.vertx.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);
  private final Map<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    String accountId = WatchListRestApi.getAccountId(context);
    final WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
    LOG.info("Deleted: {}, Remaining: {}", deleted, watchListPerAccount.values());
    context.response().end(deleted.toJsonObject().toBuffer());
  }
}
