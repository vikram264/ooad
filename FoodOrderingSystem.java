package ooad;

import jdk.jfr.StackTrace;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
Design Food Ordering System with OOAD
Key Components
User - who orders food
Restaurant - which offers food
Menu - Menu of restaurant
Order - User places the order
Payment - Payment of order
Delivery - of the order
 */
public class FoodOrderingSystem {

    class User {
        String name;
        String phone;

        public User(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    class Restaurant {
        String name;
        String id;
        String address;
        Menu menu;

        public Restaurant(String name, String id, String address, Menu menu) {
            this.name = name;
            this.id = id;
            this.address = address;
            this.menu = menu;
        }
    }

    class Menu {
        List<MenuItem> menuItems;

        public Menu() {
            menuItems = new ArrayList<>();
        }

        public void addMenuItem(MenuItem menuItem) {
            menuItems.add(menuItem);
        }

        public List<MenuItem> getMenuItems() {
            return menuItems;
        }
    }

    class MenuItem {
        String name;
        String id;
        Double price;

        public MenuItem(String name, String id, Double price) {
            this.name = name;
            this.id = id;
            this.price = price;
        }
    }
    @Getter
    @Setter
    class Order {
        String id;
        Restaurant restaurant;
        List<OrderItem> orderItems;
        OrderStatus orderStatus;
        Payment payment;

        public Order(String id, Restaurant restaurant, List<OrderItem> orderItems, OrderStatus orderStatus, Payment payment) {
            this.id = id;
            this.restaurant = restaurant;
            this.orderItems = orderItems;
            this.orderStatus = orderStatus;
            this.payment = payment;
        }
        public void addOrderItem(OrderItem item) {
            orderItems.add(item);
        }

        public void makePayment(Payment payment) {
            this.payment = payment;
            this.orderStatus = OrderStatus.PAID;
        }

        public void updateStatus(OrderStatus status) {
            this.orderStatus = status;
        }
    }

    class OrderItem {
        MenuItem menuItem;
        int quantity;

        public OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }
    }

    enum OrderStatus {
        PENDING, PAID, PREPARING, DELIVERED, CANCELLED
    }
    @Getter
    @Setter
    class Payment {
        String id;
        Double amount;
        PaymentStatus paymentStatus;

        public Payment(String id, Double amount, PaymentStatus paymentStatus) {
            this.id = id;
            this.amount = amount;
            this.paymentStatus = paymentStatus;
        }
        public void processPayment() {
            // Simulate payment processing
            this.paymentStatus = PaymentStatus.COMPLETED;
        }
    }
    enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
    @Getter
    @Setter
    class Delivery {
        String deliveryId;
        Order order;
        DeliveryStatus deliveryStatus;

        public Delivery(String deliveryId, Order order) {
            this.deliveryId = deliveryId;
            this.order = order;
        }
        public void updateStatus(DeliveryStatus status) {
            this.deliveryStatus = status;
        }
    }
    enum DeliveryStatus {
        PENDING, IN_PROGRESS, DELIVERED
    }
    class OrderingSystem {
        private List<User> users;
        private List<Restaurant> restaurants;
        private List<Order> orders;
        private List<Delivery> deliveries;

        public OrderingSystem() {
            this.users = new ArrayList<>();
            this.restaurants = new ArrayList<>();
            this.orders = new ArrayList<>();
            this.deliveries = new ArrayList<>();
        }

        public void addUser(User user) {
            users.add(user);
        }

        public void addRestaurant(Restaurant restaurant) {
            restaurants.add(restaurant);
        }

        public void placeOrder(Order order) {
            orders.add(order);
            order.updateStatus(OrderStatus.PREPARING);
            Delivery delivery = new Delivery("D" + (deliveries.size() + 1), order);
            deliveries.add(delivery);
        }

        public void completePayment(Order order, Payment payment) {
            order.makePayment(payment);
            payment.processPayment();
            order.updateStatus(OrderStatus.PAID);
        }

        public void deliverOrder(Delivery delivery) {
            delivery.updateStatus(DeliveryStatus.DELIVERED);
            delivery.getOrder().updateStatus(OrderStatus.DELIVERED);
        }

        public List<Restaurant> getRestaurants() {
            return restaurants;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public List<Delivery> getDeliveries() {
            return deliveries;
        }
    }
}
