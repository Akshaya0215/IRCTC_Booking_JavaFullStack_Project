<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verify OTP - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="card-form mx-auto" style="max-width: 400px; text-align: center;">
            
            <div class="mb-3">
                <i class="bi bi-shield-lock-fill text-primary" style="font-size: 3rem;"></i>
            </div>

            <h2>Verify Account</h2>
            <p class="text-muted mb-4">
                We have sent a 6-digit code to<br>
                <strong>${sessionScope.registeringEmail}</strong>
            </p>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger py-2 mb-3">${errorMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/VerifyOtpServlet" method="POST">
                <div class="mb-4">
                    <input type="text" name="otp" class="form-control form-control-lg text-center" 
                           placeholder="Enter 6-digit OTP" maxlength="6" required 
                           style="letter-spacing: 5px; font-weight: bold; font-size: 1.5rem;">
                </div>
                
                <button type="submit" class="btn btn-orange w-100 py-2">Verify & Login</button>
            </form>
            
            <div class="mt-3">
                <small class="text-muted">Did not receive code? <a href="#">Resend</a></small>
            </div>
        </div>
    </div>

</body>
</html>