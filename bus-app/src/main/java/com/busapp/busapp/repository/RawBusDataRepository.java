package com.busapp.busapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.busapp.busapp.entity.RawBusData;

public interface RawBusDataRepository extends JpaRepository<RawBusData, Long> {
}
