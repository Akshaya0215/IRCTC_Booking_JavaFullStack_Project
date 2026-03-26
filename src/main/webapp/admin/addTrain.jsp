<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add New Train - Admin</title>
    
    <%-- 1. Bootstrap & Icons --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <%-- 2. Admin Theme --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">

    <style>
        .form-card {
            background: white;
            border-radius: 12px;
            box-shadow: var(--shadow-soft);
            padding: 30px;
            max-width: 800px;
            margin: 0 auto;
            border-top: 5px solid var(--admin-accent);
        }
        .form-label {
            font-weight: 600;
            color: #555;
            font-size: 0.9rem;
        }
        .input-group-text {
            background-color: #f8f9fa;
            color: #666;
        }
    </style>
</head>
<body>

    <%-- Security Check --%>
    <c:if test="${empty sessionScope.loggedInAdmin}">
        <c:redirect url="${pageContext.request.contextPath}/admin/adminLogin.jsp" />
    </c:if>

    <div class="admin-wrapper">
        
        <%-- 3. Sidebar --%>
        <jsp:include page="adminNavbar.jsp" />

        <%-- 4. Main Content --%>
        <div class="main-content">
            
            <div class="page-header animate-entry">
                <div class="page-title">
                    <i class="bi bi-plus-circle-dotted me-2"></i> Add New Train
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/admin/manageTrains" class="btn btn-outline-secondary fw-bold">
                        <i class="bi bi-arrow-left me-1"></i> Back to List
                    </a>
                </div>
            </div>

            <%-- Error Message --%>
            <c:if test="${not empty adminErrorMessage}">
                 <div class="alert alert-danger shadow-sm animate-entry">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i> <c:out value="${adminErrorMessage}" />
                </div>
            </c:if>

            <div class="form-card animate-entry" style="animation-delay: 0.1s;">
                
                <form action="${pageContext.request.contextPath}/admin/manageTrains" method="POST">
                    <input type="hidden" name="action" value="add">

                    <h5 class="mb-4 text-muted text-uppercase border-bottom pb-2">Train Details</h5>
                    
                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <label class="form-label">Train Number</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-123"></i></span>
                                <input type="text" name="trainNumber" class="form-control" placeholder="e.g. 12951" required value="<c:out value="${param.trainNumber}"/>">
                            </div>
                        </div>
                        <div class="col-md-8">
                            <label class="form-label">Train Name</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-train-front"></i></span>
                                <input type="text" name="trainName" class="form-control" placeholder="e.g. Rajdhani Express" required value="<c:out value="${param.trainName}"/>">
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Source Station</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                <input type="text" name="source" class="form-control" placeholder="Origin City" required value="<c:out value="${param.source}"/>">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Destination Station</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
                                <input type="text" name="destination" class="form-control" placeholder="Destination City" required value="<c:out value="${param.destination}"/>">
                            </div>
                        </div>
                    </div>

                    <h5 class="mb-3 mt-4 text-muted text-uppercase border-bottom pb-2">Schedule & Capacity</h5>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Departure Time</label>
                            <input type="time" name="departureTime" class="form-control" required value="<c:out value="${param.departureTime}"/>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Arrival Time</label>
                            <input type="time" name="arrivalTime" class="form-control" required value="<c:out value="${param.arrivalTime}"/>">
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <label class="form-label">Total Seats</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-grid-3x3"></i></span>
                                <input type="number" name="totalSeats" class="form-control" min="1" placeholder="Capacity" required value="<c:out value="${param.totalSeats}"/>">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Available Seats</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-check-square"></i></span>
                                <input type="number" name="availableSeats" class="form-control" min="0" placeholder="Initial" required value="<c:out value="${param.availableSeats}"/>">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Base Fare (₹/km)</label>
                            <div class="input-group">
                                <span class="input-group-text">₹</span>
                                <input type="number" step="0.01" name="fare" class="form-control" min="0" placeholder="Rate" required value="<c:out value="${param.fare}"/>">
                            </div>
                        </div>
                    </div>

                    <div class="text-end">
                        <button type="submit" class="btn btn-success fw-bold px-5 py-2 shadow-sm">
                            <i class="bi bi-save me-2"></i> Save Train
                        </button>
                    </div>
                </form>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>