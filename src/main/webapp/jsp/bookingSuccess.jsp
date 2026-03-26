<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Successful - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        /* Specific style for the success checkmark */
        .success-icon {
            font-size: 80px;
            color: #28a745;
            line-height: 1;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="card-form text-center" style="max-width: 600px; margin: 20px auto;">
           
            <div class="success-icon">
                <i class="bi bi-check-circle-fill"></i>
            </div>

            <h2 style="color: #28a745; margin-top: 15px;">Booking Successful!</h2>
            
            <p class="lead" style="font-size: 1.3rem;">
                Your e-ticket has been confirmed for PNR: 
                <strong style="color: var(--irctc-blue);">${param.bookingId}</strong>
            </p>
            <p>
                A confirmation email with the PDF ticket has been sent to your primary email.
            </p>
            <hr class="my-4">
            
            <%-- Use a row for better button spacing on mobile --%>
            <div class="row g-2">
                <div class="col-md">
                    <a href="${pageContext.request.contextPath}/BookingHistoryServlet" class="btn btn-secondary w-100 py-2">
                        <i class="bi bi-ticket-perforated-fill me-1"></i> View My Bookings
                    </a>
                </div>
                <div class="col-md">
                    <a href="home.jsp" class="btn btn-orange w-100 py-2">
                        <i class="bi bi-train-front-fill me-1"></i> Book Another Ticket
                    </a>
                </div>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>

</body>
</html>