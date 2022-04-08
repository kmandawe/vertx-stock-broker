package com.kensbunker.vertx.broker.quotes;

import com.kensbunker.vertx.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent) {

    parent
        .get("/quotes/:asset")
        .handler(
            context -> {
              final String assetParam = context.pathParam("asset");
              LOG.debug("Asset parameter: {}", assetParam);

              var quote = initRandomQuote(assetParam);

              final JsonObject response = quote.toJsonObject();

              LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
              context.response().end(response.toBuffer());
            });
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
        .asset(new Asset(assetParam))
        .volume(randomValue())
        .ask(randomValue())
        .bid(randomValue())
        .lastPrice(randomValue())
        .volume(randomValue())
        .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
