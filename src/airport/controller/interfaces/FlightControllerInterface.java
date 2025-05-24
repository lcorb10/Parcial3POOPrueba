package airport.controller.interfaces;

import java.util.List;

import airport.model.Flight;

/**
 * Se define la interfaz para el controlador(Flight_Controller)
 */
public interface FlightControllerInterface {

    void createFlight(Flight flight);

    Flight getFlight(String id);

    List<Flight> getAllFlights();

    void updateFlight(Flight flight);

    void removeFlight(String id);
}
