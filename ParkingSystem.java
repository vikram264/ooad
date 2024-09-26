package com.example.ooad.jobscheduling;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

public  class ParkingSystem {
    public enum ParkingSpotType {
        SMALL, MEDIUM, LARGE, HANDICAPPED;

        /**
         * Explanation of this in switch:
         * this refers to the current instance of the enum VehicleSize. In other words, it refers to the specific VehicleSize enum value on which the fitsIn() method is being called.
         * How this Works in the Switch Statement:
         * When you call vehicleSize.fitsIn(spotSize), this refers to the value of vehicleSize, the object that invokes the method.
         * The switch (this) statement switches based on the value of vehicleSize.
         * @param spotSize
         * @return
         */
        public boolean fitsIn(ParkingSpotType spotSize) {
            switch (this) {
                case SMALL:
                    return spotSize == SMALL || spotSize == MEDIUM || spotSize == LARGE;
                case MEDIUM:
                    return spotSize == MEDIUM || spotSize == LARGE;
                case LARGE:
                    return spotSize == LARGE;
                case HANDICAPPED:
                    return spotSize == HANDICAPPED;
                default:
                    return false;
            }

    }
    @Getter
    @Setter
    static class ParkingLot {
        String name;
        String id;
        String address;
        List<ParkingLevel> parkingLevels;
        List<EntryPanel> entryPanels;
        List<ExitPanel> exitPanels;
        public static ParkingLot INSTANCE = new ParkingLot();
        private ParkingLot() {
            parkingLevels = new ArrayList<>();
            entryPanels = new ArrayList<>();
            exitPanels = new ArrayList<>();
        }

        public void addParkingLevel(ParkingLevel parkingLevel) {
            parkingLevels.add(parkingLevel);
        }

        public ParkingSpot findSpot(Vehicle vehicle) {
            for(ParkingLevel parkingLevel : parkingLevels) {
                ParkingSpot spot = parkingLevel.findAvailableSlot(vehicle);
                if (spot != null) {
                    return spot;
                }
            }
            return null;
        }

        public Ticket parkVehicle(Vehicle vehicle) {
            ParkingSpot spot = findSpot(vehicle);
            if (spot != null) {
                spot.assignVehicle(vehicle);
                return new Ticket(vehicle, spot);
            }
            return null; // Parking lot is full
        }
        public double exitVehicle(Ticket ticket) {
            ParkingSpot spot = ticket.getParkingSpot();
            spot.removeVehicle();
            double fee = new ParkingFeeCalculator().calculateFee(ticket);
            System.out.println("Parking Fee: $" + fee);
            return fee;
        }
    }

   @Getter
   @Setter
    static class ParkingLevel {
        Integer level;
        private List<ParkingSpot> spots;

        public ParkingLevel(Integer level) {
            this.level = level;
            this.spots = new ArrayList<>();
        }
        public ParkingSpot findAvailableSlot(Vehicle vehicle) {
            for(ParkingSpot spot : spots) {
                if(spot.canFitVehicle(vehicle) && spot.getIsAvailable()) {
                    return spot;
                }

            }
            return null;
        }

        public void addSpot(ParkingSpot  parkingSpot) {
            spots.add(parkingSpot);
        }
    }

    @Getter
    @Setter
    static class ParkingSpot {
        String spotId;
        Vehicle vehicle;
        Boolean isAvailable;
        ParkingSpotType parkingSpotType;

        public ParkingSpot(String spotId, ParkingSpotType parkingSpotType) {
            this.spotId = spotId;
            this.parkingSpotType = parkingSpotType;
            this.isAvailable = true;
        }

        public Boolean canFitVehicle(Vehicle v) {
           // return this.isAvailable && v.getParkingSpotType().ordinal() <= parkingSpotType.ordinal();
             return this.isAvailable && v.getParkingSpotType().fitsIn(parkingSpotType);
        }
        public void assignVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            this.isAvailable = false;
        }
        public void removeVehicle() {
            this.vehicle = null;
            this.isAvailable = true;
        }

        public boolean isAvailable() {
            return isAvailable;
        }
    }
    @Getter
    abstract static class Vehicle {
        String licensePlate;
        ParkingSpotType parkingSpotType;

        public Vehicle(String licensePlate, ParkingSpotType parkingSpotType) {
            this.licensePlate = licensePlate;
            this.parkingSpotType = parkingSpotType;
        }
    }
    @Getter
    static class Ticket {
        String ticketId;
        Vehicle vehicle;
        ParkingSpot parkingSpot;
        LocalDateTime issuedAt;

        public Ticket(Vehicle vehicle, ParkingSpot parkingSpot) {
            this.ticketId = UUID.randomUUID().toString();
            this.vehicle = vehicle;
            this.parkingSpot = parkingSpot;
            this.issuedAt = LocalDateTime.now();
        }
    }

    static class ParkingFeeCalculator {
        public double calculateFee(Ticket ticket) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime issueTime = ticket.getIssuedAt();

            double hourly = new HourlyCost().getHourlyCost(ticket.getParkingSpot().getParkingSpotType());
            return hourly * 2;
        }
    }

    static class HourlyCost {
        private Map<ParkingSpotType, Double> hourlyCosts = new HashMap<>();
        public  HourlyCost() {
            hourlyCosts.put(ParkingSpotType.SMALL, 20.0);
            hourlyCosts.put(ParkingSpotType.LARGE, 30.0);
            hourlyCosts.put(ParkingSpotType.MEDIUM, 10.0);
            hourlyCosts.put(HANDICAPPED, 25.0);

        }

        public  double getHourlyCost(ParkingSpotType parkingSpotType) {
            return hourlyCosts.get(parkingSpotType);
        }
    }
    static class EntryPanel {
        String id;

        public EntryPanel(String id) {
            this.id = id;
        }

        public Ticket issueTicket(Vehicle vehicle) {
            //return ParkingLot.INSTANCE.parkVehicle(vehicle);
            return ParkingLot.INSTANCE.parkVehicle(vehicle);
        }
    }
        static class ExitPanel {
            String id;


            public ExitPanel(String id) {
                this.id = id;
            }

            public Double calculateFee(Ticket ticket) {
                return ParkingLot.INSTANCE.exitVehicle(ticket);
                //return parkingLot.exitVehicle(ticket);
            }
        }


}


}
