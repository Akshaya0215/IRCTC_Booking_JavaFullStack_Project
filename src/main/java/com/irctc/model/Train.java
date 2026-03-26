package com.irctc.model;

import java.math.BigDecimal;
import java.sql.Time;

/**
 * Model class for a Train.
 * This is a DTO that maps to the 'trains' table.
 */
public class Train {

    // Fields based on the 'trains' table schema
    private int trainId;
    private String trainName;
    private String trainNumber;
    private String source;
    private String destination;
    private Time departureTime; // Main departure
    private Time arrivalTime;   // Main arrival
    private int totalSeats;
    private int availableSeats;
    private BigDecimal farePerKm; 
    
    // --- TRANSIENT FIELDS (Not in 'trains' table, calculated on the fly) ---
    private Time segmentDepartureTime; // Specific to the search route
    private Time segmentArrivalTime;   // Specific to the search route
    private BigDecimal ticketPrice;    // <--- NEW FIELD for Search Results
    // ----------------------------------------------------------------------

    // No-argument constructor
    public Train() {
    }

    // --- Getters and Setters ---

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getFarePerKm() {
        return farePerKm;
    }

    public void setFarePerKm(BigDecimal farePerKm) {
        this.farePerKm = farePerKm;
    }
    
    // --- NEW GETTERS/SETTERS ---
    
    public Time getSegmentDepartureTime() {
        return segmentDepartureTime;
    }

    public void setSegmentDepartureTime(Time segmentDepartureTime) {
        this.segmentDepartureTime = segmentDepartureTime;
    }

    public Time getSegmentArrivalTime() {
        return segmentArrivalTime;
    }

    public void setSegmentArrivalTime(Time segmentArrivalTime) {
        this.segmentArrivalTime = segmentArrivalTime;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "Train [trainId=" + trainId + ", trainName=" + trainName + ", trainNumber=" + trainNumber + "]";
    }
}