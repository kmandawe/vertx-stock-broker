package com.kensbunker.vertx.broker.watchlist;

import com.kensbunker.vertx.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.List;

@Value
public class WatchList {

  List<Asset> assets;

  JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
