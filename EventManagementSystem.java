package ooad;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class EventManagementSystem {
    @Getter
    @Setter
    abstract static class Event {
        String name;
        String description;
        Date date;
        Venue venue;
        List<ShowTime> showTimes;

        public Event(String name, String description, Date date, Venue venue) {
            this.name = name;
            this.description = description;
            this.date = date;
            this.venue = venue;
        }
        public abstract List<ShowTime> getShowtimes();
    }

    static class Concert extends Event {
        private String artist;

        public Concert(String name, String description, Date date, Venue venue, String artist) {
            super(name, description, date, venue);
            this.artist = artist;
        }

        @Override
        public List<ShowTime> getShowtimes() {
            return this.getVenue().getShowtimes();
        }
    }
    @Getter
    @Setter
    static class Theater extends Event {

        String director;

        public Theater(String name, String description, Date date, Venue venue, String director) {
            super(name, description, date, venue);
            this.director = director;
        }

        @Override
        public List<ShowTime> getShowtimes() {
            return null;
        }
    }
    // Match etc..
    @Getter
    static class Venue{
        String name;
        String address;
        List<ShowTime> showtimes;

        public Venue(String name, String address) {
            this.name = name;
            this.address = address;
            showtimes = new ArrayList<>();
        }
        public void addShowTime(ShowTime showTime) {
            this.showtimes.add(showTime);
        }

    }

     static class ShowTime {
        Date date;
        Venue venue;
        Event event;
        List<Seat> availableSeats;

        public ShowTime(Date date, Venue venue, Event event) {
            this.date = date;
            this.venue = venue;
            this.event = event;
        }


        public void addSeat(Seat seat) {
            this.availableSeats.add(seat);
        }
        public boolean isSeatAvailable(int seatNumber) {
            return availableSeats.stream()
                    .filter(seat -> seat.getSeatNumber() == seatNumber && !seat.isBooked())
                    .findFirst()
                    .isPresent();
        }

        public void bookSeat(int seatNumber) {
            availableSeats.stream()
                    .filter(seat -> seat.getSeatNumber() == seatNumber && !seat.isBooked())
                    .forEach(Seat::book);
        }

        public Event getEvent() {
            return event;
        }
    }
    @Getter
    static class Seat {
        int seatNumber;
        boolean isBooked;

        public Seat(int seatNumber) {
            this.seatNumber = seatNumber;
            this.isBooked  = false;
        }
        public void book() {
            this.isBooked = true;
        }
    }
    @Getter
   static  class Booking {
        User user;
        ShowTime showTime;
        int seatNumber;

        public Booking(User user, ShowTime showTime, int seatNumber) {
            this.user = user;
            this.showTime = showTime;
            this.seatNumber = seatNumber;
        }
    }
    @Getter
    static class User {
        String userId;
        String email;
        List<Booking> bookings;

        public User(String userId, String email) {
            this.userId = userId;
            this.email = email;
            this.bookings = new ArrayList<>();
        }

        public void addBooking(Booking booking) {
            bookings.add(booking);
        }
    }

    static  class BookingManager {
        public static BookingManager INSTANCE  = new BookingManager();

        public Booking bookTicket(User user, ShowTime showTime, Integer seatNumber) {
            if(showTime.isSeatAvailable(seatNumber)) {
                showTime.bookSeat(seatNumber);
                Booking booking = new Booking(user,showTime,seatNumber);
                user.addBooking(booking);
                Payment p = new Payment(booking, 15.0);
                p.processPayment();
                return booking;
            } else {
                System.out.println("Seat not available.");
                return new Booking(null,null,-1);
            }


        }

    }
    static class Payment {
        Booking booking;
        Double amount;
        PaymentStatus status;

        public Payment(Booking booking, Double amount) {
            this.booking = booking;
            this.amount = amount;
            this.status = PaymentStatus.PENDING;
        }

        public void processPayment() {
            // Logic to process payment
            this.status = PaymentStatus.COMPLETED;
            System.out.println("Payment of $" + amount + " completed for booking.");
        }

        public PaymentStatus getStatus() {
            return status;
        }

    }
     enum PaymentStatus {
        PENDING, COMPLETED, FAILED;
    }

     class EventSystem {
         public static void main(String[] args) {
             Venue v = new Venue("venue 1", "Mumbai");
             Event e = new Concert("Musical", "taylor swift cobcet", new Date(), v,"Taylor Swift");
             ShowTime showTime = new ShowTime(new Date(), v,e);
             showTime.addSeat(new Seat(1));
             showTime.addSeat(new Seat(2));
             v.addShowTime(showTime);

             User user = new User("john_doe", "john@example.com");
             BookingManager bookingManager = BookingManager.INSTANCE;
             bookingManager.bookTicket(user, showTime,2);


         }
    }

}

/**
 * How to handle concurrency; such that no two users are able to
 * book the same seat? We can use transactions in SQL databases to avoid
 * any clashes. For example, if we are using SQL server we can
 * utilize Transaction Isolation Levels (https://docs.microsoft.com/enus/
 * sql/odbc/reference/develop-app/transaction-isolation-levels) to lock the
 * rows before we update them. Note: within a transaction, if we read rows we
 * get a write-lock on them so that they can’t be updated by anyone else. Here is
 * the sample code:
 */
