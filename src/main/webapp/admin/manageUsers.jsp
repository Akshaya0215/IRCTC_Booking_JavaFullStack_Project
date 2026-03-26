<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Users - Admin</title>
    
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
                    <i class="bi bi-people-fill me-2"></i> User Management
                </div>
                <div class="text-muted">
                    Total Users: <strong>${userList.size()}</strong>
                </div>
            </div>

            <%-- Alerts --%>
            <c:if test="${not empty adminErrorMessage}">
                 <div class="alert alert-danger shadow-sm mb-4">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i> <c:out value="${adminErrorMessage}" />
                </div>
            </c:if>

            <div class="admin-table-container animate-entry" style="animation-delay: 0.1s;">
                
                <c:choose>
                    <c:when test="${not empty userList}">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>User Details</th>
                                    <th>Contact Info</th>
                                    <th>Status</th>
                                    <th>Joined On</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="user" items="${userList}">
                                    <tr style="${user.blocked ? 'border-left: 4px solid #e74a3b;' : ''}">
                                        <td class="fw-bold text-secondary">#<c:out value="${user.userId}" /></td>
                                        
                                        <td>
                                            <div class="fw-bold text-dark"><c:out value="${user.fullName}" /></div>
                                            <div class="small text-muted"><c:out value="${user.gender}" />, Age: <c:out value="${user.age}" /></div>
                                        </td>
                                        
                                        <td>
                                            <div class="text-dark"><i class="bi bi-envelope me-1"></i> <c:out value="${user.email}" /></div>
                                            <div class="small text-muted"><i class="bi bi-telephone me-1"></i> <c:out value="${user.phone}" /></div>
                                        </td>
                                        
                                        <td>
                                            <c:choose>
                                                <c:when test="${user.blocked}">
                                                    <span class="badge bg-danger">BLOCKED</span>
                                                </c:when>
                                                <c:when test="${user.verified}">
                                                    <span class="badge bg-success">VERIFIED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark">PENDING</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        
                                        <td class="text-muted small">
                                            <fmt:formatDate value="${user.createdAt}" pattern="dd MMM yyyy" />
                                        </td>
                                        
                                        <td class="text-end">
                                            <%-- BLOCK / UNBLOCK ACTION --%>
                                            <form action="${pageContext.request.contextPath}/AdminActionServlet" method="POST" style="display:inline;">
                                                <input type="hidden" name="id" value="${user.userId}">
                                                
                                                <c:choose>
                                                    <c:when test="${user.blocked}">
                                                        <input type="hidden" name="action" value="UNBLOCK_USER">
                                                        <button type="submit" class="btn-action-icon btn-icon-unblock" title="Unblock User">
                                                            <i class="bi bi-unlock-fill"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="hidden" name="action" value="BLOCK_USER">
                                                        <button type="submit" class="btn-action-icon btn-icon-block" title="Block User">
                                                            <i class="bi bi-lock-fill"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>

                                            <%-- DELETE ACTION --%>
                                            <form action="${pageContext.request.contextPath}/AdminActionServlet" method="POST" style="display:inline;" onsubmit="return confirm('⚠️ NUCLEAR OPTION ⚠️\n\nDeleting User #${user.userId} will WIPE:\n- Their Account\n- All Bookings\n- All Payments\n\nAre you absolutely sure?');">
                                                <input type="hidden" name="action" value="DELETE_USER">
                                                <input type="hidden" name="id" value="${user.userId}">
                                                <button type="submit" class="btn-action-icon btn-icon-delete" title="Delete User">
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
                            <i class="bi bi-people text-muted display-4"></i>
                            <p class="mt-3 text-muted">No users found in the database.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>