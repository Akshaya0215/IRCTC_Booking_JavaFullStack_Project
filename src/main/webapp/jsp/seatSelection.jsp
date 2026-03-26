<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Select Seats - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/seatmap.css">
    
    <style>
        /* Integrated Header Style */
        .seat-selection-header {
            background-color: var(--irctc-blue);
            color: white;
            padding: 20px 30px;
            margin: -30px -30px 25px -30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        
        .header-title {
            margin: 0;
            font-weight: 700;
            font-size: 1.5rem;
            letter-spacing: 0.5px;
        }

        /* Timer Pill */
        .timer-pill {
            background: rgba(255,255,255,0.15);
            border: 1px solid rgba(255,255,255,0.3);
            padding: 8px 16px;
            border-radius: 30px;
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 1rem;
            backdrop-filter: blur(5px);
        }
        
        .timer-count {
            font-weight: 800;
            font-family: 'Courier New', monospace;
            font-size: 1.2rem;
            color: #fff;
        }
        
        /* Train Info Box */
        .train-info-box {
            background-color: #f8f9fa;
            border-left: 5px solid var(--irctc-orange);
            padding: 15px 20px;
            margin-bottom: 25px;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        
        /* Legend Refinement */
        .legend-item { font-size: 0.85rem; color: #555; font-weight: 600; }
        .legend-box { width: 18px; height: 18px; border-radius: 4px; display: inline-block; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-4">
        
        <div class="card-form mx-auto" style="max-width: 900px; overflow: hidden;">
            
            <div class="seat-selection-header">
                <div class="header-title">
                    <i class="bi bi-grid-3x3-gap-fill me-2"></i> Select Your Seats
                </div>
                
                <div class="timer-pill">
                    <i class="bi bi-stopwatch-fill text-warning"></i>
                    <span>Time Left:</span>
                    <span id="timer" class="timer-count text-warning">10:00</span>
                </div>
            </div>

            <div class="train-info-box">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <h5 class="mb-1 text-dark"><c:out value="${sessionScope.pendingTrainName}" /></h5>
                        <span class="text-muted small">CLASS: <strong><c:out value="${sessionScope.pendingClassCode}" /></strong></span>
                    </div>
                    <div class="col-md-6 text-md-end mt-2 mt-md-0">
                        <span class="text-muted">Passengers:</span>
                        <span class="fs-5 fw-bold text-primary ms-2"><c:out value="${sessionScope.pendingPassengerCount}" /></span>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-center gap-4 mb-4">
                <div class="d-flex align-items-center gap-2 legend-item">
                    <div class="legend-box" style="border: 1px solid var(--irctc-blue); background: white;"></div> Available
                </div>
                <div class="d-flex align-items-center gap-2 legend-item">
                    <div class="legend-box" style="background-color: #e0e0e0; border: 1px solid #ccc;"></div> Booked
                </div>
                <div class="d-flex align-items-center gap-2 legend-item">
                    <div class="legend-box" style="background-color: var(--irctc-orange); border: 1px solid var(--irctc-orange);"></div> Selected
                </div>
            </div>

            <div class="d-flex justify-content-center">
                <div id="seat-map-container" class="coach-container">
                    <div class="text-center p-5 text-muted">Loading Coach Layout...</div>
                </div>
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <span class="text-muted small text-uppercase fw-bold">Selected Seats</span><br>
                    <span id="selected-seats-display" class="fs-5 fw-bold text-primary">None</span>
                </div>
                
                <form action="${pageContext.request.contextPath}/jsp/payment.jsp" method="POST" class="d-flex gap-2">
                    <input type="hidden" id="selectedSeatsData" name="selectedSeatsData">
                    
                    <button type="submit" class="btn btn-outline-secondary" onclick="document.getElementById('selectedSeatsData').value=''">
                        Skip & Auto-Assign
                    </button>
                    
                    <button type="submit" id="confirm-seats-btn" class="btn btn-orange px-4" disabled>
                        Confirm & Pay <i class="bi bi-arrow-right-circle-fill ms-2"></i>
                    </button>
                </form>
            </div>

        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/seatmap.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            
            // --- 1. GET CLASS CODE FROM SESSION ---
            const classCode = "${sessionScope.pendingClassCode}"; 
            
            // --- 2. TIMER LOGIC ---
            const bookingId = "${sessionScope.pendingBookingId}";
            const STORAGE_KEY = 'irctc_timer_expiry_' + bookingId;
            const timerElement = document.getElementById('timer');
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
                    timerElement.textContent = "00:00";
                    alert("Session Expired!");
                    sessionStorage.removeItem(STORAGE_KEY);
                    window.location.href = "${pageContext.request.contextPath}/jsp/home.jsp";
                    return;
                }
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                const mStr = minutes < 10 ? "0" + minutes : minutes;
                const sStr = seconds < 10 ? "0" + seconds : seconds;
                timerElement.textContent = mStr + ":" + sStr;
                if (minutes < 1) timerElement.classList.add('text-danger');
            }, 1000);

            // --- 3. DETERMINE COACH CODE ---
            const trainId = "${sessionScope.pendingTrainId}";
            const date = "${sessionScope.pendingDate}";
            const paxCount = ${sessionScope.pendingPassengerCount};
            
            let coachPrefix = "S"; 
            if (classCode === '1A') coachPrefix = "H";
            else if (classCode === '2A') coachPrefix = "A";
            else if (classCode === '3A') coachPrefix = "B";
            else if (classCode === '3E') coachPrefix = "M";
            else if (classCode === 'CC') coachPrefix = "C";
            
            const coachCode = coachPrefix + "1"; 

            // --- 4. FETCH AND RENDER ---
            fetch('${pageContext.request.contextPath}/SeatAvailabilityServlet?trainId=' + trainId + '&date=' + date + '&coach=' + coachCode)
                .then(response => response.json())
                .then(bookedList => {
                    initSeatMap(classCode, paxCount, bookedList); 
                })
                .catch(err => console.error("Failed to load seats", err));
        });
    </script>

</body>
</html>