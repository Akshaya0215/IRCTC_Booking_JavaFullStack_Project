<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Trains - Admin</title>
    
    <%-- 1. Load Bootstrap & Icons --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <%-- 2. Load New Admin Theme --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">
</head>
<body>

    <%-- Security Check --%>
    <c:if test="${empty sessionScope.loggedInAdmin}">
        <c:redirect url="${pageContext.request.contextPath}/admin/adminLogin.jsp" />
    </c:if>

    <div class="admin-wrapper">
        
        <%-- 3. Include Sidebar --%>
        <jsp:include page="adminNavbar.jsp" />

        <%-- 4. Main Content --%>
        <div class="main-content">
            
            <div class="page-header animate-entry">
                <div class="page-title">
                    <i class="bi bi-train-front-fill me-2"></i> Train Operations
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/admin/manageTrains?action=add" class="btn btn-primary fw-bold shadow-sm">
                        <i class="bi bi-plus-lg me-1"></i> Add New Train
                    </a>
                </div>
            </div>

            <%-- Success/Error Messages --%>
            <c:if test="${not empty sessionScope.adminSuccessMessage}">
                <div class="alert alert-success shadow-sm animate-entry">
                    <i class="bi bi-check-circle-fill me-2"></i> <c:out value="${sessionScope.adminSuccessMessage}" />
                </div>
                <% session.removeAttribute("adminSuccessMessage"); %>
            </c:if>
            
            <c:if test="${not empty adminErrorMessage}">
                 <div class="alert alert-danger shadow-sm animate-entry">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i> <c:out value="${adminErrorMessage}" />
                </div>
            </c:if>

            <div class="admin-table-container animate-entry" style="animation-delay: 0.1s;">
                <c:choose>
                    <c:when test="${not empty trainList}">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Train No</th>
                                    <th>Name & Route</th>
                                    <th>Schedule</th>
                                    <th>Capacity</th>
                                    <th>Base Fare</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="train" items="${trainList}">
                                    <tr>
                                        <td class="fw-bold text-primary">#<c:out value="${train.trainNumber}" /></td>
                                        
                                        <td>
                                            <div class="fw-bold text-dark" style="font-size: 1rem;"><c:out value="${train.trainName}" /></div>
                                            <div class="small text-muted">
                                                <i class="bi bi-geo-alt-fill text-secondary"></i> 
                                                <c:out value="${train.source}" /> &rarr; <c:out value="${train.destination}" />
                                            </div>
                                        </td>
                                        
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <span class="badge bg-light text-dark border">
                                                    Dep: <fmt:formatDate value="${train.departureTime}" pattern="HH:mm" />
                                                </span>
                                                <i class="bi bi-arrow-right text-muted small"></i>
                                                <span class="badge bg-light text-dark border">
                                                    Arr: <fmt:formatDate value="${train.arrivalTime}" pattern="HH:mm" />
                                                </span>
                                            </div>
                                        </td>
                                        
                                        <td>
                                            <%-- Dynamic Badge based on Availability --%>
                                            <c:choose>
                                                <c:when test="${train.availableSeats == 0}">
                                                    <span class="badge bg-danger">FULL</span>
                                                </c:when>
                                                <c:when test="${train.availableSeats < 20}">
                                                    <span class="badge bg-warning text-dark">FAST FILLING</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-success">AVAILABLE</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="small text-muted mt-1">
                                                <strong>${train.availableSeats}</strong> / ${train.totalSeats} seats
                                            </div>
                                        </td>
                                        
                                        <td class="fw-bold text-dark">
                                            ₹<fmt:formatNumber value="${train.farePerKm}" pattern="0.00" /> <span class="small text-muted fw-normal">/km</span>
                                        </td>
                                        
                                        <td class="text-end">
                                            <%-- Edit Button --%>
                                            <a href="${pageContext.request.contextPath}/admin/manageTrains?action=edit&id=${train.trainId}" 
                                               class="btn-action-icon btn-icon-edit" title="Edit Train">
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            
                                            <%-- Delete Button (God Mode) --%>
                                            <form action="${pageContext.request.contextPath}/AdminActionServlet" method="POST" style="display:inline;" 
                                                  onsubmit="return confirm('⚠️ WARNING ⚠️\n\nDeleting train ${train.trainNumber} is permanent.\nIt may affect historical booking data.\n\nProceed?');">
                                                <input type="hidden" name="action" value="DELETE_TRAIN">
                                                <input type="hidden" name="id" value="${train.trainId}">
                                                <button type="submit" class="btn-action-icon btn-icon-delete" title="Delete Train">
                                                    <i class="bi bi-trash-fill"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center p-5">
                            <i class="bi bi-train-lightrail-front text-muted display-4"></i>
                            <p class="mt-3 text-muted">No trains found in the fleet.</p>
                            <a href="${pageContext.request.contextPath}/admin/manageTrains?action=add" class="btn btn-outline-primary btn-sm">Add First Train</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>