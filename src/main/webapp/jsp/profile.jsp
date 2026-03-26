<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        .profile-header-icon {
            font-size: 4rem;
            color: var(--irctc-blue);
        }
        .verified-badge {
            color: #198754; /* Success Green */
            font-weight: bold;
            font-size: 0.9rem;
            background: #d1e7dd;
            padding: 2px 8px;
            border-radius: 12px;
            border: 1px solid #badbcc;
        }
        .unverified-badge {
            color: #dc3545; /* Danger Red */
            font-weight: bold;
            font-size: 0.9rem;
            background: #f8d7da;
            padding: 2px 8px;
            border-radius: 12px;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        
        <c:if test="${empty sessionScope.loggedInUser}">
            <c:redirect url="login.jsp" />
        </c:if>

        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card-form" style="max-width: 100%;">
                    
                    <div class="text-center mb-4">
                        <i class="bi bi-person-circle profile-header-icon"></i>
                        <h2 class="mt-2">My Profile</h2>
                        <p class="text-muted">Manage your personal details</p>
                    </div>

                    <%-- Messages --%>
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success message success-message">
                            <i class="bi bi-check-circle-fill me-2"></i> ${sessionScope.successMessage}
                        </div>
                        <% session.removeAttribute("successMessage"); %>
                    </c:if>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger message error-message">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i> ${sessionScope.errorMessage}
                        </div>
                        <% session.removeAttribute("errorMessage"); %>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/UpdateProfileServlet" method="POST">
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label fw-bold">User ID</label>
                                <input type="text" class="form-control" value="${sessionScope.loggedInUser.userId}" disabled readonly>
                            </div>
                            <div class="col-md-8 mb-3">
                                <label class="form-label fw-bold">Account Status</label>
                                <div class="form-control bg-light d-flex align-items-center">
                                    <c:choose>
                                        <c:when test="${sessionScope.loggedInUser.verified}">
                                            <span class="verified-badge"><i class="bi bi-patch-check-fill me-1"></i> Verified</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="unverified-badge"><i class="bi bi-exclamation-circle-fill me-1"></i> Not Verified</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="ms-auto text-muted small">
                                        Joined: <fmt:formatDate value="${sessionScope.loggedInUser.createdAt}" pattern="dd-MMM-yyyy" />
                                    </span>
                                </div>
                            </div>
                        </div>

                        <hr>

                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person-vcard-fill"></i></span>
                                <input type="text" id="fullName" name="fullName" class="form-control" value="<c:out value='${sessionScope.loggedInUser.fullName}'/>" required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Email Address <span class="text-muted small">(Cannot be changed)</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-envelope-fill text-muted"></i></span>
                                    <input type="email" class="form-control bg-light" value="<c:out value='${sessionScope.loggedInUser.email}'/>" disabled readonly>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="phone" class="form-label">Phone Number</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-telephone-fill"></i></span>
                                    <input type="tel" id="phone" name="phone" class="form-control" value="<c:out value='${sessionScope.loggedInUser.phone}'/>" required>
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="address" class="form-label">Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-house-fill"></i></span>
                                <textarea id="address" name="address" class="form-control" rows="2" required><c:out value='${sessionScope.loggedInUser.address}'/></textarea>
                            </div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/jsp/home.jsp" class="btn btn-secondary me-md-2">Cancel</a>
                            <button type="submit" class="btn btn-orange">
                                <i class="bi bi-floppy-fill me-2"></i> Save Changes
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>