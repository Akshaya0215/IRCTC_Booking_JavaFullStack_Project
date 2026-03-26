<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Bookings - Admin</title>
    
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
                    <i class="bi bi-ticket-perforated-fill me-2"></i> Global Booking Log
                </div>
                <div>
                    <button class="btn btn-light border shadow-sm fw-bold" onclick="window.print()">
                        <i class="bi bi-printer me-1"></i> Print Report
                    </button>
                </div>
            </div>

            <%-- Success/Error Messages --%>
            <c:if test="${not empty adminErrorMessage}">
                 <div class="alert alert-danger shadow-sm animate-entry">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${adminErrorMessage}" />
                </div>
            </c:if>

            <div class="admin-table-container animate-entry" style="animation-delay: 0.1s;">
                <c:choose>
                    <c:when test="${not empty bookingList}">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>PNR</th>
                                    <th>User Email</th>
                                    <th>Train Info</th>
                                    <th>Route</th>
                                    <th>Date</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th class="text-end">God Mode</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="booking" items="${bookingList}">
                                    <tr>
                                        <%-- PNR --%>
                                        <td class="fw-bold text-primary">
                                            #<c:out value="${booking.bookingId}" />
                                        </td>
                                        
                                        <%-- User --%>
                                        <td class="text-muted small">
                                            <i class="bi bi-person me-1"></i> <c:out value="${booking.user.email}" />
                                        </td>
                                        
                                        <%-- Train --%>
                                        <td class="fw-bold text-dark">
                                            <c:out value="${booking.train.trainNumber}" />
                                        </td>
                                        
                                        <%-- Route --%>
                                        <td class="small">
                                            <div class="d-flex align-items-center">
                                                <span class="text-uppercase fw-bold"><c:out value="${booking.segmentSource}"/></span>
                                                <i class="bi bi-arrow-right mx-2 text-muted"></i>
                                                <span class="text-uppercase fw-bold"><c:out value="${booking.segmentDestination}"/></span>
                                            </div>
                                        </td>
                                        
                                        <%-- Date --%>
                                        <td class="text-muted small">
                                            <fmt:formatDate value="${booking.journeyDate}" pattern="dd-MMM" />
                                        </td>
                                        
                                        <%-- Amount --%>
                                        <td class="fw-bold text-dark">
                                            ₹<fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0" />
                                        </td>
                                        
                                        <%-- Status Badge --%>
                                        <td>
                                            <c:choose>
                                                <c:when test="${booking.status == 'CONFIRMED'}">
                                                    <span class="badge bg-success">CONFIRMED</span>
                                                </c:when>
                                                <c:when test="${booking.status == 'CANCELLED'}">
                                                    <span class="badge bg-danger">CANCELLED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark"><c:out value="${booking.status}" /></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        
                                        <%-- Admin Actions --%>
                                        <td class="text-end">
                                            <c:if test="${booking.status == 'CONFIRMED'}">
                                                <form action="${pageContext.request.contextPath}/AdminActionServlet" method="POST" 
                                                      onsubmit="return confirm('⚠️ FORCE CANCEL PNR ${booking.bookingId}?\n\nThis will bypass normal cancellation rules.\nProceed?');">
                                                    <input type="hidden" name="action" value="FORCE_CANCEL_BOOKING">
                                                    <input type="hidden" name="id" value="${booking.bookingId}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger fw-bold" style="font-size: 0.7rem;">
                                                        <i class="bi bi-x-circle-fill me-1"></i> Force Cancel
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${booking.status == 'CANCELLED'}">
                                                <span class="text-muted small fst-italic">--</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center p-5">
                            <i class="bi bi-journal-x text-muted display-4"></i>
                            <p class="mt-3 text-muted">No bookings found in system records.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>