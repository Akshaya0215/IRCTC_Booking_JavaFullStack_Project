<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- PRELOADER RESOURCES --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/preloader.css">
<script src="${pageContext.request.contextPath}/js/preloader.js" defer></script>

<%-- PRELOADER RESOURCES --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/preloader.css">
<script src="${pageContext.request.contextPath}/js/preloader.js" defer></script>

<%-- PRELOADER RESOURCES --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/preloader.css">
<script src="${pageContext.request.contextPath}/js/preloader.js" defer></script>

<%-- PRELOADER RESOURCES --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/preloader.css">
<script src="${pageContext.request.contextPath}/js/preloader.js" defer></script>

<%-- PRELOADER HTML (Premium Orbit) --%>
<div id="preloader">
    <div class="loader-container">
        <%-- The Spinning Rings --%>
        <div class="ring ring-one"></div>
        <div class="ring ring-two"></div>
        
        <%-- The Static Logo in Center --%>
        <img src="${pageContext.request.contextPath}/assets/images/IRCTC.jpg" class="loader-logo" alt="Loading...">
    </div>
    
    <%-- Random Fact Text --%>
    <div class="loading-text">Loading your journey...</div>
</div>
<%-- END PRELOADER --%>

<nav class="navbar navbar-expand-lg navbar-light navbar-custom fixed-top">
  <div class="container-fluid">
    <%-- Left Logo (IRCTC) --%>
    <a class="navbar-brand" href="${pageContext.request.contextPath}/jsp/home.jsp">
        <img src="${pageContext.request.contextPath}/assets/images/irctc.png" alt="IRCTC Logo" style="height: 30px; margin-right: 5px;">
        IRCTC Clone
    </a>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">

        <c:choose>
            <c:when test="${empty sessionScope.loggedInUser}">
                <%-- Not Logged In --%>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/login.jsp">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Login
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/register.jsp">
                        <i class="bi bi-person-plus-fill me-1"></i> Register
                    </a>
                </li>
            </c:when>
            <c:otherwise>
                <%-- Logged In --%>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/profile.jsp" title="View Profile">
                        <i class="bi bi-person-circle me-1"></i> Welcome, <c:out value="${sessionScope.loggedInUser.fullName}" />!
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/BookingHistoryServlet">
                        <i class="bi bi-ticket-perforated-fill me-1"></i> My Bookings
                    </a>
                </li>
                <li class="nav-item">
                     <a class="nav-link" href="${pageContext.request.contextPath}/LogoutServlet">
                        <i class="bi bi-power me-1"></i> Logout
                    </a>
                </li>
            </c:otherwise>
        </c:choose>

        <%-- Right Logo (Indian Railways) --%>
        <li class="nav-item">
             <img src="${pageContext.request.contextPath}/assets/images/ir.png" alt="Indian Railways Logo" style="height: 35px; margin-left: 15px;">
        </li>

      </ul>
    </div>
  </div>
</nav>