package com.bookingapp.infrastructure.persistence.repository;

import com.bookingapp.infrastructure.persistence.entity.AccommodationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
}
