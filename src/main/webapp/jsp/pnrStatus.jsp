<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Check PNR Status - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="card-form text-center" style="max-width: 500px; margin: 50px auto;">
            <h2 class="mb-4" style="color: var(--irctc-blue);">
                <i class="bi bi-search me-2"></i>Check PNR Status
            </h2>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/PublicPnrServlet" method="GET">
                <div class="mb-4">
                    <input type="text" name="pnr" class="form-control form-control-lg text-center" placeholder="Enter PNR Number" required maxlength="10" style="font-size: 1.5rem; letter-spacing: 2px;">
                    <div class="form-text">Enter 10-digit PNR for real trains or Booking ID for clone.</div>
                </div>
                <button type="submit" class="btn btn-orange w-100 btn-lg">
                    Get Status
                </button>
            </form>
            
            <div class="mt-3">
                <a href="home.jsp" class="text-decoration-none">Back to Home</a>
            </div>
        </div>
    </div>
</body>
</html>