package com.miss.services.common.apis;

import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Venues;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface VenueApi {
    @GetMapping({"","/","/index"})
    public List<Venues> index() throws MetrixException;

    @PostMapping("/create")
    public Venues create(@RequestBody Venues newVenue) throws MetrixException;

    @GetMapping("/retrieve/{venueId}")
    public Optional<Venues> retrieve(@PathVariable("venueId") Integer venueId) throws MetrixException;

    @PostMapping("/update/{venueId}")
    public Venues update(@PathVariable("venueId") Integer venueId, @RequestBody Venues updatedVenue) throws MetrixException;

    @GetMapping("/delete/{venueId}")
    public ResponseEntity<Void> delete(@PathVariable("venueId") Integer venueId) throws MetrixException;
}