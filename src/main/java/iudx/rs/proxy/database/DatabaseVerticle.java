package iudx.rs.proxy.database;

import static iudx.rs.proxy.common.Constants.DB_SERVICE_ADDRESS;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import iudx.rs.proxy.database.example.postgres.PostgresServiceImpl;

public class DatabaseVerticle extends AbstractVerticle {

  private ServiceBinder binder;
  private MessageConsumer<JsonObject> consumer;
  private DatabaseService dbServiceImpl;

  @Override
  public void start() throws Exception {

    binder = new ServiceBinder(vertx);
    dbServiceImpl = new PostgresServiceImpl(vertx, config());
    consumer = binder.setAddress(DB_SERVICE_ADDRESS).register(DatabaseService.class, dbServiceImpl);
  }

  @Override
  public void stop() {
    binder.unregister(consumer);
  }
}
