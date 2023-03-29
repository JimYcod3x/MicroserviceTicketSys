package com.miss.services.event.services;

import com.miss.services.common.apis.TicketApi;
import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Tickets;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "TicketsApiService", url = "${svc.url.ticket}")
public interface TicketsServices extends TicketApi {
    @Override
    @GetMapping("/index")
    public List<Tickets> index() throws MetrixException;
}