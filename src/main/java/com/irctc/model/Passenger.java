package com.irctc.model;

/**
 * Model class for a Passenger.
 * This is a DTO that maps to the 'passenger_details' table.
 */
public class Passenger {

    private int passengerId;
    private int bookingId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    
    // --- NEW FIELDS for Seat Selection ---
    private String coachCode;   // e.g., "S1", "B2"
    private int seatNumber;     // e.g., 45
    private String berthType;   // e.g., "LOWER", "SIDE UPPER"
    // -------------------------------------

    // No-argument constructor
    public Passenger() {}

    // --- Getters and Setters ---

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // --- NEW GETTERS/SETTERS ---

    public String getCoachCode() {
        return coachCode;
    }

    public void setCoachCode(String coachCode) {
        this.coachCode = coachCode;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }
}