package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.dbProvider.ServiceRepo;
import se.kry.codetest.dbProvider.model.ServiceDBModel;

import java.util.regex.Pattern;


public class MainVerticle extends AbstractVerticle {

  //TODO use this
  private DBConnector connector;
  private ServiceRepo repo;
  private BackgroundPoller poller;

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    repo = new ServiceRepo(connector);
    poller =  new BackgroundPoller(repo);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices());
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router){
    router.route("/*").handler(StaticHandler.create());
    router.get("/service").handler(this::getAllServices);
    router.post("/service").handler(this::createService);
    router.put("/service/:id").handler(this::updateService);
    router.delete("/service/:id").handler(this::deleteService);
    router.get("/service/:id").handler(this::getServiceById);
    router.put();
  }

  private void getServiceById(RoutingContext routingContext) {
    repo.getById(Integer.parseInt(routingContext.request().getParam("id"))).setHandler(t -> {
      if(t.succeeded()) {
        routingContext.response().putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(t.result()));
      }
      else {
        sendError(500, routingContext.response());
      }
    });
  }

  private void deleteService(RoutingContext routingContext) {
    repo.delete(Integer.parseInt(routingContext.pathParam("id"))).setHandler(t -> {
      if(t.succeeded()) {
        routingContext.response().putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(t.result()));
      }
      else {
        sendError(500, routingContext.response());
      }
    });
  }

  private void updateService(RoutingContext routingContext) {
    if (isValid(routingContext.getBodyAsJson().getString("url"))) {
      repo.updateClient(ServiceDBModel.fromJson(routingContext.getBodyAsJson())).setHandler(t -> {
        if(t.succeeded()) {
          routingContext.response().putHeader("Content-Type", "application/json")
                  .end(Json.encodePrettily(t.result()));
        }
        else {
          sendError(500, routingContext.response());
        }
      });
    } else {
    sendError(400, routingContext.response().setStatusMessage("Invalid Service Url"));
  }

  }

  private void createService(RoutingContext routingContext) {
     if (isValid(routingContext.getBodyAsJson().getString("url"))) {
       repo.createService(ServiceDBModel.fromJson(routingContext.getBodyAsJson())).setHandler(t -> {
         if(t.succeeded()) {
           routingContext.response().putHeader("Content-Type", "application/json")
                   .end(Json.encodePrettily(t.result()));
         }
         else {
           sendError(500, routingContext.response());
         }
       });
     } else {
       sendError(400, routingContext.response(), "Invalid Url");
     }
  }

  private boolean isValid(String url) {
   String pattern  = "^((((https?)://)|(mailto:|news:))" +
            "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
            "([).!';/?:,][[:blank:]])?$";
    Pattern urlPattern = Pattern.compile(pattern);
    return urlPattern.matcher(url).matches();
  }

  private void getAllServices(RoutingContext routingContext) {
      repo.getAll().setHandler(t -> {
        if(t.succeeded()) {
          routingContext.response().putHeader("Content-Type", "application/json")
                 .end(Json.encodePrettily(t.result()));
        }
        else {
          sendError(500, routingContext.response());
        }
      });
  }

  private void sendError(int i, HttpServerResponse response) {
    response.setStatusCode(i).end();
  }

  private void sendError(int i, HttpServerResponse response, String errorMessage) {
    response.setStatusCode(i).end(errorMessage);
  }
}



