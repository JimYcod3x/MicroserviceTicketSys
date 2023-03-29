package com.miss.services.event.controllers;

import com.miss.services.common.apis.EventsApi;
import com.miss.services.common.domains.Events;
import com.miss.services.common.domains.EventsForm;
import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Venues;
import com.miss.services.event.repositories.EventsRepo;
import com.miss.services.event.services.TicketsServices;
import com.miss.services.event.services.VenuesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/* 1. What should you add here to make it become a RESTful Controller */
@RestController
@RequestMapping(value = "/api/events")
public class EventsApiController implements EventsApi{

    @Autowired
    private EventsRepo repo;

    @Autowired
    private VenuesServices venuesSvc;

    @Autowired
    private TicketsServices ticketsSvc;

    @Override
    public List<Events> index() throws MetrixException {
        return repo.findAll();
    }

    @Override
    public List<Events> listEventsByVenueId(Integer venueId) throws MetrixException {
        return repo.findAll().stream().filter(e -> e.getVenue().getId() == venueId).toList();
    }

    @Override
    public Events create(EventsForm newEventForm) throws MetrixException {
        Venues venue = getVenues(newEventForm);
        Events newEvents = new Events(0, newEventForm.getEventName(), false, newEventForm.getEventDtm(), venue, null);
        return repo.save(newEvents);
    }

    @Override
    public Optional<Events> retrieve(Integer eventId) throws MetrixException {
        return repo.findById(eventId);
    }

    @Override
    public Events update(Integer eventId, EventsForm updatedEvent) throws MetrixException {
        Optional<Events> oDbEvents = repo.findById(eventId);
        if(!oDbEvents.isPresent()){
            throw new MetrixException(-1, String.format("EventsApiController::update()::Event with Id [%d] not found", eventId), "");
        }
        if(!ticketsSvc.listTicketsByEventId(eventId).isEmpty()){
            throw new MetrixException(-1, String.format("EventsApiController::update()::Event with Id [%d] already have ticket sell, cannot delete", eventId), "");
        }
        Events dbEvents = oDbEvents.get();
        if(dbEvents.getEventDtm().before(new Date())){
            throw new MetrixException(-1, String.format("EventsApiController::update()::Event with Id [%d] already finished", eventId), "");
        }
        Venues venue = getVenues(updatedEvent);
        dbEvents.setEventDtm(updatedEvent.getEventDtm());
        dbEvents.setEventName(updatedEvent.getEventName());
        dbEvents.setStartSell(updatedEvent.isStartSell());
        if(updatedEvent.getVenueId() != dbEvents.getId()){
            dbEvents.setVenue(venue);
        }
        return repo.save(dbEvents);
    }

    @Override
    public Events changeSellStatus(Integer eventId, boolean isStartSell) throws MetrixException{
        Optional<Events> oDbEvents = repo.findById(eventId);
        if(!oDbEvents.isPresent()){
            throw new MetrixException(-1, String.format("EventsApiController::update()::Event with Id [%d] not found", eventId), "");
        }
        Events dbEvents = oDbEvents.get();
        if(dbEvents.getEventDtm().before(new Date())){
            throw new MetrixException(-1, String.format("EventsApiController::update()::Event with Id [%d] already finished", eventId), "");
        }
        dbEvents.setStartSell(isStartSell);
        return repo.save(dbEvents);
    }

    @Override
    public ResponseEntity<Void> delete(Integer eventId) throws MetrixException {
        Optional<Events> oDbEvents = repo.findById(eventId);
        if(!oDbEvents.isPresent()){
            throw new MetrixException(-1, String.format("EventsApiController::delete()::Event with Id [%d] not found", eventId), "");
        }
        if(ticketsSvc.listTicketsByEventId(eventId).size() > 0){
            throw new MetrixException(-1, String.format("EventsApiController::delete()::Event with Id [%d] already have ticket sell, cannot delete", eventId), "");
        }
        repo.deleteById(eventId);
        /* 2. How can you return Response entity with HTTP 200/201 and the body is type Void ?*/
        return ResponseEntity.ok().build();

    }

    private Venues getVenues(EventsForm events) throws MetrixException{
        int venueId = events.getVenueId();
        Optional<Venues> oVenues = venuesSvc.retrieve(venueId);
        if(!oVenues.isPresent()){
            throw new MetrixException(-1, String.format("EventsApiController::getVenues()::Venue with Id [%d] not found", venueId), "");
        }
        return oVenues.get();
    }
}
