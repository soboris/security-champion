package com.secchamp.chal.repository;

import com.secchamp.chal.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List; 

@Repository
public interface TicketRepository extends MongoRepository<Ticket, Integer> {
    
    // You can add custom query methods here if needed, e.g.:
    
    // Find tickets by passenger name
    List<Ticket> findByPassengerName(String passengerName);

    // Find tickets by flight number
    List<Ticket> findByFlightNumber(String flightNumber);

    // Find tickets by departure airport
    List<Ticket> findByDepartureAirport(String departureAirport);

    // Find tickets by arrival airport
    List<Ticket> findByArrivalAirport(String arrivalAirport);

}
