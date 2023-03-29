package com.miss.services.venue.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miss.services.common.domains.Venues;

public interface VenuesRepo extends JpaRepository<Venues, Integer> {
    
}