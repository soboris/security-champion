package com.secchamp.chal.init;

import com.secchamp.chal.model.Ticket;
import com.secchamp.chal.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MongoDBInitializer {

    @Autowired
    private TicketRepository ticketRepository;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void initTickets() {
        System.out.println("Checking if tickets exist...");

        // Check if tickets already exist
        if (ticketRepository.findAll().isEmpty()) {
            System.out.println("No tickets found, creating sample tickets...");
            createSampleTickets();
        } else {
            System.out.println("Tickets already exist. Skipping initialization.");
        }
    }

    private void createSampleTickets() {
        ticketRepository.save(createTicket(1, "John Doe", "john.doe@example.com", "AA101", "JFK", "LAX",
                1, 5, "12A", "Economy", 250.00, false));

        ticketRepository.save(createTicket(2, "Jane Smith", "jane.smith@example.com", "BA202", "LHR", "SFO",
                2, 11, "5B", "Business", 1200.00, false));

        ticketRepository.save(createTicket(3, "Robert Brown", "robert.brown@example.com", "DL303", "ATL", "ORD",
                3, 2, "9C", "First Class", 550.00, true));

        ticketRepository.save(createTicket(4, "Emily Clark", "emily.clark@example.com", "UA404", "SFO", "SEA",
                4, 2, "3D", "Economy", 200.00, false));

        ticketRepository.save(createTicket(5, "David White", "david.white@example.com", "AF505", "CDG", "JFK",
                5, 8, "7A", "Economy", 600.00, false));

        ticketRepository.save(createTicket(6, "Laura Green", "laura.green@example.com", "LH606", "FRA", "BOM",
                6, 9, "14E", "Business", 900.00, true));

        System.out.println("Sample tickets created.");
    }

    private Ticket createTicket(Integer ticketId, String passengerName, String email, String flightNumber, 
                                String departureAirport, String arrivalAirport, int daysToAdd, int flightHours,
                                String seatNumber, String ticketClass, double price, boolean isCheckedIn) {

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketId);  // Set the integer ticketId
        ticket.setPassengerName(passengerName);
        ticket.setPassengerEmail(email);
        ticket.setFlightNumber(flightNumber);
        ticket.setDepartureAirport(departureAirport);
        ticket.setArrivalAirport(arrivalAirport);
        ticket.setDepartureTime(LocalDateTime.now().plusDays(daysToAdd));
        ticket.setArrivalTime(LocalDateTime.now().plusDays(daysToAdd).plusHours(flightHours));
        ticket.setSeatNumber(seatNumber);
        ticket.setTicketClass(ticketClass);
        ticket.setPrice(price);
        ticket.setCheckedIn(isCheckedIn);
        ticket.setBookingDate(LocalDateTime.now());

        return ticket;
    }
}
