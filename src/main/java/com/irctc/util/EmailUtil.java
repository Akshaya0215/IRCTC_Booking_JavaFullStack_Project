package com.irctc.util;

import com.irctc.model.Booking;
import com.irctc.model.Passenger; 

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.InputStream;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

public class EmailUtil {

    private static String senderEmail;
    private static String senderPassword;
    private static Properties mailServerProperties;

    static {
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath.");
            }
            Properties props = new Properties();
            props.load(input);

            senderEmail = props.getProperty("mail.sender.email");
            senderPassword = props.getProperty("mail.sender.password");

            mailServerProperties = new Properties();
            mailServerProperties.put("mail.smtp.host", props.getProperty("mail.smtp.host"));
            mailServerProperties.put("mail.smtp.port", props.getProperty("mail.smtp.port"));
            mailServerProperties.put("mail.smtp.auth", props.getProperty("mail.smtp.auth"));
            mailServerProperties.put("mail.smtp.starttls.enable", props.getProperty("mail.smtp.starttls.enable"));
            
            // --- DEBUG MODE DISABLED (Clean Console) ---
            mailServerProperties.put("mail.debug", "false"); 
            // -------------------------------------------
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EmailUtil.", e);
        }
    }

    public static void sendBookingConfirmation(String recipientEmail, Booking booking, byte[] pdfAttachment, 
                                               String sourceStation, String destStation,
                                               Time segmentDepTime, Time segmentArrTime) {
        if (recipientEmail == null || recipientEmail.isEmpty() || booking == null) return;

        try {
            Session mailSession = Session.getInstance(mailServerProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });
            
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Booking Confirmed! PNR: " + booking.getBookingId());

            Multipart multipart = new MimeMultipart("mixed");

            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            String emailBody = buildHtmlEmailBody(booking, sourceStation, destStation, segmentDepTime, segmentArrTime);
            htmlBodyPart.setContent(emailBody, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlBodyPart);

            if (pdfAttachment != null) {
                MimeBodyPart pdfAttachmentPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(pdfAttachment, "application/pdf");
                pdfAttachmentPart.setDataHandler(new jakarta.activation.DataHandler(source));
                pdfAttachmentPart.setFileName("IRCTC_Ticket_PNR_" + booking.getBookingId() + ".pdf");
                multipart.addBodyPart(pdfAttachmentPart);
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("EmailUtil: Booking confirmation sent to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void sendVerificationEmail(String recipientEmail, String verificationLink) {
        if (recipientEmail == null || recipientEmail.isEmpty()) return;

        try {
            Session mailSession = Session.getInstance(mailServerProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Verify Your IRCTC Clone Account");

            String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<div style='max-width: 600px; margin: auto; border: 1px solid #ddd; border-top: 5px solid #fb792b; border-radius: 8px; overflow: hidden;'>"
                    + "<div style='background-color: #003366; color: white; padding: 20px; text-align: center;'>"
                    + "<h2 style='margin: 0;'>Welcome to IRCTC Clone!</h2>"
                    + "</div>"
                    + "<div style='padding: 30px;'>"
                    + "<p>Hi there,</p>"
                    + "<p>Thank you for registering. Please click the button below to verify your email address.</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + verificationLink + "' style='background-color: #fb792b; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Verify My Account</a>"
                    + "</div>"
                    + "<p style='font-size: 0.9em;'>If the button above doesn't work, copy and paste this link into your browser:</p>"
                    + "<p style='background-color: #f9f9f9; padding: 10px; border-radius: 4px; font-size: 0.85em; word-break: break-all;'>" + verificationLink + "</p>"
                    + "</div></div></body></html>";

            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("EmailUtil: Verification Link sent to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void sendOtpEmail(String recipientEmail, String otp) {
        if (recipientEmail == null || recipientEmail.isEmpty()) return;

        try {
            Session mailSession = Session.getInstance(mailServerProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Your OTP for IRCTC Clone Registration");

            String htmlBody = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<div style='max-width: 500px; margin: auto; border: 1px solid #ddd; border-top: 5px solid #fb792b; border-radius: 8px; overflow: hidden;'>"
                    + "<div style='padding: 30px; text-align: center;'>"
                    + "<h2 style='color: #003366; margin: 0;'>Verification Code</h2>"
                    + "<p style='font-size: 1.1em; color: #555;'>Please use the following OTP to complete your registration:</p>"
                    + "<div style='background-color: #f4f4f4; color: #333; font-size: 2em; font-weight: bold; letter-spacing: 5px; padding: 15px; margin: 20px 0; border-radius: 4px;'>"
                    + otp + "</div>"
                    + "<p style='font-size: 0.9em; color: #777;'>This code is valid for 10 minutes.</p>"
                    + "</div></div></body></html>";

            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("EmailUtil: OTP sent to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static String buildHtmlEmailBody(Booking booking, String sourceStation, String destStation,
            Time segmentDepTime, Time segmentArrTime) {

        String passengerName = "Passenger";
        if (booking.getPassengersList() != null && !booking.getPassengersList().isEmpty()) {
            passengerName = booking.getPassengersList().get(0).getName();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String journeyDateStr = (booking.getJourneyDate() != null) ? dateFormat.format(booking.getJourneyDate()) : "N/A";
        String trainName = "N/A", trainNum = "N/A";
        if(booking.getTrain() != null) {
            trainName = booking.getTrain().getTrainName() != null ? booking.getTrain().getTrainName() : "N/A";
            trainNum = booking.getTrain().getTrainNumber() != null ? booking.getTrain().getTrainNumber() : "N/A";
        }
        String departureTimeStr = (segmentDepTime != null) ? timeFormat.format(segmentDepTime) : "N/A";
        String arrivalTimeStr = (segmentArrTime != null) ? timeFormat.format(segmentArrTime) : "N/A";
        
        String arrivalDateStr = journeyDateStr; 
        if (segmentDepTime != null && segmentArrTime != null && segmentArrTime.before(segmentDepTime)) {
            Calendar c = Calendar.getInstance();
            c.setTime(booking.getJourneyDate());
            c.add(Calendar.DATE, 1); 
            arrivalDateStr = dateFormat.format(c.getTime());
        }
        
        String totalAmountStr = (booking.getTotalAmount() != null) ? String.format("&#8377;%.2f", booking.getTotalAmount()) : "N/A";
        String status = (booking.getStatus() != null) ? booking.getStatus() : "N/A";

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>");
        body.append("<div style='max-width: 600px; margin: auto; border: 1px solid #ddd; border-top: 5px solid #fb792b; border-radius: 8px; overflow: hidden;'>");

        // Header
        body.append("<div style='background-color: #003366; color: white; padding: 20px; text-align: center;'>");
        body.append("<h2 style='margin: 0;'>Booking Confirmed!</h2>");
        body.append("</div>");

        // Main Content
        body.append("<div style='padding: 30px;'>");
        body.append("<p>Dear ").append(passengerName).append(",</p>");
        body.append("<p>Your train ticket has been successfully booked. Please find the PDF ticket attached for your reference.</p>");

        // PNR Box
        body.append("<div style='text-align: center; margin: 25px 0; background: #f8f9fa; padding: 15px; border-radius: 8px;'>");
        body.append("<span style='font-size: 0.9em; color: #777; text-transform: uppercase;'>PNR Number</span><br>");
        body.append("<strong style='font-size: 1.8em; color: #003366; letter-spacing: 1px;'>").append(booking.getBookingId()).append("</strong><br>");
        body.append("<span style='font-size: 1em; font-weight: bold; color: #28a745;'>").append(status).append("</span>");
        body.append("</div>");

        // Journey Details
        body.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 20px;'>");
        body.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee;'><strong>Train:</strong></td><td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(trainName).append(" (").append(trainNum).append(")</td></tr>");
        body.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee;'><strong>Route:</strong></td><td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(sourceStation).append(" to ").append(destStation).append("</td></tr>");
        body.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee;'><strong>Departs:</strong></td><td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(departureTimeStr).append(", ").append(journeyDateStr).append("</td></tr>");
        body.append("<tr><td style='padding: 8px; border-bottom: 1px solid #eee;'><strong>Arrives:</strong></td><td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>").append(arrivalTimeStr).append(", ").append(arrivalDateStr).append("</td></tr>");
        body.append("</table>");

        // Passenger Table with Seats
        body.append("<h3 style='color: #003366; font-size: 1.1em; margin-top: 20px;'>Passenger Details</h3>");
        body.append("<table style='width: 100%; border-collapse: collapse; font-size: 0.95em; border: 1px solid #eee;'>");
        body.append("<tr style='background-color: #f2f2f2;'>");
        body.append("<th style='padding: 10px; text-align: left; border-bottom: 2px solid #ddd;'>Name</th>");
        body.append("<th style='padding: 10px; text-align: center; border-bottom: 2px solid #ddd;'>Age/Gender</th>");
        body.append("<th style='padding: 10px; text-align: right; border-bottom: 2px solid #ddd;'>Coach/Seat</th>"); 
        body.append("</tr>");

        if (booking.getPassengersList() != null) {
            for (Passenger p : booking.getPassengersList()) {
                String seatInfo = "CNF";
                if (p.getSeatNumber() > 0) {
                    seatInfo = "<b>" + p.getCoachCode() + "</b> / " + p.getSeatNumber();
                    if (p.getBerthType() != null) {
                        seatInfo += " [" + p.getBerthType() + "]";
                    }
                }
                
                body.append("<tr>");
                body.append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>").append(p.getName()).append("</td>");
                body.append("<td style='padding: 10px; text-align: center; border-bottom: 1px solid #eee;'>").append(p.getAge()).append(" / ").append(p.getGender()).append("</td>");
                body.append("<td style='padding: 10px; text-align: right; border-bottom: 1px solid #eee;'>").append(seatInfo).append("</td>"); 
                body.append("</tr>");
            }
        }
        body.append("</table>");

        body.append("<p style='text-align: right; margin-top: 15px; font-size: 1.2em;'>Total Fare: <strong style='color: #003366;'>").append(totalAmountStr).append("</strong></p>");

        body.append("<div style='text-align: center; margin-top: 30px; color: #777; font-size: 0.85em;'>");
        body.append("<p>Thank you for booking with IRCTC Clone!</p>");
        body.append("</div>"); 

        body.append("</div></div></body></html>");

        return body.toString();
    }
}