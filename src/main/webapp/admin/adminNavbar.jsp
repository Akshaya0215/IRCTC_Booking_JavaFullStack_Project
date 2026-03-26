<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<nav class="sidebar">
    <div class="sidebar-brand">
        <img src="${pageContext.request.contextPath}/assets/images/irctc.png" 
             alt="Logo" 
             style="height: 35px; background: white; padding: 2px; border-radius: 4px; margin-right: 10px;">
        <span>IRCTC ADMIN</span>
    </div>

    <ul class="sidebar-menu">
        <li>
            <%-- FIXED LINK: Removed .jsp extension --%>
            <a href="${pageContext.request.contextPath}/admin/dashboard" 
               class="${fn:endsWith(pageContext.request.requestURI, 'dashboard.jsp') ? 'active' : ''}">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
        </li>
        
        <li>
            <a href="${pageContext.request.contextPath}/admin/manageTrains"
               class="${fn:contains(pageContext.request.requestURI, 'manageTrains') ? 'active' : ''}">
                <i class="bi bi-train-front-fill"></i> Manage Trains
            </a>
        </li>
        
        <li>
            <a href="${pageContext.request.contextPath}/admin/manageUsers"
               class="${fn:contains(pageContext.request.requestURI, 'manageUsers') ? 'active' : ''}">
                <i class="bi bi-people-fill"></i> Manage Users
            </a>
        </li>
        
        <li>
            <a href="${pageContext.request.contextPath}/admin/viewBookings"
               class="${fn:contains(pageContext.request.requestURI, 'viewBookings') ? 'active' : ''}">
                <i class="bi bi-ticket-perforated-fill"></i> All Bookings
            </a>
        </li>

        <li style="margin-top: 20px; border-top: 1px solid rgba(255,255,255,0.1);">
            <a href="${pageContext.request.contextPath}/AdminLogoutServlet" style="color: #ff6b6b;">
                <i class="bi bi-power"></i> Logout
            </a>
        </li>
    </ul>
    
    <div style="padding: 20px; font-size: 0.85rem; color: rgba(255,255,255,0.5); text-align: center; margin-top: auto;">
        Logged in as<br>
        <strong style="color: white;"><c:out value="${sessionScope.loggedInAdmin.fullName}" /></strong>
    </div>
</nav>