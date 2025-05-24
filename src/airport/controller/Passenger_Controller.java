package airport.controller;

import airport.model.Passenger;
import airport.service.PassengerService;

import java.util.List;

public class Passenger_Controller {

    private final PassengerService passengerService;

    /** Se inyecta el servicio de PassengerService(Inyección de dependencias) **/
    public Passenger_Controller(PassengerService passengerService) {

        this.passengerService = passengerService;
    }

    /** Permite delegar a la capa de servicios, para crear un nuevo pasajero **/
    public void createPassenger(Passenger passenger) {

        this.passengerService.createPassenger(passenger);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener un pasajero por medio
     * del
     * ID
     **/
    public Passenger getPassenger(long id) {

        return this.passengerService.getPassenger(id);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener todos los pasajeros
     * registrados
     **/
    public List<Passenger> getAllPassengers() {
        System.out.println("Controlador => getAllPassengers");
        return this.passengerService.getAllPassengers();
    }

    /**
     * Permite delegar a la capa de servicios, para actualizar la información un
     * pasajero
     **/
    public void updatePassenger(Passenger passenger) {

        this.passengerService.updatePassenger(passenger);
    }

    /**
     * Permite delegar a la capa de servicios, para eliminar aún pasajero
     **/
    public void removePassenger(long id) {

        this.passengerService.removePassenger(id);
    }
}