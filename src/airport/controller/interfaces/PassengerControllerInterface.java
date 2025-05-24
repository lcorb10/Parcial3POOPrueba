package airport.controller.interfaces;

import java.util.List;

import airport.model.Passenger;

/**
 * Se define la interfaz para el controlador(Passenger_Controller)
 */
public interface PassengerControllerInterface {

    void createPassenger(Passenger passenger);

    Passenger getPassenger(long id);

    List<Passenger> getAllPassengers();

    void updatePassenger(Passenger passenger);

    void removePassenger(long id);
}
