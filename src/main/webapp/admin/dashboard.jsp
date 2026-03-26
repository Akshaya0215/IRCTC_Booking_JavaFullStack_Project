<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- THIS TAGLIB IS REQUIRED FOR NUMBER FORMATTING --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - IRCTC Clone</title>
    
    <%-- Bootstrap & Icons --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <%-- Admin Theme --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">
</head>
<body>

    <%-- Security Check --%>
    <c:if test="${empty sessionScope.loggedInAdmin}">
        <c:redirect url="${pageContext.request.contextPath}/admin/adminLogin.jsp" />
    </c:if>

    <div class="admin-wrapper">
        
        <%-- Sidebar --%>
        <jsp:include page="adminNavbar.jsp" />

        <%-- Main Content --%>
        <div class="main-content">
            
            <div class="page-header animate-entry">
                <div class="page-title">
                    <i class="bi bi-speedometer2 me-2"></i> Dashboard Overview
                </div>
                <div class="text-muted">
                    System Status: <span class="badge bg-success">ONLINE</span>
                </div>
            </div>

            <%-- 1. PULSE STATS WIDGETS --%>
            <div class="row g-4 mb-5 animate-entry" style="animation-delay: 0.1s;">
                
                <%-- Revenue Card --%>
                <div class="col-md-3">
                    <div class="stat-card card-revenue">
                        <div class="stat-val">
                            <small style="font-size:1rem">₹</small> 
                            <fmt:formatNumber value="${stat_revenue}" pattern="#,##0" />
                        </div>
                        <div class="stat-label">Total Revenue</div>
                        <i class="bi bi-currency-rupee stat-icon"></i>
                    </div>
                </div>

                <%-- Users Card --%>
                <div class="col-md-3">
                    <div class="stat-card card-users">
                        <div class="stat-val">${stat_users}</div>
                        <div class="stat-label">Registered Users</div>
                        <i class="bi bi-people-fill stat-icon"></i>
                    </div>
                </div>

                <%-- Blocked Users Card --%>
                <div class="col-md-3">
                    <div class="stat-card card-blocked">
                        <div class="stat-val text-danger">${stat_blocked}</div>
                        <div class="stat-label">Blocked Accounts</div>
                        <i class="bi bi-slash-circle-fill stat-icon"></i>
                    </div>
                </div>

                <%-- Active Trains Card --%>
                <div class="col-md-3">
                    <div class="stat-card card-trains">
                        <div class="stat-val text-warning">${stat_trains}</div>
                        <div class="stat-label">Active Trains</div>
                        <i class="bi bi-train-front-fill stat-icon"></i>
                    </div>
                </div>
            </div>

            <%-- 2. GOD MODE & SHORTCUTS --%>
            <div class="row animate-entry" style="animation-delay: 0.2s;">
                
                <%-- Quick Actions --%>
                <div class="col-md-5">
                    <div class="admin-table-container h-100">
                        <h5 class="mb-4 fw-bold text-dark">
                            <i class="bi bi-lightning-charge-fill text-warning me-2"></i> God Mode Actions
                        </h5>
                        
                        <form action="${pageContext.request.contextPath}/AdminActionServlet" method="POST" class="mb-4">
                            <label class="form-label small text-muted text-uppercase fw-bold">Emergency Override</label>
                            <div class="input-group mb-3">
                                <input type="text" name="id" class="form-control" placeholder="Enter ID (User/PNR)" required>
                                <select name="action" class="form-select" style="max-width: 140px; font-weight:600;">
                                    <option value="BLOCK_USER">Block User</option>
                                    <option value="FORCE_CANCEL_BOOKING">Cancel PNR</option>
                                </select>
                                <button type="submit" class="btn btn-danger fw-bold">EXECUTE</button>
                            </div>
                        </form>
                        
                        <div class="alert alert-light border small text-muted">
                            <i class="bi bi-info-circle me-1"></i> 
                            <strong>Note:</strong> "Force Cancel" bypasses all refund rules. "Block User" prevents future logins.
                        </div>
                    </div>
                </div>

                <%-- Shortcuts --%>
                <div class="col-md-7">
                    <div class="admin-table-container h-100">
                        <h5 class="mb-4 fw-bold text-dark">System Shortcuts</h5>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <a href="manageTrains?action=add" class="btn btn-outline-primary w-100 p-3 text-start fw-bold">
                                    <i class="bi bi-plus-circle-fill me-2"></i> Add New Train
                                </a>
                            </div>
                            <div class="col-md-6">
                                <a href="manageUsers" class="btn btn-outline-dark w-100 p-3 text-start fw-bold">
                                    <i class="bi bi-person-gear me-2"></i> Manage Users
                                </a>
                            </div>
                            <div class="col-md-6">
                                <a href="viewBookings" class="btn btn-outline-success w-100 p-3 text-start fw-bold">
                                    <i class="bi bi-ticket-detailed-fill me-2"></i> View Booking Logs
                                </a>
                            </div>
                            <div class="col-md-6">
                                <a href="${pageContext.request.contextPath}/" target="_blank" class="btn btn-outline-secondary w-100 p-3 text-start fw-bold">
                                    <i class="bi bi-box-arrow-up-right me-2"></i> Open User Site
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>