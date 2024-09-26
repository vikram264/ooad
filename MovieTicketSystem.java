package ooad;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class MovieTicketSystem {

    static class Movie {
        private String title;
        private String description;
        private String genre;
        private Date releaseDate;
        private List<ShowTime> showtimes;

        public Movie(String title, String description, String genre, Date releaseDate) {
            this.title = title;
            this.description = description;
            this.genre = genre;
            this.releaseDate = releaseDate;
            this.showtimes = new ArrayList<>();
        }

        public String getTitle() {
            return title;
        }

        public List<ShowTime> getShowtimes() {
            return showtimes;
        }

        public void addShowtime(ShowTime showtime) {
            showtimes.add(showtime);
        }
    }


    // Multiplex
    static class Cinema {
        String name;
        List<Screen> screens;

        public Cinema(String name) {
            this.name = name;
            this.screens = new ArrayList<>();
        }


        public void addScreen(Screen screen) {
            this.screens.add(screen);
        }
        public List<ShowTime> getShowtimesForScreen(int screenNumber) {
            return screens.stream()
                    .filter(screen -> screen.getScreenId() == screenNumber)
                    .map(Screen::getShowtimes)
                    .findFirst()
                    .orElse(null);
        }

    }
    @Getter
    static class Screen {
        int screenId;
        private List<ShowTime> showtimes;

        public Screen(int screenId) {
            this.screenId = screenId;
            this.showtimes = new ArrayList<>();

        }

        public void addShowTime(ShowTime showTime) {
            showtimes.add(showTime);
        }
    }
    static class ShowTime {
        LocalDateTime time;
        Screen screen;
        Movie movie;
        private List<Seat> availableSeats;

        public ShowTime(LocalDateTime time, Movie movie, Screen screen, List<Seat> availableSeats) {
            this.time = time;
            this.movie = movie;
            this.screen = screen;
            this.availableSeats = availableSeats;
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

        public Movie getMovie() {
            return movie;
        }

        public Screen getScreen() {
            return screen;
        }

        public LocalDateTime getTime() {
            return time;
        }
    }
   @Getter
    static class Seat {
        int seatNumber;
        SeatType seatType;
        Boolean isBooked;

        public Seat(int seatNumber, SeatType seatType) {
            this.seatNumber = seatNumber;
            this.seatType = seatType;
            this.isBooked = false;
        }

        public void book() {
            this.isBooked = true;
        }
        public boolean isBooked() {
            return isBooked;
        }
    }
    enum SeatType {
        REGULAR, VIP, HANDICAPPED;
    }



    static class User {
        String name;
        String emailId;
        private List<Booking> bookingHistory;

        public User(String name, String emailId) {
            this.name = name;
            this.emailId = emailId;
        }

        public List<Booking> getBookingHistory() {
            return bookingHistory;
        }

        public void addBooking(Booking booking) {
            this.bookingHistory.add(booking);

        }

        public Booking bookTicket(ShowTime showtime, int seatNumber) {
            if (showtime.isSeatAvailable(seatNumber)) {
                showtime.bookSeat(seatNumber);
                Booking booking = new Booking(this, showtime, seatNumber);
                addBooking(booking);
                return booking;
            } else {
                System.out.println("Seat not available.");
                return null;
            }
        }
    }

    static class Booking {
        User user;
        ShowTime showTime;
        int seat;
        LocalDateTime bookingTime;

        public Booking(User user, ShowTime showTime, int seat) {
            this.user = user;
            this.showTime = showTime;
            this.seat = seat;
            bookingTime = LocalDateTime.now();
        }

        public User getUser() {
            return user;
        }

        public ShowTime getShowTime() {
            return showTime;
        }

        public int getSeat() {
            return seat;
        }

        public LocalDateTime getBookingTime() {
            return bookingTime;
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
        public enum PaymentStatus {
            PENDING, COMPLETED, FAILED;
        }
    }
    
    static class BookingManager {

        public static BookingManager INSTANCE  = new BookingManager();

        private BookingManager() {
        }

        // Method for booking a ticket
        public Booking bookTicket(ShowTime showtime, User user, int seatNumber) {
            if (showtime.isSeatAvailable(seatNumber)) {
                showtime.bookSeat(seatNumber);
                Booking booking = new Booking(user, showtime, seatNumber);
                user.addBooking(booking);
                return booking;
            } else {
                System.out.println("Seat not available.");
                return null;
            }
        }
    }

    static class MovieSystem {
        public void main(String[] args) {

        }
    }
    interface Search {
        public List<Movie> searchByTitle(String title);
        public List<Movie> searchByLanguage(String language);
        public List<Movie> searchByGenre(String genre);
        public List<Movie> searchByReleaseDate(Date relDate);
        public List<Movie> searchByCity(String cityName);
    }

    class Catalog implements Search {
        List<Movie> movies;

        public Catalog() {
            this.movies = new ArrayList<>();
        }
        // Add movies to the system
        public void addMovie(Movie movie) {
            movies.add(movie);
        }
        @Override
        public List<Movie> searchByTitle(String title) {
            return List.of();
        }

        @Override
        public List<Movie> searchByLanguage(String language) {
            return List.of();
        }

        @Override
        public List<Movie> searchByGenre(String genre) {
            return List.of();
        }

        @Override
        public List<Movie> searchByReleaseDate(Date relDate) {
            return List.of();
        }

        @Override
        public List<Movie> searchByCity(String cityName) {
            return List.of();
        }
    }
}
