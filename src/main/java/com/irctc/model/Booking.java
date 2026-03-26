package com.irctc.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List; // *** NEW IMPORT ***

/**
 * Model class for a Booking.
 * This is a DTO that maps to the 'bookings' table.
 */
public class Booking {

    // Fields based on the 'bookings' table schema
    private int bookingId;
    private int userId;
    private int trainId;
    private Date bookingDate;
    private Date journeyDate;
    private int passengers; // This is the count (or seats_booked)
    private BigDecimal totalAmount;
    private String primaryEmail; // The email we added to the table
    
    // --- NEW FIELDS (from our ALTER TABLE) ---
    private String segmentSource;
    private String segmentDestination;
    // --- END NEW FIELDS ---
    
    private String status;
    private Timestamp createdAt;
    
    // --- FIELDS Not in the table, but used for logic ---
    
    // Used to hold the joined Train object
    private Train train; 
    
    // Used to hold the joined User object
    private User user;
    
    // Used to hold the list of passenger details
    private List<Passenger> passengersList;

    // --- END LOGIC FIELDS ---

    // No-argument constructor
    public Booking() {
    }

    // --- Getters and Setters ---

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Date journeyDate) {
        this.journeyDate = journeyDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    // --- NEW GETTERS/SETTERS ---
    public String getSegmentSource() {
        return segmentSource;
    }

    public void setSegmentSource(String segmentSource) {
        this.segmentSource = segmentSource;
    }

    public String getSegmentDestination() {
        return segmentDestination;
    }

    public void setSegmentDestination(String segmentDestination) {
        this.segmentDestination = segmentDestination;
    }
    // --- END NEW GETTERS/SETTERS ---

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // --- Getters/Setters for Joined/Logic Objects ---

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Passenger> getPassengersList() {
        return passengersList;
    }

    public void setPassengersList(List<Passenger> passengersList) {
        this.passengersList = passengersList;
    }
}