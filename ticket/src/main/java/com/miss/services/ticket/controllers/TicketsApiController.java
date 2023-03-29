package com.miss.services.ticket.controllers;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import com.miss.services.common.apis.TicketApi;
import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Tickets;
import com.miss.services.common.domains.Events;
import com.miss.services.common.domains.TicketsForm;
import com.miss.services.ticket.repositories.TicketsRepo;
import com.miss.services.ticket.services.EventsServices;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RestController;

/* 1. What should you add here to make it become a RESTful Controller */
@RestController
@RequestMapping(value = "/api/tickets")
public class TicketsApiController implements TicketApi{

    @Autowired
    private TicketsRepo repo;

    @Autowired
    private EventsServices eventsSvc;

    @Override
    public List<Tickets> index() throws MetrixException {
        return repo.findAll();
    }

    @Override
    public List<Tickets> listTicketsByEventId(Integer eventId) throws MetrixException {
        return repo.getByEventsId(eventId);
    }

    @Override
    public Tickets claim(String qrcode) throws MetrixException {
        Optional<Tickets> oTickets = repo.getByQrCode(qrcode);
        if(!oTickets.isPresent()){
            throw new MetrixException(-1, String.format("TicketsApiController::claim()::Ticket with QR Code [%s] not found", qrcode), "");
        }
        Tickets ticket = oTickets.get();
        ticket.setClaimDtm(new Date());
        ticket.setClaimed(true);
        return repo.save(ticket);
    }

    @Override
    @Transactional
    public List<Tickets> create(TicketsForm newTicketForm) throws MetrixException { 
        validate(newTicketForm);//will throw errors if not valid
        Events events = getEvents(newTicketForm);
        if(!events.isStartSell()){
            throw new MetrixException(-1, String.format("TicketsApiController::create()::Event with Id [%d] not yet start", events.getId()), "");
        }
        List<Tickets> rtn = new ArrayList<>();
        for(int i=0; i<newTicketForm.getNumberOfTicket(); i++){
            Tickets newTicket = new Tickets(0, newTicketForm.getOperator(), generateQRcode(), false, new Date(), null, events);
            repo.save(newTicket);
            rtn.add(newTicket);
        }
        return rtn;
    }

    @Override
    public Optional<Tickets> retrieve(Integer ticketId) throws MetrixException {
        return repo.findById(ticketId);
    }

    @Override
    public Optional<Tickets> retrieveByQrCode(String qrCode) throws MetrixException {
        return repo.getByQrCode(qrCode);
    }    

    @Override
    @Transactional
    public Tickets update(Integer ticketId, Tickets updatedTicket) throws MetrixException {
        Optional<Tickets> oDbTickets = repo.findById(ticketId);
        if(oDbTickets.isEmpty()){
            throw new MetrixException(-1, String.format("TicketsApiController::update()::Ticket with Id [%d] not found", ticketId), "");
        }
        Tickets dbTickets = oDbTickets.get();
        if(dbTickets.isClaimed()){
            throw new MetrixException(-1, String.format("TicketsApiController::update()::Ticket with Id [%d] already claimed and cannot be change", ticketId), "");
        }
        dbTickets.setOperator(updatedTicket.getOperator());
        dbTickets.setQrcode(generateQRcode()); //Void the old ticket and force reprint
        return repo.save(dbTickets);
    }

    @Override
    public ResponseEntity<Void> delete(String qrCode) throws MetrixException {
        Optional<Tickets> oDbTickets = repo.getByQrCode(qrCode);
        if(!oDbTickets.isPresent()){
            throw new MetrixException(-1, String.format("TicketsApiController::delete()::Ticket with Qr Code [%s] not found", qrCode), "");
        }

        Tickets ticket = oDbTickets.get();
        if(ticket.isClaimed()){
            throw new MetrixException(-1, String.format("TicketsApiController::delete()::Ticket with Qr Code [%s] already claimed", qrCode), "");
        }
        repo.deleteById(ticket.getId());
        /* 2. How can you return Response entity with HTTP 200/201 and the body is type Void ?*/
        return ResponseEntity.ok().build();
    }

    private Events getEvents(TicketsForm ticketsForm) throws MetrixException{
        int eventId = ticketsForm.getEventId();
        Optional<Events> oEvents = eventsSvc.retrieve(eventId);
        if(!oEvents.isPresent()){
            throw new MetrixException(-1, String.format("TicketsApiController::getEvents()::Event with Id [%d] not found", eventId), "");
        }
        return oEvents.get();
    }

    private String generateQRcode(){
        //generate qr code but do while loop, you can assume it will never endless. Safe guard by only have max 100 seat
        boolean isDuplicated = true;
        String qrCode = "";
        do{
            qrCode = UUID.randomUUID().toString();
            if(!repo.getByQrCode(qrCode).isPresent()){
                isDuplicated = false;
            }
        }while(isDuplicated);
        return qrCode;
    }
    
    private void validate(TicketsForm form) {
        try {
            Optional<Events> oEvent = eventsSvc.retrieve(form.getEventId());

            //2. Check whether the event is present
            if(!oEvent.isPresent()){
                throw new MetrixException(-1, String.format("TicketsApiController::validate()::Event with Id [%d] not found", form.getEventId()), "");
            } else {
                //3. Check whether the ticket is active and the corresponding event is start sell
                Events event = oEvent.get();
                if(!event.isStartSell()){
                    throw new MetrixException(-1, String.format("TicketsApiController::validate()::Event with Id [%d] not started yet", form.getEventId()), "");
                }

                //4. Check whether the Venue of the event is full
                //4a. Get the number of seat
                int numOfSeat = event.getVenue().getNumberOfSeat();
                //4b. Get the total number of ticket for that event;
                int ticketSold = repo.getByEventsId(event.getId()).size();
                //4c. Get current ticket needed
                int ticketNeeded = form.getNumberOfTicket();
                if(ticketSold + ticketNeeded > numOfSeat){
                    throw new MetrixException(-1, String.format("TicketsApiController::validate()::Event with Id [%d] not started yet", form.getEventId()), "");
                }           
            }
        } catch (MetrixException e) {
            e.printStackTrace();
        }
    }
}
