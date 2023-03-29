package com.miss.services.ticket.services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.miss.services.common.apis.VenueApi;
import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Venues;

/* 1. How can you use Feign Client here, with name "VenuesApiService" and the URL is ${svc.url.venue} ?*/
@FeignClient(name = "VenuesApiService", url = "${svc.url.venue}")
public interface VenuesServices extends VenueApi {
    @Override
    @GetMapping("/index")
    public List<Venues> index() throws MetrixException;
}