<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="card-form" style="max-width: 600px; margin: 20px auto;">
        
            <h2><i class="bi bi-person-plus-fill me-2"></i>Create an Account</h2>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger message error-message" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <form id="registerForm" action="${pageContext.request.contextPath}/RegisterServlet" method="POST">
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="fullName" class="form-label">Full Name</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person-vcard-fill"></i></span>
                            <input type="text" id="fullName" name="fullName" class="form-control" required value="<c:out value='${param.fullName}'/>" placeholder="First and Last Name ">
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="phone" class="form-label">Phone Number</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-telephone-fill"></i></span>
                            <input type="tel" id="phone" name="phone" class="form-control" required value="<c:out value='${param.phone}'/>" placeholder="10-digit number">
                        </div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope-fill"></i></span>
                        <input type="email" id="email" name="email" class="form-control" required value="<c:out value='${param.email}'/>" placeholder="name@example.com">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="password" class="form-label">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                            <input type="password" id="password" name="password" class="form-control" required placeholder="Min 6 characters">
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="confirmPassword" class="form-label">Confirm Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required placeholder="Re-enter password">
                        </div>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="gender" class="form-label">Gender</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-gender-ambiguous"></i></span>
                        <select id="gender" name="gender" class="form-select" required>
                            <option value="">-- Select Gender --</option>
                            <option value="MALE" ${param.gender == 'MALE' ? 'selected' : ''}>Male</option>
                            <option value="FEMALE" ${param.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                            <option value="OTHER" ${param.gender == 'OTHER' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="address" class="form-label">Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-house-fill"></i></span>
                        <textarea id="address" name="address" class="form-control" rows="3" placeholder="Enter your full address" required><c:out value='${param.address}'/></textarea>
                    </div>
                </div>
				<%-- NEW: Verification Method Selection --%>
                <div class="mb-4">
                    <label class="form-label d-block">Select Verification Method:</label>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="verificationMethod" id="verifyOTP" value="OTP" checked>
                        <label class="form-check-label" for="verifyOTP">
                            <i class="bi bi-shield-lock-fill me-1"></i> Send OTP
                        </label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="verificationMethod" id="verifyLink" value="LINK">
                        <label class="form-check-label" for="verifyLink">
                            <i class="bi bi-link-45deg me-1"></i> Send Email Link
                        </label>
                    </div>
                </div>
                <%-- END NEW --%>
                <button type="submit" class="btn btn-orange w-100">
                    <i class="bi bi-person-check-fill me-2"></i> Register
                </button>
            </form>

            <div class="text-center mt-3">
                Already have an account?
                <a href="login.jsp" style="color: var(--irctc-orange); font-weight: bold; text-decoration: none;">
                    Login here
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>

</body>
</html>