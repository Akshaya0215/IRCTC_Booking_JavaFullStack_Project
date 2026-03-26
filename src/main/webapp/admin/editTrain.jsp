<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Train - Admin</title>
    
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
            border-top: 5px solid #36b9cc; /* Cyan for Edit Mode */
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
            
            <c:if test="${empty train}">
                <div class="alert alert-warning shadow-sm m-4">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i> Train details not found.
                    <a href="${pageContext.request.contextPath}/admin/manageTrains" class="alert-link">Return to List</a>.
                </div>
                <c:set var="stopRendering" value="true" scope="request"/>
            </c:if>

            <c:if test="${!stopRendering}">
                <div class="page-header animate-entry">
                    <div class="page-title">
                        <i class="bi bi-pencil-square me-2"></i> Edit Train <span class="text-muted">#<c:out value="${train.trainNumber}"/></span>
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
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" name="trainId" value="<c:out value="${train.trainId}"/>">

                        <h5 class="mb-4 text-muted text-uppercase border-bottom pb-2">Core Details</h5>
                        
                        <div class="row g-3 mb-3">
                            <div class="col-md-4">
                                <label class="form-label">Train Number</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-hash"></i></span>
                                    <input type="text" name="trainNumber" class="form-control" required value="<c:out value="${train.trainNumber}"/>">
                                </div>
                            </div>
                            <div class="col-md-8">
                                <label class="form-label">Train Name</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-train-lightrail-front"></i></span>
                                    <input type="text" name="trainName" class="form-control" required value="<c:out value="${train.trainName}"/>">
                                </div>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Source Station</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                    <input type="text" name="source" class="form-control" required value="<c:out value="${train.source}"/>">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Destination Station</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
                                    <input type="text" name="destination" class="form-control" required value="<c:out value="${train.destination}"/>">
                                </div>
                            </div>
                        </div>

                        <h5 class="mb-3 mt-4 text-muted text-uppercase border-bottom pb-2">Operational Config</h5>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Departure Time</label>
                                <fmt:formatDate value="${train.departureTime}" pattern="HH:mm" var="depTime"/>
                                <input type="time" name="departureTime" class="form-control" required value="${depTime}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Arrival Time</label>
                                <fmt:formatDate value="${train.arrivalTime}" pattern="HH:mm" var="arrTime"/>
                                <input type="time" name="arrivalTime" class="form-control" required value="${arrTime}">
                            </div>
                        </div>

                        <div class="row g-3 mb-4">
                            <div class="col-md-4">
                                <label class="form-label">Total Seats</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-grid-3x3"></i></span>
                                    <input type="number" name="totalSeats" class="form-control" min="1" required value="<c:out value="${train.totalSeats}"/>">
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Available Seats</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-check2-square"></i></span>
                                    <input type="number" name="availableSeats" class="form-control" min="0" required value="<c:out value="${train.availableSeats}"/>">
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Base Fare (₹/km)</label>
                                <div class="input-group">
                                    <span class="input-group-text">₹</span>
                                    <fmt:formatNumber value="${train.farePerKm}" pattern="0.00" var="fareValue"/>
                                    <input type="number" step="0.01" name="fare" class="form-control" min="0" required value="${fareValue}">
                                </div>
                            </div>
                        </div>

                        <div class="text-end">
                            <button type="submit" class="btn btn-info text-white fw-bold px-5 py-2 shadow-sm" style="background-color: #36b9cc; border-color: #36b9cc;">
                                <i class="bi bi-check2-circle me-2"></i> Update Changes
                            </button>
                        </div>
                    </form>
                </div>
            </c:if>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>