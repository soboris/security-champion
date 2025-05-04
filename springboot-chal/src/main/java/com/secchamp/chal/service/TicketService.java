package com.secchamp.chal.service;

import com.secchamp.chal.model.Ticket;
import com.secchamp.chal.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Retrieve all tickets
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    // Find ticket by ID (updated to handle Integer)
    public Ticket findById(Integer ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    // Save or update a ticket
    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    // Search tickets by passenger name, flight number, or departure/arrival airports
    public List<Ticket> searchTickets(String keyword) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
            Criteria.where("passengerName").regex(keyword, "i"),
            Criteria.where("flightNumber").regex(keyword, "i"),
            Criteria.where("departureAirport").regex(keyword, "i"),
            Criteria.where("arrivalAirport").regex(keyword, "i")
        ));
        return mongoTemplate.find(query, Ticket.class);
    }
}
