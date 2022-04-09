package com.kensbunker.vertx.broker.quotes;

import com.kensbunker.vertx.broker.assets.Asset;
import com.kensbunker.vertx.broker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router parent) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> cachedQuotes.put(symbol, initRandomQuote(symbol)));

    parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
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
