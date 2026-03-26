<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Complete Payment - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <%-- Stripe JS Library --%>
    <script src="https://js.stripe.com/v3/"></script>

    <style>
        /* Header Styles (Same as Seat Selection) */
        .payment-header {
            background-color: var(--irctc-blue);
            color: white;
            padding: 20px 30px;
            margin: -30px -30px 25px -30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 4px solid var(--irctc-orange);
        }
        .header-title { font-weight: 700; font-size: 1.4rem; display: flex; align-items: center; gap: 10px; }
        
        /* Timer Pill */
        .timer-pill {
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.3);
            padding: 6px 15px;
            border-radius: 50px;
            display: flex; align-items: center; gap: 10px;
            font-size: 1rem; backdrop-filter: blur(4px);
        }
        .timer-count { font-weight: 800; font-family: 'Courier New', monospace; font-size: 1.2rem; color: #ffc107; }

        /* Summary Box */
        .booking-summary {
            background-color: #f8f9fa;
            border-left: 5px solid var(--irctc-orange);
            padding: 15px 20px;
            border-radius: 4px;
            margin-bottom: 25px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }

        /* Gateway Options */
        .gateway-option {
            border: 2px solid #eee;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            transition: all 0.2s;
            text-align: center;
            background: #fff;
        }
        .gateway-option:hover { border-color: #ccc; background: #f9f9f9; }
        .gateway-option.active { 
            border-color: var(--irctc-blue); 
            background: #eef5ff; 
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        .gateway-icon { font-size: 2rem; margin-bottom: 5px; display: block; }
        
        /* Sections */
        .payment-section { display: none; animation: fadeIn 0.3s; }
        .payment-section.active { display: block; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

        /* Stripe Specific */
        .stripe-box {
            background: #f7f9fc;
            border: 1px solid #e6e9ef;
            border-radius: 8px;
            padding: 30px;
            text-align: center;
        }
        .btn-stripe {
            background-color: #635bff;
            color: white;
            font-weight: 600;
            padding: 12px 20px;
            border-radius: 4px;
            transition: background 0.2s;
            border: none;
            width: 100%;
            font-size: 1.1rem;
        }
        .btn-stripe:hover { background-color: #4b42e3; color: white; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-4">
        <div class="card-form mx-auto" style="max-width: 600px; overflow: hidden;">
            
            <%-- HEADER --%>
            <div class="payment-header">
                <div class="header-title">
                    <i class="bi bi-credit-card-2-front-fill"></i> Payment
                </div>
                <div class="timer-pill">
                    <i class="bi bi-hourglass-split text-warning"></i>
                    <span>Time Left:</span>
                    <span id="timer" class="timer-count">10:00</span>
                </div>
            </div>

            <c:if test="${empty sessionScope.pendingBookingId}">
                 <div class="alert alert-danger text-center">
                     <i class="bi bi-exclamation-triangle-fill me-2"></i> Session expired.
                     <div class="mt-2"><a href="home.jsp" class="btn btn-sm btn-primary">Back to Home</a></div>
                </div>
            </c:if>

            <c:if test="${not empty sessionScope.pendingBookingId}">
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger"><c:out value="${errorMessage}" /></div>
                </c:if>

                 <%-- SUMMARY --%>
                <div class="booking-summary">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="text-muted small text-uppercase fw-bold">Total Amount</span>
                        <span class="fs-3 fw-bold" style="color: var(--irctc-blue);">
                            <fmt:setLocale value="en_IN"/>
                            <fmt:formatNumber value="${sessionScope.pendingBookingAmount}" type="currency" currencyCode="INR" />
                        </span>
                    </div>
                    <div class="small text-muted">
                        <i class="bi bi-train-front me-1"></i> <c:out value="${sessionScope.pendingTrainName}" />
                    </div>
                </div>

                <%-- 1. GATEWAY SELECTION --%>
                <label class="form-label text-muted fw-bold small mb-3 text-uppercase">Select Payment Method</label>
                <div class="row g-3 mb-4">
                    <div class="col-6">
                        <div class="gateway-option active" onclick="selectGateway('stripe')" id="opt-stripe">
                            <i class="bi bi-stripe gateway-icon" style="color: #635bff;"></i>
                            <div class="fw-bold">Stripe</div>
                            <div class="small text-muted">Credit/Debit Cards</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="gateway-option" onclick="selectGateway('dummy')" id="opt-dummy">
                            <i class="bi bi-wallet2 gateway-icon" style="color: #6c757d;"></i>
                            <div class="fw-bold">Dummy Pay</div>
                            <div class="small text-muted">Test Simulator</div>
                        </div>
                    </div>
                </div>

                <%-- 2. STRIPE SECTION --%>
                <div id="sec-stripe" class="payment-section active">
                    <div class="stripe-box">
                        <i class="bi bi-shield-check text-success fs-1 mb-3"></i>
                        <h5 class="mb-2">Secure Checkout</h5>
                        <p class="text-muted small mb-4">
                            You will be redirected to Stripe's secure payment page to complete your transaction.
                        </p>
                        <button id="stripe-checkout-btn" class="btn-stripe shadow-sm">
                            Pay <fmt:formatNumber value="${sessionScope.pendingBookingAmount}" type="currency" currencyCode="INR" />
                        </button>
                        <div id="stripe-error" class="text-danger small mt-2"></div>
                    </div>
                </div>

                <%-- 3. DUMMY SECTION (The Old Form) --%>
                <div id="sec-dummy" class="payment-section">
                    <form action="${pageContext.request.contextPath}/PaymentServlet" method="POST">
                        <input type="hidden" name="bookingId" value="${sessionScope.pendingBookingId}" />
                        <input type="hidden" name="payerEmail" value="${sessionScope.loggedInUser.email}" />
                        <input type="hidden" name="source" value="${sessionScope.pendingSource}" />
                        <input type="hidden" name="destination" value="${sessionScope.pendingDestination}" />
                        
                        <%-- Seat Data must be passed here too --%>
                        <input type="hidden" name="selectedSeatsData" value="${param.selectedSeatsData}" />

                        <div class="card p-3 bg-light border-0 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="paymentMode" value="CARD" checked>
                                <label class="form-check-label">Dummy Card Payment</label>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-secondary w-100 py-3 fw-bold">
                            Simulate Success
                        </button>
                    </form>
                </div>
            </c:if>
         </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function togglePaymentDetails(method) {
            const cardDetails = document.getElementById('cardDetails');
            const upiDetails = document.getElementById('upiDetails');
            const cardInputs = cardDetails.querySelectorAll('input');
            const upiInput = upiDetails.querySelector('input');

            if (method === 'CARD') {
                cardDetails.style.display = 'block';
                upiDetails.style.display = 'none';
                cardInputs.forEach(input => input.required = true);
                upiInput.required = false;
            } else if (method === 'UPI') {
                cardDetails.style.display = 'none';
                upiDetails.style.display = 'block';
                cardInputs.forEach(input => input.required = false);
                upiInput.required = true;
            }
        }

        // --- STRIPE LOGIC (FIXED) ---
        const stripeBtn = document.getElementById('stripe-checkout-btn');
        if(stripeBtn) {
            stripeBtn.addEventListener('click', function() {
                
                // 1. GET SEAT DATA SAFELY FROM THE DOM
                // We look for the hidden input field that came from the previous page's form submission
                const seatInput = document.querySelector('input[name="selectedSeatsData"]');
                const seatData = seatInput ? seatInput.value : "";
                
                console.log("DEBUG: Sending Seat Data to Stripe ->", seatData); // Check your browser console!

                if (!seatData) {
                    console.warn("Warning: No seat data found. Server will auto-assign seats.");
                }

                // Loading state
                stripeBtn.disabled = true;
                stripeBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';

                fetch('${pageContext.request.contextPath}/StripeSessionServlet', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    // Send seat data securely
                    body: 'selectedSeatsData=' + encodeURIComponent(seatData)
                })
                .then(function(response) {
                    return response.json();
                })
                .then(function(session) {
                    if (session.error) {
                        throw new Error(session.error);
                    }
                    return window.location.href = session.url;
                })
                .catch(function(error) {
                    console.error('Error:', error);
                    document.getElementById('stripe-error').textContent = "Payment Error: " + error.message;
                    stripeBtn.disabled = false;
                    stripeBtn.textContent = "Retry Payment";
                });
            });
        }

        // --- TIMER LOGIC ---
        document.addEventListener('DOMContentLoaded', function() {
            const timerElement = document.getElementById('timer');
            const bookingId = "${sessionScope.pendingBookingId}";
            const STORAGE_KEY = 'irctc_timer_expiry_' + bookingId;
            
            // Check if seat data arrived on the page at all
            const hiddenInput = document.querySelector('input[name="selectedSeatsData"]');
            if (hiddenInput) {
                console.log("DEBUG: Page Loaded with Seat Data: ", hiddenInput.value);
            } else {
                console.error("DEBUG: Hidden input 'selectedSeatsData' NOT FOUND on page!");
            }
            
            let expiryTime = sessionStorage.getItem(STORAGE_KEY);
            if (!expiryTime) {
                const now = new Date().getTime();
                expiryTime = now + (10 * 60 * 1000);
                sessionStorage.setItem(STORAGE_KEY, expiryTime);
            }
            
            const interval = setInterval(() => {
                const now = new Date().getTime();
                const distance = expiryTime - now;
                if (distance < 0) {
                    clearInterval(interval);
                    if(timerElement) timerElement.textContent = "00:00";
                    alert("Session Expired!");
                    window.location.href = "${pageContext.request.contextPath}/jsp/home.jsp";
                    return;
                }
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                const mStr = minutes < 10 ? "0" + minutes : minutes;
                const sStr = seconds < 10 ? "0" + seconds : seconds;
                if(timerElement) {
                    timerElement.textContent = mStr + ":" + sStr;
                    if(minutes < 1) timerElement.classList.add('text-danger');
                }
            }, 1000);
        });
    </script>
</body>
</html>