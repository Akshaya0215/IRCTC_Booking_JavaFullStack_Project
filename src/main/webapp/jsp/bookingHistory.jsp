<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Bookings - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <%-- Custom Styles --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-4">
        
        <%-- Header with white background box for readability --%>
        <div class="card-form mb-4" style="padding: 15px 30px; max-width: 900px;">
            <h2 class="mb-0" style="color: var(--irctc-blue); text-align: left;">
                <i class="bi bi-ticket-perforated-fill me-2" style="color: var(--irctc-orange);"></i> My Bookings
            </h2>
        </div>

        <%-- Message Section --%>
        <c:if test="${not empty sessionScope.errorMessage}">
             <div class="alert alert-danger message error-message" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <c:out value="${sessionScope.errorMessage}" />
            </div>
            <% session.removeAttribute("errorMessage"); %>
        </c:if>
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success message success-message" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                <c:out value="${sessionScope.successMessage}" />
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>

        <c:choose>
            <c:when test="${not empty bookingList}">
                
                <c:forEach var="booking" items="${bookingList}">
                    <div class="booking-card">
                        <div class="booking-header">
                            <h5>
                                PNR: <strong><c:out value="${booking.bookingId}" /></strong>
                                <%-- Status with dynamic color --%>
                                (<span class="status-${booking.status}"><c:out value="${booking.status}" /></span>)
                            </h5>
                            <span class="text-muted">
                                <i class="bi bi-calendar3 me-1"></i> Booked On: <fmt:formatDate value="${booking.createdAt}" pattern="dd-MMM-yyyy" />
                            </span>
                        </div>
                        <div class="booking-body">
                            <div class="route">
                                <i class="bi bi-train-front-fill me-2"></i>
                                <c:out value="${booking.train.trainName}" /> (<c:out value="${booking.train.trainNumber}" />)
                            </div>
                            <div class="row booking-info">
                                <div class="col-md-6">
                                    <p><strong style="color: var(--irctc-blue);"><i class="bi bi-geo-alt-fill me-1"></i> From:</strong> <c:out value="${booking.segmentSource}" /></p>
                                    <p><strong style="color: var(--irctc-blue);"><i class="bi bi-geo-alt-fill me-1"></i> To:</strong> <c:out value="${booking.segmentDestination}" /></p>
                                    <p><strong><i class="bi bi-calendar-event me-1"></i> Date:</strong> <fmt:formatDate value="${booking.journeyDate}" pattern="dd-MMM-yyyy" /></p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>Departure:</strong> <fmt:formatDate value="${booking.train.departureTime}" pattern="HH:mm" /></p>
                                    <p><strong>Arrival:</strong> <fmt:formatDate value="${booking.train.arrivalTime}" pattern="HH:mm" /></p>
                                    <p><strong><i class="bi bi-people-fill me-1"></i> Passengers:</strong> <c:out value="${booking.passengers}" /></p>
                                </div>
                            </div>
                             <hr>
                             <h5 class="text-end">
                                Total Fare: <span style="color: var(--irctc-blue); font-size: 1.2em;">₹<fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0.00" /></span>
                             </h5>
                        </div>
                        <div class="booking-footer">
                            <%-- Action Buttons with Icons --%>
                            
                            <%-- FIXED: Changed 'b' to 'booking' --%>
                            <c:if test="${booking.status == 'CONFIRMED'}">
                                <a href="${pageContext.request.contextPath}/FoodOrderingServlet?pnr=${booking.bookingId}" 
                                   class="btn btn-outline-warning text-dark me-2" 
                                   title="Order Food">
                                    <i class="bi bi-basket3-fill text-danger me-1"></i> e-Catering
                                </a>
                            
                                <a href="${pageContext.request.contextPath}/DownloadTicketServlet?bookingId=${booking.bookingId}" 
                                   class="btn btn-secondary me-2" target="_blank">
                                   <i class="bi bi-file-earmark-pdf-fill me-1"></i> Download Ticket
                                </a>
                                <a href="${pageContext.request.contextPath}/CancelBookingServlet?bookingId=${booking.bookingId}" 
                                   class="btn btn-danger" 
                                   onclick="return confirm('Are you sure you want to cancel PNR ${booking.bookingId}? This action cannot be undone.');">
                                   <i class="bi bi-x-circle-fill me-1"></i> Cancel Ticket
                                </a>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>

            </c:when>
            <c:otherwise>
                <div class="alert alert-info card-form" role="alert" style="max-width: 900px; margin-left: 0;">
                    <h4 class="alert-heading"><i class="bi bi-info-circle-fill me-2"></i> No Bookings Found</h4>
                    <p>You have not made any bookings yet. Please search for a train to book a ticket.</p>
                    <hr>
                    <a href="${pageContext.request.contextPath}/jsp/home.jsp" class="btn btn-primary">
                        <i class="bi bi-search me-1"></i> Book a Ticket
                    </a>
                </div>
            </c:otherwise>
        </c:choose>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>

</body>
</html>