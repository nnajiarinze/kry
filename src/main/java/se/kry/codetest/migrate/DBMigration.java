package se.kry.codetest.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.DBConnector;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DBConnector connector = new DBConnector(vertx);

    connector.query(getCreateTableStatement()).setHandler(done -> {
      if (done.succeeded()) {
        System.out.println("completed creating tables");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });

    connector.query(getCreateIndexOnCreatedByStatement()).setHandler(done -> {
      if (done.succeeded()) {
        System.out.println("completed creating indexes");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });
  }


  private static String getCreateTableStatement() {
    return "CREATE TABLE IF NOT EXISTS polled_services (" +
            "_id INTEGER PRIMARY KEY, " +
            "name VARCHAR(150) NOT NULL, " +
            "url VARCHAR(500) NOT NULL, " +
            "date_created DATETIME NOT_NULL DEFAULT current_timestamp," +
            "current_status VARCHAR(10), " +
            "previous_status VARCHAR(10), " +
            "last_updated DATETIME NULL, " +
            "created_by VARCHAR(50) NOT NULL); ";

  }

  private static String getCreateIndexOnCreatedByStatement() {
    return "CREATE UNIQUE INDEX IF NOT EXISTS IDX_NAME on polled_services(name); ";
  }

  private static String creteIndexNameForServiceNameStatement() {
    return "CREATE UNIQUE INDEX IF NOT EXISTS  IDX_CREATED_BY on polled_services(created_by); ";
  }

}
