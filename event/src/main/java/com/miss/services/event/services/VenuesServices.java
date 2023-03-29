package com.miss.services.event.services;

import com.miss.services.common.apis.VenueApi;
import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Venues;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "TicketsApiService", url = "${svc.url.venue}")
public interface VenuesServices extends VenueApi {
    @Override
    @GetMapping("/index")
    public List<Venues> index() throws MetrixException;
}
