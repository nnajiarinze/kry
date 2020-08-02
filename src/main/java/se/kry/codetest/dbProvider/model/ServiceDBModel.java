package se.kry.codetest.dbProvider.model;

import io.vertx.core.json.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;


public class ServiceDBModel {

    private int id;
    private String name;
    private String url;
    private Date dateCreated;
    private String currentStatus;
    private String previousStatus;
    private Date lastUpdated;
    private String createdBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public static ServiceDBModel fromJson(JsonObject object) {
        ServiceDBModel serviceDBModel = new ServiceDBModel();
        serviceDBModel.setName(object.getString("name"));
        if (Objects.nonNull(object.getValue("_id"))) {
            serviceDBModel.setId(object.getInteger("_id"));
        }

        serviceDBModel.setCreatedBy(object.getString("created_by"));
        serviceDBModel.setCurrentStatus(object.getString("current_status"));
        serviceDBModel.setPreviousStatus(object.getString("previous_status"));
        serviceDBModel.setUrl(object.getString("url"));
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Instant instant = Instant.now();
            ZoneId systemZone = ZoneId.systemDefault(); // my timezone
            ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(instant);
            LocalDateTime dateTime = LocalDateTime.parse(object.getString("date_created"), f);
           serviceDBModel.setDateCreated(Date.from(dateTime.toInstant(currentOffsetForMyZone)));
            if(object.getString("last_updated") != null) {
                 dateTime = LocalDateTime.parse(object.getString("date_created"), f);
                serviceDBModel.setLastUpdated(Date.from(dateTime.toInstant(currentOffsetForMyZone)));
            }
        } catch ( NullPointerException parseException) {
            serviceDBModel.setDateCreated(null);
            serviceDBModel.setLastUpdated(null);
        }
        return serviceDBModel;
    }
    public static JsonObject toJson(ServiceDBModel dbModel) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("_id", dbModel.getId());
        jsonObject.put("name", dbModel.getName());
        jsonObject.put("created_by", dbModel.getCreatedBy());
        jsonObject.put("current_status", dbModel.getCurrentStatus());
        jsonObject.put("previous_status", dbModel.getPreviousStatus());
        jsonObject.put("url", dbModel.getUrl());
        jsonObject.put("date_created", dbModel.getDateCreated());
        jsonObject.put("last_updated", dbModel.getLastUpdated());

        return jsonObject;
    }
}
