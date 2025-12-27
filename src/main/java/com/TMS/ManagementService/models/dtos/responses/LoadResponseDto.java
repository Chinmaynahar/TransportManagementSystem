package com.TMS.ManagementService.models.dtos.responses;

import com.TMS.ManagementService.models.entities.Bid;
import com.TMS.ManagementService.models.entities.Load;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadResponseDto implements Serializable {
    UUID loadId;
    String shipperId;
    String loadingCity;
    String unloadingCity;
    String loadingDate;
    String productType;
    Double weight;
    String weightUnit;
    String truckType;
    Integer noOfTrucks;
    String status;
    String datePosted;
    List<BidResponseDto> activeBids;

    public LoadResponseDto(UUID loadId,String status) {
        this.loadId=loadId;
        this.status = status;
    }

    public LoadResponseDto(UUID loadId, String shipperId, String loadingCity, String unloadingCity, String loadingDate, String productType, double weight, String weightUnit, String truckType, int noOfTrucks, String status, String datePosted, List<BidResponseDto> activeBids) {
        this.loadId = loadId;
        this.shipperId = shipperId;
        this.loadingCity = loadingCity;
        this.unloadingCity = unloadingCity;
        this.loadingDate = loadingDate;
        this.productType = productType;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.status = status;
        this.datePosted = datePosted;
        this.activeBids = activeBids;
    }

    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public String getShipperId() {
        return shipperId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public String getLoadingCity() {
        return loadingCity;
    }

    public void setLoadingCity(String loadingCity) {
        this.loadingCity = loadingCity;
    }

    public String getUnloadingCity() {
        return unloadingCity;
    }

    public void setUnloadingCity(String unloadingCity) {
        this.unloadingCity = unloadingCity;
    }

    public String getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(String loadingDate) {
        this.loadingDate = loadingDate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public Integer getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setNoOfTrucks(int noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public LoadResponseDto() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public List<BidResponseDto> getActiveBids() {
        return activeBids;
    }

    public void setActiveBids(List<BidResponseDto> activeBids) {
        this.activeBids = activeBids;
    }

    public static LoadResponseDto from(Load load){
        LoadResponseDto dto=new LoadResponseDto();
        dto.setLoadId(load.getLoadId());
        dto.setShipperId(load.getShipperId());
        dto.setLoadingCity(load.getLoadingCity());
        dto.setUnloadingCity(load.getUnloadingCity());
        dto.setLoadingDate(load.getLoadingDate().toString());
        dto.setProductType(load.getProductType());
        dto.setWeight(load.getWeight());
        dto.setWeightUnit(load.getWeightUnit().toString());
        dto.setTruckType(load.getTruckType());
        dto.setNoOfTrucks(load.getNoOfTrucks());
        dto.setStatus(load.getStatus().name());
        dto.setDatePosted(load.getDatePosted().toString());
        return dto;
    }
    public static LoadResponseDto from(Load load,List<Bid>bids){
        LoadResponseDto dto=from(load);
        dto.setActiveBids(bids.stream().map(BidResponseDto::from).collect(Collectors.toList()));
        return dto;
    }
}

