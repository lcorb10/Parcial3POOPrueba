package airport.controller;

import airport.model.Flight;
import airport.service.FlightService;

import java.util.List;

public class Flight_Controller {
    private final FlightService flightService;

    /** Se inyecta el servicio de FlightService(Inyección de dependencias) **/
    public Flight_Controller(FlightService flightService) {

        this.flightService = flightService;
    }

    /** Permite delegar a la capa de servicios, para crear un vuelo **/
    public void createFlight(Flight flight) {

        this.flightService.createFlight(flight);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener una vuelo por medio del
     * ID
     **/
    public Flight getFlight(String id) {

        return this.flightService.getFlight(id);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener todos los vuelos
     * registrados
     **/
    public List<Flight> getAllFlights() {

        return this.flightService.getAllFlights();
    }

    /**
     * Permite delegar a la capa de servicios, para actualizar la información un
     * vuelo
     **/
    public void updateFlight(Flight updated) {

        this.flightService.updateFlight(updated);
    }

    /** Permite delegar a la capa de servicios, para eliminar un vuelo **/
    public void removeFlight(String id) {

        this.flightService.removeFlight(id);
    }
}