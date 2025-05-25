package airport.controller;

import airport.controller.interfaces.FlightControllerInterface;
import airport.controller.interfaces.LocationControllerInterface;
import airport.controller.interfaces.PassengerControllerInterface;
import airport.controller.interfaces.PlaneControllerInterface;
import airport.model.*;
import airport.service.validator.impl.FlightValidator;
import airport.service.validator.impl.LocationValidator;
import airport.service.validator.impl.PassengerValidator;
import airport.service.validator.impl.PlaneValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DateTimeException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AirportController {

    private final PlaneControllerInterface planeController;
    private final FlightControllerInterface flightController;
    private final LocationControllerInterface locationController;
    private final PassengerControllerInterface passengerController;

    private final PassengerValidator passengerValidator = new PassengerValidator();
    private final LocationValidator locationValidator = new LocationValidator();
    private final PlaneValidator planeValidator = new PlaneValidator();
    private final FlightValidator flightValidator = new FlightValidator();

    public AirportController(
            PlaneControllerInterface planeController,
            FlightControllerInterface flightController,
            LocationControllerInterface locationController,
            PassengerControllerInterface passengerController) {
        this.planeController = planeController;
        this.flightController = flightController;
        this.locationController = locationController;
        this.passengerController = passengerController;
    }

    // --------- CREAR ENTIDADES ----------
    public Response<Passenger> registerPassenger(long id, String firstname, String lastname, int year, int month, int day,
                                                int phoneCode, long phone, String country) {
        try {
            if (passengerController.getPassenger(id) != null) {
                throw new IllegalArgumentException("El ID del pasajero ya existe.");
            }
            LocalDate birthDate = buildDate(year, month, day, "Fecha de nacimiento inválida.");
            Passenger tempPassenger = new Passenger(id, firstname, lastname, birthDate, phoneCode, phone, country);
            passengerValidator.validate(tempPassenger);
            Passenger created = passengerController.createPassenger(tempPassenger);
            return new Response<>(201, "Pasajero registrado con éxito.", created.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Plane> createPlane(String id, String brand, String model, int maxCapacity, String airline) {
        try {
            if (planeController.getPlane(id) != null) {
                throw new IllegalArgumentException("El ID del avión ya existe.");
            }
            Plane tempPlane = new Plane(id, brand, model, maxCapacity, airline);
            planeValidator.validate(tempPlane);
            Plane created = planeController.createPlane(id, brand, model, maxCapacity, airline);
            return new Response<>(201, "Avión creado con éxito.", created.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Location> createLocation(String id, String name, String city, String country, double latitude, double longitude) {
        try {
            if (locationController.getLocation(id) != null) {
                throw new IllegalArgumentException("El ID del aeropuerto ya existe.");
            }
            Location tempLocation = new Location(id, name, city, country, latitude, longitude);
            locationValidator.validate(tempLocation);
            Location created = locationController.createLocation(id, name, city, country, latitude, longitude);
            return new Response<>(201, "Ubicación creada con éxito.", created.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Flight> createFlight(
            String id,
            String planeId,
            String departureLocationId,
            String arrivalLocationId,
            String scaleLocationId,
            int year,
            int month,
            int day,
            int hour,
            int minutes,
            int hoursDurationsArrival,
            int minutesDurationsArrival,
            int hoursDurationsScale,
            int minutesDurationsScale) {
        try {
            if (flightController.getFlight(id) != null) {
                throw new IllegalArgumentException("El ID del vuelo ya existe.");
            }
            Plane plane = getPlaneOrFail(planeId);
            Location departure = getLocationOrFail(departureLocationId, "Ubicación de salida inexistente.");
            Location arrival = getLocationOrFail(arrivalLocationId, "Ubicación de llegada inexistente.");
            Location scale = (scaleLocationId != null && !scaleLocationId.isEmpty() && !scaleLocationId.equals("-"))
                    ? getLocationOrFail(scaleLocationId, "Ubicación de escala inexistente.")
                    : null;

            LocalDateTime departureDate = buildDateTime(year, month, day, hour, minutes, "Fecha y hora de salida inválida.");

            Flight tempFlight;
            if (scale == null) {
                tempFlight = new Flight(id, plane, departure, arrival, departureDate, hoursDurationsArrival, minutesDurationsArrival);
            } else {
                tempFlight = new Flight(id, plane, departure, scale, arrival, departureDate,
                        hoursDurationsArrival, minutesDurationsArrival,
                        hoursDurationsScale, minutesDurationsScale);
            }
            flightValidator.validate(tempFlight);
            Flight created = flightController.createFlight(tempFlight);
            return new Response<>(201, "Vuelo creado con éxito.", created.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    // --------- OBTENER LISTAS PARA TABLAS (ordenadas, copias) ----------
    public Response<List<Passenger>> getAllPassengers() {
        try {
            List<Passenger> sorted = passengerController.getAllPassengers().stream()
                    .sorted(Comparator.comparingLong(Passenger::getId))
                    .map(Passenger::clone)
                    .collect(Collectors.toList());
            return new Response<>(200, "Pasajeros obtenidos con éxito.", sorted);
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<List<Plane>> getAllPlanes() {
        try {
            List<Plane> sorted = planeController.getAllPlanes().stream()
                    .sorted(Comparator.comparing(Plane::getId))
                    .map(Plane::clone)
                    .collect(Collectors.toList());
            return new Response<>(200, "Aviones obtenidos con éxito.", sorted);
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<List<Location>> getAllLocations() {
        try {
            List<Location> sorted = locationController.getAllLocations().stream()
                    .sorted(Comparator.comparing(Location::getAirportId))
                    .map(Location::clone)
                    .collect(Collectors.toList());
            return new Response<>(200, "Ubicaciones obtenidas con éxito.", sorted);
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<List<Flight>> getAllFlights() {
        try {
            List<Flight> sorted = flightController.getAllFlights().stream()
                    .sorted(Comparator.comparing(Flight::getDepartureDate))
                    .map(Flight::clone)
                    .collect(Collectors.toList());
            return new Response<>(200, "Vuelos obtenidos con éxito.", sorted);
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    // --------- MÉTODOS EXTRA DE USO FRECUENTE ----------
    public Response<Passenger> updatePassenger(long id, String firstname, String lastname, int year, int month, int day, int phoneCode,
                                               long phone, String country) {
        try {
            Passenger passenger = passengerController.getPassenger(id);
            if (passenger == null) throw new IllegalArgumentException("No existe el pasajero a actualizar.");

            LocalDate birthDate = buildDate(year, month, day, "Fecha de nacimiento inválida.");
            Passenger tempPassenger = new Passenger(id, firstname, lastname, birthDate, phoneCode, phone, country);
            passengerValidator.validate(tempPassenger);

            // Solo se actualiza si la validación pasa
            passenger.setFirstname(firstname);
            passenger.setLastname(lastname);
            passenger.setBirthDate(birthDate);
            passenger.setCountryPhoneCode(phoneCode);
            passenger.setPhone(phone);
            passenger.setCountry(country);

            Passenger updated = passengerController.updatePassenger(passenger);
            return new Response<>(200, "Pasajero actualizado con éxito.", updated.clone());
        } catch (Exception e) {
            // Nada se actualiza si hay error
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Plane> updatePlane(String id, String brand, String model, int maxCapacity, String airline) {
        try {
            Plane plane = planeController.getPlane(id);
            if (plane == null) throw new IllegalArgumentException("No existe el avión a actualizar.");
            Plane tempPlane = new Plane(id, brand, model, maxCapacity, airline);
            planeValidator.validate(tempPlane);

            plane.setBrand(brand);
            plane.setModel(model);
            plane.setAirline(airline);

            Plane updated = planeController.updatePlane(plane);
            return new Response<>(200, "Avión actualizado con éxito.", updated.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Location> updateLocation(String id, String name, String city, String country, double latitude, double longitude) {
        try {
            Location location = locationController.getLocation(id);
            if (location == null) throw new IllegalArgumentException("No existe la ubicación a actualizar.");
            Location tempLocation = new Location(id, name, city, country, latitude, longitude);
            locationValidator.validate(tempLocation);

            location.setAirportName(name);
            location.setAirportCity(city);
            location.setAirportCountry(country);
            location.setAirportLatitude(latitude);
            location.setAirportLongitude(longitude);

            Location updated = locationController.updateLocation(location);
            return new Response<>(200, "Ubicación actualizada con éxito.", updated.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Flight> updateFlight(
            String id,
            String planeId,
            String departureLocationId,
            String arrivalLocationId,
            String scaleLocationId,
            int year,
            int month,
            int day,
            int hour,
            int minutes,
            int hoursDurationsArrival,
            int minutesDurationsArrival,
            int hoursDurationsScale,
            int minutesDurationsScale) {
        try {
            Flight flight = flightController.getFlight(id);
            if (flight == null) throw new IllegalArgumentException("No existe el vuelo a actualizar.");

            Plane plane = getPlaneOrFail(planeId);
            Location departure = getLocationOrFail(departureLocationId, "Ubicación de salida inexistente.");
            Location arrival = getLocationOrFail(arrivalLocationId, "Ubicación de llegada inexistente.");
            Location scale = (scaleLocationId != null && !scaleLocationId.isEmpty() && !scaleLocationId.equals("-"))
                    ? getLocationOrFail(scaleLocationId, "Ubicación de escala inexistente.")
                    : null;
            LocalDateTime departureDate = buildDateTime(year, month, day, hour, minutes, "Fecha y hora de salida inválida.");

            Flight tempFlight;
            if (scale == null) {
                tempFlight = new Flight(id, plane, departure, arrival, departureDate, hoursDurationsArrival, minutesDurationsArrival);
            } else {
                tempFlight = new Flight(id, plane, departure, scale, arrival, departureDate,
                        hoursDurationsArrival, minutesDurationsArrival,
                        hoursDurationsScale, minutesDurationsScale);
            }
            flightValidator.validate(tempFlight);

            // Si es válido, actualiza el objeto real:
            flight.setPlane(plane);
            flight.setDepartureLocation(departure);
            flight.setArrivalLocation(arrival);
            flight.setDepartureDate(departureDate);
            flight.setHoursDurationArrival(hoursDurationsArrival);
            flight.setMinutesDurationArrival(minutesDurationsArrival);
            if (scale == null) {
                flight.setScaleLocation(null);
                flight.setHoursDurationScale(0);
                flight.setMinutesDurationScale(0);
            } else {
                flight.setScaleLocation(scale);
                flight.setHoursDurationScale(hoursDurationsScale);
                flight.setMinutesDurationScale(minutesDurationsScale);
            }

            Flight updated = flightController.updateFlight(flight);
            return new Response<>(200, "Vuelo actualizado con éxito.", updated.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<List<Flight>> getFlightsByPassenger(long passengerId) {
        try {
            Passenger passenger = passengerController.getPassenger(passengerId);
            if (passenger == null) throw new IllegalArgumentException("Pasajero no encontrado.");
            List<Flight> sorted = passenger.getFlights().stream()
                    .sorted(Comparator.comparing(Flight::getDepartureDate))
                    .map(Flight::clone)
                    .collect(Collectors.toList());
            return new Response<>(200, "Vuelos del pasajero obtenidos con éxito.", sorted);
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Flight> delayFlight(String flightId, int hours, int minutes) {
        try {
            if (hours < 0 && minutes < 0)
                throw new IllegalArgumentException("El tiempo de retraso debe ser mayor que 00:00.");
            Flight flight = flightController.getFlight(flightId);
            if (flight == null) throw new IllegalArgumentException("Vuelo no encontrado.");

            // Validar el delay sobre una copia
            Flight tempFlight = flight.clone();
            tempFlight.delay(hours, minutes);
            flightValidator.validate(tempFlight);

            // Si es válido, aplica el cambio real
            flight.delay(hours, minutes);
            Flight updated = flightController.updateFlight(flight);
            return new Response<>(200, "Vuelo retrasado correctamente.", updated.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    public Response<Flight> addPassengerToFlight(long passengerId, String flightId) {
        try {
            Passenger passenger = passengerController.getPassenger(passengerId);
            if (passenger == null) throw new IllegalArgumentException("Pasajero no encontrado.");
            Flight flight = flightController.getFlight(flightId);
            if (flight == null) throw new IllegalArgumentException("Vuelo no encontrado.");

            if (flight.getPassengers().contains(passenger)) {
                throw new IllegalArgumentException("El pasajero ya está en el vuelo.");
            }

            // Simula el cambio para validar
            Flight tempFlight = flight.clone();
            Passenger tempPassenger = passenger.clone();
            tempFlight.addPassenger(tempPassenger);
            flightValidator.validate(tempFlight);

            // Si es válido, aplica el cambio real
            flight.addPassenger(passenger);
            passenger.addFlight(flight);

            Flight updatedFlight = flightController.updateFlight(flight);
            passengerController.updatePassenger(passenger);

            return new Response<>(200, "Pasajero agregado al vuelo correctamente.", updatedFlight.clone());
        } catch (Exception e) {
            return new Response<>(400, e.getMessage(), null);
        }
    }

    // Facade para obtener por id (devuelve copia)
    public Response<Passenger> getPassenger(long id) {
        try {
            Passenger p = passengerController.getPassenger(id);
            if (p == null) throw new IllegalArgumentException("Pasajero no encontrado.");
            return new Response<>(200, "Pasajero obtenido con éxito.", p.clone());
        } catch (Exception e) {
            return new Response<>(404, e.getMessage(), null);
        }
    }

    public Response<Plane> getPlane(String id) {
        try {
            Plane p = planeController.getPlane(id);
            if (p == null) throw new IllegalArgumentException("Avión no encontrado.");
            return new Response<>(200, "Avión obtenido con éxito.", p.clone());
        } catch (Exception e) {
            return new Response<>(404, e.getMessage(), null);
        }
    }

    public Response<Location> getLocation(String id) {
        try {
            Location l = locationController.getLocation(id);
            if (l == null) throw new IllegalArgumentException("Ubicación no encontrada.");
            return new Response<>(200, "Ubicación obtenida con éxito.", l.clone());
        } catch (Exception e) {
            return new Response<>(404, e.getMessage(), null);
        }
    }

    public Response<Flight> getFlight(String id) {
        try {
            Flight f = flightController.getFlight(id);
            if (f == null) throw new IllegalArgumentException("Vuelo no encontrado.");
            return new Response<>(200, "Vuelo obtenido con éxito.", f.clone());
        } catch (Exception e) {
            return new Response<>(404, e.getMessage(), null);
        }
    }

    // ------------------ Métodos auxiliares ------------------
    private LocalDate buildDate(int year, int month, int day, String errorMessage) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private LocalDateTime buildDateTime(int year, int month, int day, int hour, int minute, String errorMessage) {
        try {
            return LocalDateTime.of(year, month, day, hour, minute);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private Plane getPlaneOrFail(String id) {
        Plane plane = planeController.getPlane(id);
        if (plane == null) throw new IllegalArgumentException("Avión inexistente.");
        return plane;
    }

    private Location getLocationOrFail(String id, String errorMsg) {
        Location loc = locationController.getLocation(id);
        if (loc == null) throw new IllegalArgumentException(errorMsg);
        return loc;
    }
}