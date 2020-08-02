package se.kry.codetest.dbProvider;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import se.kry.codetest.DBConnector;
import se.kry.codetest.dbProvider.model.ServiceDBModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceRepo {

    DBConnector _dbConnector;

    public ServiceRepo (DBConnector connector) {
        this._dbConnector = connector;
    }

    public Future<String> createService(ServiceDBModel serviceDBModel) {
        String query = "INSERT INTO polled_services (name, url, current_status, previous_status, last_updated, created_by)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        JsonArray array = new JsonArray();
        array.add(serviceDBModel.getName());
        array.add(serviceDBModel.getUrl());
        array.add(Optional.ofNullable(serviceDBModel.getCurrentStatus()).orElse(""));
        array.add(Optional.ofNullable(serviceDBModel.getPreviousStatus()).orElse(""));
        array.add(Optional.ofNullable(serviceDBModel.getLastUpdated()).orElse(new Date()).toString());
        array.add(Optional.ofNullable(serviceDBModel.getCreatedBy()).orElse(""));
        Future<UpdateResult> updateResult = this._dbConnector.saveOrUpdate(query, array);
        Future<String> futureResult = Future.future();
        updateResult.setHandler(ar -> {
            if (ar.succeeded() && ar.result().getUpdated() > 0) {
                 futureResult.complete("created");
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
        return futureResult;
    }


    public Future<ServiceDBModel> getByNameAndCreatedBy(String name, String createdBy) {
        String query = "SELECT * FROM polled_services WHERE name = ? AND created_by = ?";
        JsonArray array = new JsonArray();
        array.add(name);
        array.add(createdBy);

        Future<ResultSet> resultSetFuture = this._dbConnector.query(query, array);

        Future<ServiceDBModel> futureResult = Future.future();
        resultSetFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                ServiceDBModel result  = ServiceDBModel.fromJson(ar.result().getRows().get(0));
                futureResult.complete(result);
            } else {
                System.out.println(ar.cause().getMessage());
                futureResult.complete();
            }
        });
        return futureResult;
    }

    public Future<List<ServiceDBModel>> getForUser(String createdBy) {
        String query = "SELECT * FROM polled_services WHERE created_by = ?";
        JsonArray array = new JsonArray();
        array.add(createdBy);
        Future<ResultSet> resultSetFuture = this._dbConnector.query(query, array);
        List<ServiceDBModel> result = new ArrayList<>();
        Future<List<ServiceDBModel>> futureResult = Future.future();
        resultSetFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                result.addAll(ar.result().getRows().stream().map(ServiceDBModel::fromJson).collect(Collectors.toList()));
                futureResult.complete(result);
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
        return futureResult;
    }

    public Future<List<ServiceDBModel>> getAll() {
        String query = "SELECT * FROM polled_services";
        Future<ResultSet> resultSetFuture = this._dbConnector.query(query);
        List<ServiceDBModel> result = new ArrayList<>();
        Future<List<ServiceDBModel>> futureResult = Future.future();
        resultSetFuture.setHandler(ar -> {
            if (ar.succeeded()) {
                result.addAll(ar.result().getRows().stream().map(ServiceDBModel::fromJson).collect(Collectors.toList()));
                futureResult.complete(result);
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
       return futureResult;
    }


    public Future<ServiceDBModel> getById(int id) {
        String query = "SELECT * FROM polled_services WHERE _id = ? ";
        JsonArray array = new JsonArray();
        array.add(id);
        Future<ResultSet> resultSetFuture = this._dbConnector.query(query, array);

        Future<ServiceDBModel> futureResult = Future.future();
        resultSetFuture.setHandler(ar -> {
            if (ar.succeeded() && ar.result().getNumRows() > 0) {
                ServiceDBModel result = ServiceDBModel.fromJson(ar.result().getRows().get(0));
                futureResult.complete(result);
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
        return futureResult;
    }

    public Future<String> delete(int id) {
        String query = "DELETE FROM polled_services WHERE _id = ?";
        JsonArray array = new JsonArray();
        array.add(id);
        Future<UpdateResult> updateResult = this._dbConnector.saveOrUpdate(query, array);
        Future<String> futureResult = Future.future();
        updateResult.setHandler(ar -> {
            if (ar.succeeded() && ar.result().getUpdated() > 0) {
                futureResult.complete("deleted");
            } else {
                futureResult.complete("unable to delete");
            }
        });
        return futureResult;
    }

    public Future<String> updateClient(ServiceDBModel updatedRecord) {
        String query = "UPDATE polled_services SET " +
                "name = ?,  " +
                "last_updated = ? " +
                "WHERE _id = ?";
        JsonArray array = new JsonArray();
        array.add(updatedRecord.getName());
        array.add(updatedRecord.getLastUpdated());
        array.add(updatedRecord.getId());
        Future<UpdateResult> updateResult = this._dbConnector.saveOrUpdate(query, array);
        Future<String> futureResult = Future.future();
        updateResult.setHandler(ar -> {
            if (ar.succeeded() && ar.result().getUpdated() > 0) {
                futureResult.complete("updated");
            } else {
                futureResult.complete("unable to update");
            }
        });
        return futureResult;
    }

    public Future<String> updateInternal(ServiceDBModel updatedRecord) {
        String query = "UPDATE polled_services SET current_status = ?,  " +
                "previous_status = ?, " +
                "last_updated = ? " +
                "WHERE _id = ?";
        JsonArray array = new JsonArray();
        array.add(updatedRecord.getCurrentStatus());
        array.add(updatedRecord.getPreviousStatus());
        array.add(Optional.ofNullable(updatedRecord.getLastUpdated()).orElse(new Date()).toString());
        array.add(updatedRecord.getId());
        Future<UpdateResult> updateResult = this._dbConnector.saveOrUpdate(query, array);
        Future<String> futureResult = Future.future();
        updateResult.setHandler(ar -> {
            if (ar.succeeded() && ar.result().getUpdated() > 0) {
                futureResult.complete("updated");
            } else {
                futureResult.complete("unable to update");
            }
        });
        return futureResult;
    }
}