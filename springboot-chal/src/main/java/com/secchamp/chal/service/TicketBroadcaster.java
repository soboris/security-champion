package com.secchamp.chal.service;

import com.secchamp.chal.model.Ticket;
import com.secchamp.chal.repository.TicketRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class TicketBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final TicketRepository ticketRepository;
    private final Random random = new Random();

    public TicketBroadcaster(SimpMessagingTemplate messagingTemplate, TicketRepository ticketRepository) {
        this.messagingTemplate = messagingTemplate;
        this.ticketRepository = ticketRepository;
    }

    @Scheduled(fixedRate = 15000)  // Broadcast every 15 seconds
    public void broadcastRandomTicket() {
        List<Ticket> tickets = ticketRepository.findAll();  // Fetch all tickets from MongoDB

        if (!tickets.isEmpty()) {
            // Select a random ticket from MongoDB
            Ticket randomTicket = tickets.get(random.nextInt(tickets.size()));
            String ticketInfo = formatTicketInfo(randomTicket);

            messagingTemplate.convertAndSend("/topic/tickets", ticketInfo);  // Send message to WebSocket topic
        }
    }

    private String formatTicketInfo(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format("Ticket Info at %s: Passenger Name: %s, Flight Number: %s, Departure: %s, Arrival: %s",
                now.format(formatter),
                ticket.getPassengerName(),
                ticket.getFlightNumber(),
                ticket.getDepartureAirport(),
                ticket.getArrivalAirport());
    }
}
