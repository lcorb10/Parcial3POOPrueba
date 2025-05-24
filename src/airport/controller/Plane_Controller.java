package airport.controller;

import airport.model.Plane;
import airport.service.PlaneService;

import java.util.List;

public class Plane_Controller {

    private final PlaneService planeService;

    /** Se inyecta el servicio de FlightService(Inyección de dependencias) **/
    public Plane_Controller(PlaneService planeService) {
        this.planeService = planeService;
    }

    /** Permite delegar a la capa de servicios, para crear un avión **/
    public void createPlane(String id, String brand, String model, int maxCapacity, String airline) {

        this.planeService.createPlane(id, brand, model, maxCapacity, airline);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener un avión por medio del
     * ID
     **/
    public Plane getPlane(String id) {

        return this.planeService.getPlane(id);
    }

    /**
     * Permite delegar a la capa de servicios, para obtener todos los aviones
     * registrados
     **/
    public List<Plane> getAllPlanes() {

        return this.planeService.getAllPlanes();
    }

    /**
     * Permite delegar a la capa de servicios, para actualizar la información un
     * avión
     **/
    public void updatePlane(Plane updated) {

        this.planeService.updatePlane(updated);
    }

    /**
     * Permite delegar a la capa de servicios, para eliminar un avión
     **/
    public void removePlane(String id) {

        this.planeService.removePlane(id);
    }
}