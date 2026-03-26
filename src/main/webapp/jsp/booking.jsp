<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirm Booking - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- ADDED: Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="card-form">
            <%-- Updated Header --%>
            <h2><i class="bi bi-people-fill me-2"></i>Passenger Details</h2>

            <c:if test="${empty sessionScope.loggedInUser}">
                <div class="alert alert-danger" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    You must be logged in to book a ticket.
                    <a href="login.jsp" class="alert-link">Login Now</a>
                </div>
            </c:if>

            <c:if test="${not empty sessionScope.loggedInUser}">

                <div class="booking-summary">
                    <h3>
                        <i class="bi bi-train-front-fill me-2"></i>
                        <c:out value="${train.trainName}" /> (<c:out value="${train.trainNumber}" />)
                    </h3>
                    <p>
                        <i class="bi bi-geo-alt-fill me-1" style="color: var(--irctc-blue);"></i> From: <strong><c:out value="${source}" /></strong>
                        <i class="bi bi-arrow-right mx-2" style="color: #999;"></i>
                        <i class="bi bi-geo-alt-fill me-1" style="color: var(--irctc-blue);"></i> To: <strong><c:out value="${destination}" /></strong>
                    </p>
                    <p>
                        <i class="bi bi-calendar-event-fill me-1" style="color: var(--irctc-blue);"></i> Journey Date: <strong><fmt:formatDate value="${journeyDate}" pattern="dd-MMM-yyyy" /></strong>
                    </p>
                    
                    <c:choose>
                        <c:when test="${calculatedFare > 0}">
                             <p class="fare-details">
                                Fare per Passenger:
                                <fmt:setLocale value="en_IN"/>
                                <fmt:formatNumber value="${calculatedFare}" type="currency" currencyCode="INR" />
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="fare-details text-muted" style="font-size: 1rem;">
                                <i class="bi bi-info-circle me-1"></i> (Total fare will be calculated on the next step)
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>

              
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger message error-message" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <c:out value="${errorMessage}" />
                    </div>
                </c:if>

        
                <form action="${pageContext.request.contextPath}/BookingServlet" method="POST">
                    
                    <input type="hidden" name="trainId" value="${train.trainId}" />
                    <input type="hidden" name="journeyDate" value="${journeyDate}" />
                    <input type="hidden" name="source" value="${source}" />
                    <input type="hidden" name="destination" value="${destination}" />
                    <input type="hidden" name="classCode" value="${classCode}" />

                    <div class="mb-3">
                         <label for="primaryEmail" class="form-label">Contact Email (for ticket):</label>
                         <%-- Added Input Group with Icon --%>
                         <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope-at-fill"></i></span>
                            <input type="email" id="primaryEmail" name="primaryEmail" class="form-control" 
                                   value="<c:out value='${sessionScope.loggedInUser.email}'/>" required>
                        </div>
                         <div class="form-text">Your e-ticket will be sent to this email address.</div>
                    </div>

                    <div class="mb-3">
                        <label for="passengers" class="form-label">Number of Passengers:</label>
                        <%-- Added Input Group with Icon --%>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person-lines-fill"></i></span>
                            <select id="passengers" name="passengers" class="form-select" required>
                                <c:set var="maxSeats" value="${train.availableSeats < 6 ? train.availableSeats : 6}" />
                                <c:forEach var="i" begin="1" end="${maxSeats}">
                                    <option value="${i}">${i}</option>
                                </c:forEach>
                             </select>
                        </div>
                        <div class="form-text">
                            (Max ${maxSeats} allowed. Available Seats: <c:out value="${train.availableSeats}" />)
                     </div>
                    </div>

                    <hr class="my-4" style="color: var(--irctc-blue);">
                    
                    <div id="passenger-forms-container">
                         <%-- Passenger forms will be injected here --%>
                    </div>

                    <button type="submit" class="btn btn-orange w-100 py-2" style="font-size: 1.2rem;">
                        Proceed to Payment <i class="bi bi-arrow-right-circle-fill ms-2"></i>
                    </button>
                </form>

            </c:if> 
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() 
        {
            const passengerSelect = document.getElementById('passengers');
            const container = document.getElementById('passenger-forms-container');
            const loggedInUser = {
                name: "<c:out value='${sessionScope.loggedInUser.fullName}'/>",
                gender: "<c:out value='${sessionScope.loggedInUser.gender}'/>",
                phone: "<c:out value='${sessionScope.loggedInUser.phone}'/>"
            };
            function generatePassengerForms(count) {
                container.innerHTML = '';
                
                for (let i = 1; i <= count; i++) {
                    const name = (i === 1) ? loggedInUser.name : '';
                    const gender = (i === 1) ? loggedInUser.gender : '';
                    const phone = (i === 1) ? loggedInUser.phone : '';
                    const email = (i === 1) ? document.getElementById('primaryEmail').value : '';
                    const isFirst = (i === 1);
                    
                    // --- UPDATED JAVASCRIPT TO INCLUDE ICONS IN GENERATED HTML ---
                    let formHtml = 
                        '<div class="passenger-form">' +
                            '<h5><i class="bi bi-person-fill me-2"></i>Passenger ' + i + (isFirst ? ' (Primary)' : '') + '</h5>' +
                            '<div class="row">' +
                                '<div class="col-md-12 mb-3">' +
                                    '<label for="paxName' + i + '" class="form-label">Full Name</label>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-text"><i class="bi bi-person-vcard"></i></span>' +
                                        '<input type="text" id="paxName' + i + '" name="paxName" class="form-control" value="' + name + '" required placeholder="Name as per ID">' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="row">' +
                                '<div class="col-md-6 mb-3">' +
                                    '<label for="paxAge' + i + '" class="form-label">Age</label>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-text"><i class="bi bi-calendar3"></i></span>' +
                                        '<input type="number" id="paxAge' + i + '" name="paxAge" class="form-control" min="1" max="120" required placeholder="Age">' +
                                    '</div>' +
                                '</div>' +
                                '<div class="col-md-6 mb-3">' +
                                    '<label for="paxGender' + i + '" class="form-label">Gender</label>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-text"><i class="bi bi-gender-ambiguous"></i></span>' +
                                        '<select id="paxGender' + i + '" name="paxGender" class="form-select" required>' +
                                            '<option value="">-- Select --</option>' +
                                            '<option value="MALE" ' + (gender === 'MALE' ? 'selected' : '') + '>Male</option>' +
                                            '<option value="FEMALE" ' + (gender === 'FEMALE' ? 'selected' : '') + '>Female</option>' +
                                            '<option value="OTHER" ' + (gender === 'OTHER' ? 'selected' : '') + '>Other</option>' +
                                        '</select>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="row">' +
                                '<div class="col-md-6 mb-3">' +
                                    '<label for="paxPhone' + i + '" class="form-label">Phone (Optional)</label>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-text"><i class="bi bi-telephone"></i></span>' +
                                        '<input type="tel" id="paxPhone' + i + '" name="paxPhone" class="form-control" value="' + phone + '" placeholder="Mobile Number">' +
                                    '</div>' +
                                '</div>' +
                                '<div class="col-md-6 mb-3">' +
                                    '<label for="paxEmail' + i + '" class="form-label">Email (Optional)</label>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-text"><i class="bi bi-envelope"></i></span>' +
                                        '<input type="email" id="paxEmail' + i + '" name="paxEmail" class="form-control" value="' + email + '" placeholder="Email Address">' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>';
                    // --- END UPDATED JAVASCRIPT ---
                        
                    container.insertAdjacentHTML('beforeend', formHtml);
                }
            }
            passengerSelect.addEventListener('change', (e) => {
                generatePassengerForms(e.target.value);
            });
            generatePassengerForms(passengerSelect.value);
        });
    </script>
</body>
</html>