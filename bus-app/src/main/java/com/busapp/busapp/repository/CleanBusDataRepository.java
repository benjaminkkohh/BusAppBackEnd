package com.busapp.busapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.busapp.busapp.entity.CleanBusData; // Update the import to use CleanBusData

public interface CleanBusDataRepository extends JpaRepository<CleanBusData, Long> {
    // You can add custom query methods here if needed
    @Query("SELECT DISTINCT cbd.vehicleRef FROM CleanBusData cbd")

    List<String> findDistinctVehicleRef();

    @Query("SELECT DISTINCT cbd.publishedLineName FROM CleanBusData cbd")

    List<String> findDistinctPublishedLineName();

}


