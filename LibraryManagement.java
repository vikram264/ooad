package ooad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

public class LibraryManagement {
    @Getter
    @Setter
    @AllArgsConstructor
    static abstract class User {
        String id;
        String phone;
        String name;
        String email;

    }
    @Getter
    @Setter
    static class Member extends User {
        private static final Integer MAX_BOOKS_ALLOWED = 5;
        Integer booksCheckedOut;

        public Member(String id, String phone, String name, String email) {
            super(id, phone, name, email);
        }
        public void incrementBooksCheckedOut() {
            booksCheckedOut++;
        }

        public void decrementBooksCheckedOut() {
            booksCheckedOut--;
        }

        public boolean canBorrow() {
            return booksCheckedOut < MAX_BOOKS_ALLOWED;
        }
    }
    static class Admin extends User {

        public Admin(String id, String phone, String name, String email) {
            super(id, phone, name, email);
        }
        public void addBookItem(Book book) {
         Library.INSTANCE.addBook(book);
        }

        public void removeBookItem(Book book) {

        }

        public void addMember(Member member) {

        }

        public void blockMember(Member member) {

        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Book {
        String isbn;
        String title;
        String author;
        String publisher;
        Date publicationDate;
        int totalCopies;
        int availableCopies;

        public Boolean isAvailable() {
            return availableCopies > 0;
        }
    }
    @Getter
    @Setter
    static class BookItem {
        Book book;
        String id; // barcode
        boolean isBorrowed;

        public BookItem(Book book, String id) {
            this.book = book;
            this.id = id;
            isBorrowed = false;
        }

        public void borrowed() {
            isBorrowed = true;
        }
    }

    interface Search {
        List<Book> findByTitle();
        List<Book> findByAuthor(); //etc
    }

    @Getter
    static class Catalog implements Search {
        List<Book> books;

        public Catalog() {
            books = new ArrayList<>();
        }

        public void addBook(Book book) {
            this.books.add(book);
        }

        @Override
        public List<Book> findByTitle() {
            return List.of();
        }

        @Override
        public List<Book> findByAuthor() {
            return List.of();
        }
    }

    @Getter
    static class Library {
        String name;
        String address;
        Catalog catalog;
        private Map<String, BookItem> bookItems;
        private Map<String, Loan> activeLoans;
        public static Library INSTANCE = new Library();
        private Library() {
            bookItems = new HashMap<>();
            activeLoans = new HashMap<>();
        }

        public void addBook(Book book) {
            catalog.addBook(book);
            String barcode = UUID.randomUUID().toString();
            BookItem bookItem = new BookItem(book, barcode);
            bookItems.put(barcode, bookItem);


        }
        public Loan loanBook(String barcode, Member member) {
            if (!member.canBorrow()) {
                System.out.println("Member has reached the borrowing limit.");
                return null;
            }
            BookItem bookItem = bookItems.get(barcode);
            if (bookItem == null || !bookItem.isBorrowed()) { // bookItem.getBook.isAvailable
                System.out.println("Book is not available.");
                return null;
            }
            bookItem.isBorrowed();
            Loan loan = new Loan(bookItem, member, new Date());
            activeLoans.put(barcode, loan);
            member.incrementBooksCheckedOut();
            return loan;
        }

        public void returnBook(String barcode, Member member) {
            Loan loan = activeLoans.get(barcode);
            if (loan != null) {
                loan.markAsReturned();
                BookItem bookItem = loan.getBookItem();
                bookItem.setBorrowed(false);
                member.decrementBooksCheckedOut();

                if (loan.isOverdue()) {
                    Fine fine = new Fine(member, calculateFine(loan));
                    System.out.println("Fine imposed on member: " + fine.getAmount());
                }

                activeLoans.remove(barcode);
            }
        }

        private double calculateFine(Loan loan) {
            long daysOverdue = (LocalDate.now().getDayOfMonth()- loan.getDueDate().getDayOfMonth()) / (1000 * 60 * 60 * 24);
            return daysOverdue > 0 ? daysOverdue * 2 : 0; // $2 fine per day overdue
        }
    }
    @Getter
    @Setter
    static class Loan {
        private BookItem bookItem;
        private Member member;
        private Date issueDate;
        private LocalDate dueDate;
        private Date returnDate;
        private static final Integer LOAN_PERIOD_DAYS = 14;

        public Loan(BookItem bookItem, Member member, Date issueDate) {
            this.bookItem = bookItem;
            this.member = member;
            this.issueDate = issueDate;
            this.dueDate = calculateDueDate();
        }

        public LocalDate calculateDueDate() {
            return LocalDate.now().plusDays(LOAN_PERIOD_DAYS);
        }

        public boolean isOverdue() {
            return returnDate == null && LocalDate.now().isAfter(getDueDate());
        }
        public void markAsReturned() {
            this.returnDate = new Date();
        }
    }

    static class Fine {
        private Member member;
        private double amount;

        public Fine(Member member, double amount) {
            this.member = member;
            this.amount = amount;
        }

        public Member getMember() {
            return member;
        }

        public double getAmount() {
            return amount;
        }

        public void payFine() {
            amount = 0.0;
        }
    }
}
