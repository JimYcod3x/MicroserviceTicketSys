package com.miss.services.event.repositories;

import com.miss.services.common.domains.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepo extends JpaRepository<Events, Integer> {
}
