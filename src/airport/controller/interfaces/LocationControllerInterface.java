package airport.controller.interfaces;

import java.util.List;

import airport.model.Location;

/**
 * Se define la interfaz para el controlador(Location_Controller)
 */
public interface LocationControllerInterface {

    void createLocation(String id, String name, String city, String country, double latitude, double longitude);

    Location getLocation(String id);

    List<Location> getAllLocations();

    void updateLocation(Location location);

    void removeLocation(String id);
}
