package com.miss.webportal.services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.miss.services.common.apis.TicketApi;
import com.miss.services.common.domains.Tickets;
import com.miss.services.common.domains.MetrixException;

/* 1. How can you use Feign Client here, with name "TicketsApiService" and the URL is ${svc.url.ticket} ?*/
@FeignClient(name = "TicketsApiService", url = "${svc.url.ticket}")
public interface TicketsServices extends TicketApi {
    @Override
    @GetMapping("/index")
    public List<Tickets> index() throws MetrixException;
}