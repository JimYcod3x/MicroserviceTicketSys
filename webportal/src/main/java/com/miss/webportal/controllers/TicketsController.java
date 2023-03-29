package com.miss.webportal.controllers;

import com.miss.services.common.domains.MetrixException;
import com.miss.services.common.domains.Tickets;
import com.miss.services.common.domains.TicketsForm;
import com.miss.webportal.domains.SellForm;
import com.miss.webportal.services.EventsServices;
import com.miss.webportal.services.TicketsServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketsController {
    private static final String VIEW_PREFIX = "tickets/";
    private static final String HARD_CODE_OPERATOR = "Dummy Operator";

    @Autowired
    private TicketsServices ticketSvc;

    @Autowired
    private EventsServices eventSvc;

    @GetMapping("/list/{eventId}")
    public String list(ModelMap m, @PathVariable("eventId") Integer eventId) throws MetrixException{
        List<Tickets> allTickets = ticketSvc.listTicketsByEventId(eventId);
        m.addAttribute("allTickets", allTickets);
        m.addAttribute("ticketDetail", new Tickets());
        return VIEW_PREFIX + "list"; 
    }

    @GetMapping("/create")
    public String create(ModelMap m) throws MetrixException{
        m.addAttribute("newSellForm", new SellForm());
        m.addAttribute("allEvents", eventSvc.index());
        return VIEW_PREFIX + "create";
    }

    @PostMapping("/create")
    @Transactional
    public String create(ModelMap m, @Valid @ModelAttribute("newSellForm") TicketsForm newSellForm, BindingResult result) throws MetrixException{
        if(result.hasErrors()){
            m.addAttribute("newSellForm", newSellForm);
            m.addAttribute("allEvents", eventSvc.index());
            return VIEW_PREFIX + "create";
        }
        newSellForm.setOperator(HARD_CODE_OPERATOR);
        List<Tickets> ticketSold = ticketSvc.create(newSellForm);

        m.addAttribute("ticketSold", ticketSold);
        return VIEW_PREFIX + "ticketSold";
    }

    //Query ticket info
    @GetMapping({"","/","/query"})
    public String query(ModelMap m){
        m.addAttribute("ticketDetail", new Tickets());
        return VIEW_PREFIX + "query";
    }

    @PostMapping("/detail")
    public String detail(ModelMap m, @ModelAttribute("ticketDetail") Tickets queryTicket) throws MetrixException{
        if(queryTicket.getQrcode().trim().isEmpty()){
            throw new MetrixException(-1, "Ticket QR code must be provided!", "/" + VIEW_PREFIX + "query");
        }

        Optional<Tickets> oTicket = ticketSvc.retrieveByQrCode(queryTicket.getQrcode().trim());

        if(!oTicket.isPresent()){
            throw new MetrixException(-2, "Ticket not found!", "/" + VIEW_PREFIX + "query");
        }

        m.addAttribute("ticketDetail", oTicket.get());
        return VIEW_PREFIX + "detail"; 
    }

    @PostMapping("/claim")
    public String claim(ModelMap m, @ModelAttribute("ticketDetail") Tickets claimTicket) throws MetrixException{
        if(claimTicket.getQrcode().trim().isEmpty()){
            throw new MetrixException(-1, "Ticket QR code must be provided!", "/" + VIEW_PREFIX + "query");
        }

        Tickets claimedTicket = ticketSvc.claim(claimTicket.getQrcode());

        //show the ticket info in view /tickets/detail.html
        m.addAttribute("ticketDetail", claimedTicket);
        return VIEW_PREFIX + "detail";     
    }

    @GetMapping("/delete/{qrcode}")
    public String delete(@PathVariable("qrcode") String qrcode) throws MetrixException{
        Optional<Tickets> oTicket = ticketSvc.retrieveByQrCode(qrcode);
        ticketSvc.delete(qrcode);
        return "redirect:/" + VIEW_PREFIX + "list/" + oTicket.get().getEvent().getId();
    }  
}
