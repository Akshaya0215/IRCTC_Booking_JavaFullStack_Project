package com.irctc.util;

import com.irctc.model.Booking;
import com.irctc.model.Payment;
import com.irctc.model.Passenger;
import com.irctc.model.Train;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import jakarta.servlet.ServletContext;

public class PdfTicketUtil {

    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final Color COLOR_PRIMARY_BLUE = new Color(0, 51, 102);
    private static final Color COLOR_ACCENT_ORANGE = new Color(251, 121, 43);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BLACK = Color.BLACK;
    private static final Color COLOR_GRAY_TEXT = new Color(85, 85, 85);
    private static final Color COLOR_LIGHT_GRAY_BG = new Color(240, 240, 240);
    private static final Color COLOR_PAGE_BACKGROUND = new Color(233, 236, 239);

    public static byte[] generateTicketPdf(Booking booking, Payment payment, ServletContext context,
                                           String sourceStation, String destStation,
                                           Time segmentDepTime, Time segmentArrTime) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            float pageHeight = page.getMediaBox().getHeight();
            float pageWidth = page.getMediaBox().getWidth();
            float margin = 40;
            float cornerRadius = 8; 

            float cardX = margin;
            float cardWidth = pageWidth - (2 * margin);
            float cardHeight = pageHeight - (2 * margin);
            float cardY = margin;
            
            float contentX = cardX + 20; 
            float contentWidth = cardWidth - 40; 
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // 1. PAGE BACKGROUND
                contentStream.setNonStrokingColor(COLOR_PAGE_BACKGROUND);
                contentStream.addRect(0, 0, pageWidth, pageHeight);
                contentStream.fill();

                // 2. MAIN CARD
                contentStream.setNonStrokingColor(COLOR_WHITE);
                drawRoundedRectangle(contentStream, cardX, cardY, cardWidth, cardHeight, cornerRadius, true, true, true, true);
                contentStream.fill();
                
                // 3. CARD HEADER
                float orangeBarHeight = 5; 
                float headerHeight = 55; 
                float headerY = cardY + cardHeight - orangeBarHeight - headerHeight;
                float orangeBarY = cardY + cardHeight - orangeBarHeight;

                contentStream.setNonStrokingColor(COLOR_ACCENT_ORANGE);
                drawRoundedRectangle(contentStream, cardX, orangeBarY, cardWidth, orangeBarHeight, cornerRadius, true, true, false, false);
                contentStream.fill();

                BufferedImage gradientImage = createGradientImage((int)cardWidth, (int)headerHeight);
                PDImageXObject pdGradient = LosslessFactory.createFromImage(document, gradientImage);

                contentStream.saveGraphicsState();
                createRoundedRectPath(contentStream, cardX, headerY, cardWidth, headerHeight, cornerRadius, false, false, false, false); 
                contentStream.clip();
                contentStream.drawImage(pdGradient, cardX, headerY, cardWidth, headerHeight);
                contentStream.restoreGraphicsState(); 
                
                // 4. LOGOS & TEXT
                try (InputStream irctcLogoStream = context.getResourceAsStream("/assets/images/IRCTC.jpg")) {
                    if (irctcLogoStream != null) {
                        PDImageXObject irctcLogo = PDImageXObject.createFromByteArray(document, irctcLogoStream.readAllBytes(), "irctc_logo");
                        contentStream.drawImage(irctcLogo, cardX + 15, headerY + 12, 30, 30);
                    }
                }
                try (InputStream irLogoStream = context.getResourceAsStream("/assets/images/ir.png")) {
                    if (irLogoStream != null) {
                        PDImageXObject irLogo = PDImageXObject.createFromByteArray(document, irLogoStream.readAllBytes(), "ir_logo");
                        contentStream.drawImage(irLogo, cardX + cardWidth - 45, headerY + 12, 30, 30);
                    }
                }

                String headerText = "Electronic Reservation Slip (ERS)";
                float textWidth = (FONT_BOLD.getStringWidth(headerText) / 1000) * 16;
                float textX = cardX + (cardWidth - textWidth) / 2;
                float textY = headerY + (headerHeight / 2) - 6; 
                drawText(contentStream, headerText, FONT_BOLD, 16, COLOR_WHITE, textX, textY);

                // 5. CONTENT START
                float yPos = headerY - 25; 

                // 6. QR CODE
                try {
                    String qrData = String.format("PNR: %d\nTrain: %s\nFrom: %s\nTo: %s\nDate: %s",
                            booking.getBookingId(), booking.getTrain().getTrainNumber(),
                            sourceStation, destStation, booking.getJourneyDate().toString());

                    QRCodeWriter qrWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrWriter.encode(qrData, BarcodeFormat.QR_CODE, 90, 90);
                    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                    ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
                    ImageIO.write(qrImage, "PNG", qrBaos);
                    byte[] qrBytes = qrBaos.toByteArray();
                    PDImageXObject qrPdImage = PDImageXObject.createFromByteArray(document, qrBytes, "qrCode");

                    contentStream.drawImage(qrPdImage, contentX, yPos - 90, 90, 90);
                } catch (Exception e) {
                    System.err.println("PdfTicketUtil: Could not generate QR code.");
                }

                // 7. PNR & TRAIN INFO
                String trainNumName = booking.getTrain().getTrainNumber() + " / " + booking.getTrain().getTrainName();
                
                drawText(contentStream, "PNR:", FONT_REGULAR, 12, COLOR_GRAY_TEXT, contentX + 110, yPos - 20);
                drawText(contentStream, String.valueOf(booking.getBookingId()), FONT_BOLD, 22, COLOR_BLACK, contentX + 110, yPos - 45);
                drawText(contentStream, "Train:", FONT_REGULAR, 12, COLOR_GRAY_TEXT, contentX + 110, yPos - 70);
                drawText(contentStream, trainNumName, FONT_BOLD, 12, COLOR_PRIMARY_BLUE, contentX + 110, yPos - 90);
                yPos -= 110;

                // 8. JOURNEY DETAILS
                String bookingDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(booking.getCreatedAt());
                String depTimeStr = timeFormat.format(segmentDepTime);
                String arrTimeStr = timeFormat.format(segmentArrTime);
                String journeyDateStr = dateFormat.format(booking.getJourneyDate());
                String arrivalDateStr = journeyDateStr;

                if (segmentArrTime.before(segmentDepTime)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(booking.getJourneyDate());
                    c.add(Calendar.DATE, 1);
                    arrivalDateStr = dateFormat.format(c.getTime());
                }

                float col1 = contentX;
                float col2 = contentX + 270;

                drawText(contentStream, "From:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col1, yPos);
                drawText(contentStream, sourceStation, FONT_BOLD, 12, COLOR_BLACK, col1, yPos - 15);
                drawText(contentStream, "To:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col2, yPos);
                drawText(contentStream, destStation, FONT_BOLD, 12, COLOR_BLACK, col2, yPos - 15);
                yPos -= 35;

                drawText(contentStream, "Departure:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col1, yPos);
                drawText(contentStream, depTimeStr + " on " + journeyDateStr, FONT_BOLD, 12, COLOR_BLACK, col1, yPos - 15);
                drawText(contentStream, "Arrival:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col2, yPos);
                drawText(contentStream, arrTimeStr + " on " + arrivalDateStr, FONT_BOLD, 12, COLOR_BLACK, col2, yPos - 15);
                yPos -= 35;
                
                drawText(contentStream, "Booking Date:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col1, yPos);
                drawText(contentStream, bookingDate, FONT_BOLD, 12, COLOR_BLACK, col1, yPos - 15);
                drawText(contentStream, "Class:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col2, yPos);
                drawText(contentStream, "SLEEPER (SL)", FONT_BOLD, 12, COLOR_BLACK, col2, yPos - 15);
                yPos -= 35;


                // 9. PASSENGER DETAILS (UPDATED for SEAT INFO)
                yPos -= 15;
                drawSectionHeader(contentStream, "Passenger Details", yPos, contentX);
                yPos -= 25;

                // Define columns for 5 items now
                float xName = contentX + 5;
                float xAge = contentX + 220;
                float xGender = contentX + 270;
                float xStatus = contentX + 330;
                float xSeat = contentX + 400; // New Column

                drawTableHeader(contentStream, contentX, yPos, contentWidth);
                drawTableRow(contentStream, yPos, FONT_BOLD, COLOR_BLACK, 
                             xName, xAge, xGender, xStatus, xSeat, 
                             "Name", "Age", "Gender", "Status", "Coach/Seat");
                yPos -= 15;

                List<Passenger> passengers = booking.getPassengersList();
                if (passengers != null && !passengers.isEmpty()) {
                    for (Passenger p : passengers) {
                        // Format Seat Info: "S1 / 12 [LB]" or "CNF (No Seat)"
                        String seatInfo = "CNF";
                        if (p.getSeatNumber() > 0) {
                            seatInfo = p.getCoachCode() + " / " + p.getSeatNumber();
                            if (p.getBerthType() != null) {
                                seatInfo += " [" + p.getBerthType() + "]";
                            }
                        }

                        drawTableRow(contentStream, yPos, FONT_REGULAR, COLOR_BLACK, 
                                     xName, xAge, xGender, xStatus, xSeat,
                                     p.getName(), String.valueOf(p.getAge()), p.getGender(), "CNF", seatInfo);
                        yPos -= 15;
                    }
                }

                // 10. PAYMENT DETAILS
                yPos -= 20;
                drawSectionHeader(contentStream, "Payment Details", yPos, contentX);
                yPos -= 25;

                drawText(contentStream, "Total Fare:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col1, yPos);
                drawText(contentStream, String.format("INR %.2f", booking.getTotalAmount()), FONT_BOLD, 14, COLOR_BLACK, col1, yPos - 20);
                drawText(contentStream, "Transaction ID:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col2, yPos);
                drawText(contentStream, payment.getTransactionId(), FONT_BOLD, 12, COLOR_BLACK, col2, yPos - 15);
                drawText(contentStream, "Payment Mode:", FONT_REGULAR, 10, COLOR_GRAY_TEXT, col2, yPos - 35);
                drawText(contentStream, payment.getPaymentMode(), FONT_BOLD, 12, COLOR_BLACK, col2, yPos - 50);

                // 11. FOOTER
                yPos = cardY + 65; 
                drawSectionHeader(contentStream, "Important Instructions", yPos, contentX);
                yPos -= 20;
                drawText(contentStream, "1. Prescribed Original ID proof is required while travelling.", FONT_REGULAR, 8, contentX, yPos);
                yPos -= 12;
                drawText(contentStream, "2. Passengers having fully waitlisted e-tickets are not allowed to board the train.", FONT_REGULAR, 8, contentX, yPos);
                yPos -= 12;
                drawText(contentStream, "3. Contact care@irctc.co.in or call 14646 for support.", FONT_REGULAR, 8, contentX, yPos);

            }
            document.save(baos);
        } catch (Exception e) {
            System.err.println("PdfTicketUtil ERROR: Failed to generate PDF.");
            e.printStackTrace();
            return null;
        }
        return baos.toByteArray();
    }

    // --- Helpers ---
    private static void drawText(PDPageContentStream stream, String text, PDType1Font font, int fontSize, Color color, float x, float y) throws IOException {
        stream.beginText();
        stream.setFont(font, fontSize);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, y);
        stream.showText(text != null ? text : "");
        stream.endText();
    }
    
    private static void drawText(PDPageContentStream stream, String text, PDType1Font font, int fontSize, float x, float y) throws IOException {
        drawText(stream, text, font, fontSize, COLOR_BLACK, x, y);
    }
    
    // Updated to handle 5 columns
    private static void drawTableRow(PDPageContentStream stream, float yPos, PDType1Font font, Color color, 
                                     float x1, float x2, float x3, float x4, float x5, 
                                     String col1, String col2, String col3, String col4, String col5) throws IOException {
        drawText(stream, col1, font, 10, color, x1, yPos);
        drawText(stream, col2, font, 10, color, x2, yPos);
        drawText(stream, col3, font, 10, color, x3, yPos);
        drawText(stream, col4, font, 10, color, x4, yPos);
        drawText(stream, col5, font, 10, color, x5, yPos);
    }
    
    private static void drawTableHeader(PDPageContentStream stream, float x, float y, float width) throws IOException {
        stream.setNonStrokingColor(COLOR_LIGHT_GRAY_BG);
        stream.addRect(x, y - 4, width, 18);
        stream.fill();
        stream.setNonStrokingColor(COLOR_BLACK);
    }
    
    private static void drawSectionHeader(PDPageContentStream stream, String text, float yPos, float x) throws IOException {
        drawText(stream, text, FONT_BOLD, 14, COLOR_ACCENT_ORANGE, x, yPos);
        stream.setStrokingColor(COLOR_ACCENT_ORANGE);
        stream.setLineWidth(0.5f);
        stream.moveTo(x, yPos - 5);
        stream.lineTo(x + 200, yPos - 5);
        stream.stroke();
    }

    private static BufferedImage createGradientImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(width, 0);
        float[] fractions = {0.0f, 0.20f, 0.80f, 1.0f};
        Color[] colors = {Color.WHITE, COLOR_PRIMARY_BLUE, COLOR_PRIMARY_BLUE, Color.WHITE};
        LinearGradientPaint paint = new LinearGradientPaint(start, end, fractions, colors);
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    private static void drawRoundedRectangle(PDPageContentStream cs, float x, float y, float width, float height, float radius,
                                             boolean roundTopLeft, boolean roundTopRight, boolean roundBottomLeft, boolean roundBottomRight) throws IOException {
        createRoundedRectPath(cs, x, y, width, height, radius, roundTopLeft, roundTopRight, roundBottomLeft, roundBottomRight);
        cs.fill();
    }

    private static void createRoundedRectPath(PDPageContentStream cs, float x, float y, float width, float height, float radius,
                                             boolean roundTopLeft, boolean roundTopRight, boolean roundBottomLeft, boolean roundBottomRight) throws IOException {
        cs.moveTo(x + (roundBottomLeft ? radius : 0), y);
        cs.lineTo(x + width - (roundBottomRight ? radius : 0), y);
        if (roundBottomRight) {
            cs.curveTo(x + width - radius, y, x + width, y, x + width, y + radius);
        } else {
            cs.lineTo(x + width, y);
        }
        cs.lineTo(x + width, y + height - (roundTopRight ? radius : 0));
        if (roundTopRight) {
            cs.curveTo(x + width, y + height - radius, x + width, y + height, x + width - radius, y + height);
        } else {
            cs.lineTo(x + width, y + height);
        }
        cs.lineTo(x + (roundTopLeft ? radius : 0), y + height);
        if (roundTopLeft) {
            cs.curveTo(x + radius, y + height, x, y + height, x, y + height - radius);
        } else {
            cs.lineTo(x, y + height);
        }
        cs.lineTo(x, y + (roundBottomLeft ? radius : 0));
        if (roundBottomLeft) {
            cs.curveTo(x, y + radius, x, y, x + radius, y);
        } else {
            cs.lineTo(x, y);
        }
    }
}