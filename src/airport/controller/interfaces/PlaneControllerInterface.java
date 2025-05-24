package airport.controller.interfaces;

import java.util.List;

import airport.model.Plane;

/**
 * Se define la interfaz para el controlador(Plane_Controller)
 */
public interface PlaneControllerInterface {

    void createPlane(String id, String brand, String model, int maxCapacity, String airline);

    Plane getPlane(String id);

    List<Plane> getAllPlanes();

    void updatePlane(Plane plane);

    void removePlane(String id);

}
