<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Portal - Login</title>
    
    <%-- Bootstrap 5 & Icons --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <style>
        :root {
            --irctc-blue: #003366;
            --irctc-orange: #fb792b;
        }

        body {
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: var(--irctc-blue);
            font-family: 'Segoe UI', system-ui, sans-serif;
            margin: 0;
            overflow: hidden;
            
            /* "Blueprint" Grid Background Pattern */
            background-image: 
                linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
                linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
            background-size: 30px 30px;
        }

        /* Animated Glow in Background */
        body::before {
            content: '';
            position: absolute;
            width: 120%;
            height: 120%;
            background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 60%);
            z-index: -1;
            animation: pulseGlow 5s infinite alternate;
        }

        @keyframes pulseGlow {
            0% { opacity: 0.5; transform: scale(1); }
            100% { opacity: 0.8; transform: scale(1.05); }
        }

        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 16px;
            padding: 40px;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
            text-align: center;
            border-top: 5px solid var(--irctc-orange);
            position: relative;
            animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .logo-wrapper {
            background: white;
            width: 90px;
            height: 90px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: -85px auto 20px auto; /* Pulls logo up outside the card */
            box-shadow: 0 5px 15px rgba(0,0,0,0.15);
            border: 4px solid var(--irctc-blue);
        }

        .logo-img {
            height: 60px;
            width: 60px;
            object-fit: contain;
        }

        .login-title {
            font-weight: 800;
            color: var(--irctc-blue);
            font-size: 1.8rem;
            margin-bottom: 5px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .login-subtitle {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 30px;
            font-weight: 500;
        }

        .input-group-text {
            background: transparent;
            border-right: none;
            color: #888;
        }

        .form-control {
            border-left: none;
            padding: 12px 0;
            background: transparent;
        }
        
        .form-control:focus {
            box-shadow: none;
            border-color: #ced4da;
        }

        .input-box {
            border: 1px solid #ced4da;
            border-radius: 8px;
            padding: 0 10px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            transition: all 0.3s;
            background: white;
        }

        .input-box:focus-within {
            border-color: var(--irctc-blue);
            box-shadow: 0 0 0 4px rgba(0, 51, 102, 0.1);
        }

        .input-box i {
            color: #aaa;
            font-size: 1.2rem;
            margin-right: 10px;
        }

        .btn-login {
            background: var(--irctc-blue);
            color: white;
            border: none;
            padding: 14px;
            font-weight: 700;
            width: 100%;
            border-radius: 8px;
            font-size: 1rem;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-login:hover {
            background: #004080;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 51, 102, 0.3);
        }

        .alert-custom {
            font-size: 0.85rem;
            text-align: left;
            border: none;
            background: #ffe5e5;
            color: #d63384;
            display: flex;
            align-items: center;
            gap: 10px;
        }
    </style>
</head>
<body>

    <div class="login-card">
        
        <%-- FLOATING CIRCLE LOGO --%>
        <div class="logo-wrapper">
            <img src="${pageContext.request.contextPath}/assets/images/ir.png" alt="Indian Railways" class="logo-img">
        </div>

        <div class="login-title">Control Room</div>
        <div class="login-subtitle">System Administrator Access</div>

        <%-- Error Message --%>
        <c:if test="${not empty adminErrorMessage}">
            <div class="alert alert-danger alert-custom mb-4">
                <i class="bi bi-shield-slash-fill fs-5"></i>
                <div><c:out value="${adminErrorMessage}" /></div>
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
             <div class="alert alert-warning alert-custom mb-4" style="background: #fff3cd; color: #856404;">
                <i class="bi bi-clock-history fs-5"></i>
                <div>Session expired. Please login again.</div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/adminLogin" method="POST">
            
            <div class="input-box">
                <i class="bi bi-person"></i>
                <input type="text" name="username" class="form-control" placeholder="Admin ID" required autocomplete="off">
            </div>

            <div class="input-box">
                <i class="bi bi-key"></i>
                <input type="password" name="password" class="form-control" placeholder="Secure Password" required>
            </div>

            <button type="submit" class="btn-login">
                Authenticate <i class="bi bi-box-arrow-in-right ms-2"></i>
            </button>
        </form>
        
        <div class="mt-4 pt-3 border-top text-muted small">
            <i class="bi bi-lock-fill me-1"></i> Restricted Area. Authorized Personnel Only.
        </div>
    </div>

</body>
</html>