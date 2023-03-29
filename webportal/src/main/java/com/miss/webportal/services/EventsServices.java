package com.miss.webportal.services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.miss.services.common.apis.EventsApi;
import com.miss.services.common.domains.Events;
import com.miss.services.common.domains.MetrixException;

/* 1. How can you use Feign Client here, with name "EventsApiService" and the URL is ${svc.url.event} ?*/
@FeignClient(name = "EventsApiService", url = "${svc.url.event}")
public interface EventsServices extends EventsApi {
    @Override
    @GetMapping("/index")
    public List<Events> index() throws MetrixException;
}