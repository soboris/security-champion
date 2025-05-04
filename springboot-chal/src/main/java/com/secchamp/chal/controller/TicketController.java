package com.secchamp.chal.controller;

import com.secchamp.chal.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class TicketController {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger LOGGER = Logger.getLogger(TicketController.class.getName());

    @GetMapping("/pages/protected/tickets/list")
    public String listTopThreeTickets(Model model) {
        List<Ticket> tickets = mongoTemplate.find(new BasicQuery("{}").limit(3), Ticket.class, "tickets");
        model.addAttribute("tickets", tickets);
        return "protected/listtickets";
    }

    @GetMapping("/pages/protected/tickets/details")
    public String getTicketDetails(@RequestParam("id") String id, Model model) {
        LOGGER.info("Received ticket details request for id: " + id);

        String query = "{ 'ticketId': " + id + " }";  
        LOGGER.info("MongoDB query: " + query);  // Log the query

        BasicQuery basicQuery = new BasicQuery(query);
        List<Ticket> tickets = mongoTemplate.find(basicQuery, Ticket.class, "tickets");

        LOGGER.info("Number of tickets found: " + tickets.size());  // Log the number of tickets found

        model.addAttribute("tickets", tickets);
        return "protected/ticketdetails";
    }
}
