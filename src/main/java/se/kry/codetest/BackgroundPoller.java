package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import se.kry.codetest.dbProvider.ServiceRepo;
import se.kry.codetest.dbProvider.model.ServiceDBModel;
import se.kry.codetest.utils.HttpStatusCodeUtils;
import se.kry.codetest.utils.ServiceStatus;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpResponse;

import java.net.http.HttpRequest;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackgroundPoller {
  ServiceRepo  serviceRepo;

  HttpClient httpClient;


  public BackgroundPoller(ServiceRepo repo) {
    this.serviceRepo = repo;
    this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();
  }

  public Future<Void> pollServices() {
  Future<List<ServiceDBModel>> servicesFuture =  serviceRepo.getAll();
  servicesFuture.setHandler(t -> {
      if(t.succeeded()) {
        List<ServiceDBModel> services = t.result();
        services.forEach(service -> {
          try {
            service.setPreviousStatus(service.getCurrentStatus());
            LocalDateTime dateTime = LocalDateTime.now();
            ZoneId systemZone = ZoneId.systemDefault();
            Instant instant = Instant.now();
            ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(instant);
            service.setLastUpdated(Date.from(dateTime.toInstant(currentOffsetForMyZone)));
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(service.getUrl()))
                    .build();
            HttpResponse<String> response = null;
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            service.setCurrentStatus(HttpStatusCodeUtils.getStatus(response.statusCode()).toString());
          } catch (IOException | IllegalArgumentException | InterruptedException e ) {
           service.setCurrentStatus(ServiceStatus.FAIL.toString());
          }
          serviceRepo.updateInternal(service);
        });
      }
      else {
        System.out.println("no services to be polled");
      }
    });
    return Future.succeededFuture();
  }
}