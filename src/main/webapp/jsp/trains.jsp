<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search Results - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        /* === HEADER STYLE (UNIFIED BLUE) === */
        .search-header {
            background-color: var(--irctc-blue);
            color: white;
            padding: 20px 25px;
            border-radius: 8px;
            border-top: 5px solid var(--irctc-orange);
            border-left: none; 
            margin-bottom: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            display: flex; justify-content: space-between; align-items: center;
        }
        
        .train-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.02);
            position: relative;
            transition: all 0.2s;
            border-left: 5px solid var(--irctc-orange); 
        }
        
        .train-card:hover { 
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            border-color: #b0b0b0; 
        }

        .card-top {
            padding: 20px;
            display: flex; flex-wrap: wrap; align-items: center;
            border-bottom: 1px solid #f1f1f1;
        }
        .train-info { flex: 2; min-width: 250px; }
        .train-name { font-size: 1.15rem; font-weight: 800; color: var(--irctc-blue); margin-bottom: 4px; }
        .runs-on { font-size: 0.8rem; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }
        
        .train-schedule { flex: 3; display: flex; align-items: center; justify-content: center; min-width: 300px; text-align: center; }
        .time-val { font-size: 1.2rem; font-weight: 700; color: #333; }
        .stn-val { font-size: 0.85rem; font-weight: 600; color: #555; margin-top: 2px; }
        .duration-line { flex-grow: 1; height: 2px; background: #ddd; margin: 0 20px; position: relative; }
        .duration-text { position: absolute; top: -20px; width: 100%; text-align: center; font-size: 0.75rem; color: #999; }

        .class-tabs {
            padding: 10px 20px;
            background: #fafafa; display: flex; gap: 10px; overflow-x: auto; scrollbar-width: none;
        }
        .class-tabs::-webkit-scrollbar { display: none; }
        
        .class-btn {
            border: 1px solid #ddd;
            background: white; border-radius: 6px;
            padding: 10px 15px; min-width: 140px; cursor: pointer; text-align: left; transition: all 0.2s;
        }
        .class-btn:hover { background: #f0f8ff; border-color: var(--irctc-blue); }
        .class-btn.active { background: #eef5ff; border: 1px solid var(--irctc-orange); border-bottom-width: 3px; }
        
        .cls-title { font-size: 0.95rem; font-weight: 700; color: #003366; display: block; }
        .cls-sub { font-size: 0.75rem; color: var(--irctc-orange); margin-top: 2px; display: block; font-weight: 600; }

        .availability-panel {
            display: none;
            padding: 20px; background: #fff; border-top: 1px solid #eee;
            animation: slideDown 0.3s ease-out; position: relative;
        }
        @keyframes slideDown { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }

        .date-slider { display: flex; gap: 10px; overflow-x: auto; padding-bottom: 10px; scrollbar-width: none; }
        
        .date-card {
            flex: 1;
            min-width: 110px; background: white; border: 1px solid #e0e0e0;
            border-radius: 6px; padding: 12px; text-align: center; cursor: pointer; 
            transition: all 0.2s;
        }
        
        .date-card:hover { background: #f0f8ff; border-color: var(--irctc-blue); }
        .date-card.selected { border: 1px solid var(--irctc-orange); background: #fff9f2; }
        
        .date-val { font-weight: 700; font-size: 0.85rem; margin-bottom: 5px; color: #333; }
        .status-val { font-weight: 800; font-size: 0.9rem; color: #28a745; }
        .status-wl { color: #dc3545; } 
        .status-rac { color: #ffc107; }

        .book-action {
            margin-top: 20px;
            display: flex; justify-content: space-between; align-items: center;
            padding-top: 15px; border-top: 1px dashed #eee;
        }
        .final-price { font-size: 1.5rem; font-weight: 800; color: #003366; }
        .btn-minimize {
            position: absolute;
            bottom: 10px; right: 10px;
            background: #f0f0f0; border: none; border-radius: 50%;
            width: 30px; height: 30px; color: #555; cursor: pointer;
            display: flex;
            align-items: center; justify-content: center; transition: 0.2s;
        }
        .btn-minimize:hover { background: #e0e0e0; color: #000; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-4">
        
        <div class="search-header d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
                <h4 class="mb-1 text-white" style="font-weight: 800;">
                    <c:out value="${source}" /> <i class="bi bi-arrow-right text-white-50 mx-2"></i> <c:out value="${destination}" />
                </h4>
                <div class="text-white-50 small">
                    <i class="bi bi-calendar-event me-1"></i> 
                    <strong><fmt:formatDate value="${journeyDate}" pattern="EEE, dd MMM yyyy" /></strong>
                    &bull; <c:out value="${trainList.size()}"/> Trains Found
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/jsp/home.jsp" class="btn btn-outline-light btn-sm fw-bold">
                <i class="bi bi-pencil-square me-1"></i> Modify Search
            </a>
        </div>

        <c:choose>
             <c:when test="${not empty trainList}">
                 <c:forEach var="train" items="${trainList}">
                    
                    <c:set var="isRajdhani" value="${fn:containsIgnoreCase(train.trainName, 'Rajdhani')}" />
                    <c:set var="baseFare" value="${train.ticketPrice}" />
                    
                    <%-- 1. UPDATED DISPLAY MULTIPLIERS --%>
                    <c:set var="fare3A" value="${baseFare * 1.8}" />
                    <c:set var="fare2A" value="${baseFare * 2.5}" />
                    <c:set var="fare1A" value="${baseFare * 4.0}" />

                    <div class="train-card">
                        
                        <div class="card-top">
                            <div class="train-info">
                                <div class="train-name"><c:out value="${train.trainName}" /> (<c:out value="${train.trainNumber}" />)</div>
                                <div class="runs-on"><i class="bi bi-calendar-week me-1"></i> Runs Daily</div>
                            </div>
                            <div class="train-schedule">
                                <div class="text-end">
                                    <div class="time-val">
                                        <c:choose>
                                            <c:when test="${not empty train.segmentDepartureTime}"><fmt:formatDate value="${train.segmentDepartureTime}" pattern="HH:mm" /></c:when>
                                            <c:otherwise><fmt:formatDate value="${train.departureTime}" pattern="HH:mm" /></c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="stn-val">DEP</div>
                                </div>
                                <div class="duration-line"><span class="duration-text">Direct</span></div>
                                <div class="text-start">
                                    <div class="time-val">
                                        <c:choose>
                                            <c:when test="${not empty train.segmentArrivalTime}"><fmt:formatDate value="${train.segmentArrivalTime}" pattern="HH:mm" /></c:when>
                                            <c:otherwise><fmt:formatDate value="${train.arrivalTime}" pattern="HH:mm" /></c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="stn-val">ARR</div>
                                </div>
                            </div>
                        </div>

                        <div class="class-tabs">
                            <c:if test="${!isRajdhani}">
                                <div id="btn-SL-${train.trainId}" class="class-btn" onclick="loadAvailability('${train.trainId}', 'SL', ${train.availableSeats}, ${baseFare})">
                                    <span class="cls-title">Sleeper (SL)</span><span class="cls-sub">Refresh <i class="bi bi-arrow-clockwise"></i></span>
                                </div>
                            </c:if>
                            <div id="btn-3A-${train.trainId}" class="class-btn" onclick="loadAvailability('${train.trainId}', '3A', ${train.availableSeats - 25}, ${fare3A})">
                                <span class="cls-title">AC 3 Tier (3A)</span><span class="cls-sub">Refresh <i class="bi bi-arrow-clockwise"></i></span>
                            </div>
                            <div id="btn-2A-${train.trainId}" class="class-btn" onclick="loadAvailability('${train.trainId}', '2A', ${train.availableSeats - 45}, ${fare2A})">
                                <span class="cls-title">AC 2 Tier (2A)</span><span class="cls-sub">Refresh <i class="bi bi-arrow-clockwise"></i></span>
                            </div>
                            <div id="btn-1A-${train.trainId}" class="class-btn" onclick="loadAvailability('${train.trainId}', '1A', ${train.availableSeats - 55}, ${fare1A})">
                                <span class="cls-title">AC First (1A)</span><span class="cls-sub">Refresh <i class="bi bi-arrow-clockwise"></i></span>
                            </div>
                        </div>

                        <div id="panel-${train.trainId}" class="availability-panel">
                            <div class="date-slider" id="dates-${train.trainId}"></div>
                            <div class="book-action">
                                <div class="d-flex flex-column">
                                    <span class="text-muted small fw-bold text-uppercase">Total Base Fare</span>
                                    <div class="final-price" id="price-${train.trainId}"></div>
                                </div>
                                <%-- 2. UPDATED BUTTON: Has ID and data-class --%>
                                <button id="book-btn-${train.trainId}" data-class="SL" class="btn btn-orange fw-bold px-5 py-3 fs-5 shadow-sm rounded-pill" onclick="bookTicket('${train.trainId}')">
                                    Book Now
                                </button>
                            </div>
                            <button class="btn-minimize" onclick="closePanel('${train.trainId}')" title="Close">
                                <i class="bi bi-chevron-up"></i>
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning text-center p-5 shadow-sm border-0">
                    <i class="bi bi-exclamation-circle display-4 text-warning mb-3"></i>
                    <h4>No Trains Found</h4>
                    <p class="text-muted">Try changing the date or station.</p>
                    <a href="${pageContext.request.contextPath}/jsp/home.jsp" class="btn btn-outline-primary mt-2">Search Again</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <%-- 3. UPDATED SCRIPT: Updates book button data-class on click --%>
    <script>
        function loadAvailability(trainId, classCode, seats, price) {
            document.querySelectorAll('[id^="btn-"][id$="-' + trainId + '"]').forEach(el => el.classList.remove('active'));
            
            const activeBtn = document.getElementById('btn-' + classCode + '-' + trainId);
            if(activeBtn) activeBtn.classList.add('active');

            document.getElementById('price-' + trainId).innerHTML = "₹ " + Math.round(price);
            
            // CRITICAL: Update the book button's data attribute
            const bookBtn = document.getElementById('book-btn-' + trainId);
            if(bookBtn) {
                bookBtn.setAttribute('data-class', classCode);
            }

            const container = document.getElementById('dates-' + trainId);
            container.innerHTML = ""; 

            const journeyDateRaw = '<fmt:formatDate value="${journeyDate}" pattern="yyyy-MM-dd" />';
            let d = new Date(journeyDateRaw);
            
            for(let i=0; i<7; i++) {
                let currentD = new Date(d);
                currentD.setDate(d.getDate() + i);
                let dateStr = currentD.toLocaleDateString('en-GB', { weekday: 'short', day: '2-digit', month: 'short' });
                
                let simSeats = (i === 0) ? seats : (seats - (i * 8)); 
                let simStatusHtml = "";
                
                if(simSeats > 0) {
                    simStatusHtml = '<div class="status-val avl">AVL ' + simSeats + '</div>';
                } else if(simSeats > -20) {
                    simStatusHtml = '<div class="status-val status-rac">RAC ' + Math.abs(simSeats) + '</div>';
                } else {
                    simStatusHtml = '<div class="status-val status-wl">WL ' + Math.abs(simSeats) + '</div>';
                }

                let cardHtml = '<div class="date-card ' + (i===0 ? 'selected' : '') + '" onclick="selectDate(this)">' +
                           '<div class="date-val">' + dateStr + '</div>' +
                           simStatusHtml +
                           '</div>';
                container.insertAdjacentHTML('beforeend', cardHtml);
            }
            document.getElementById('panel-' + trainId).style.display = "block";
        }
        
        function selectDate(card) {
            let siblings = card.parentNode.children;
            for(let i=0; i<siblings.length; i++) {
                siblings[i].classList.remove('selected');
            }
            card.classList.add('selected');
        }

        function closePanel(trainId) {
            document.getElementById('panel-' + trainId).style.display = "none";
            document.querySelectorAll('[id^="btn-"][id$="-' + trainId + '"]').forEach(el => el.classList.remove('active'));
        }

        function bookTicket(trainId) {
            // Get selected class from the button attribute
            const bookBtn = document.getElementById('book-btn-' + trainId);
            const selectedClass = bookBtn.getAttribute('data-class') || "SL";

            const url = "${pageContext.request.contextPath}/BookingServlet?trainId=" + trainId + 
                        "&classCode=" + selectedClass + 
                        "&journeyDate=${journeyDate}&source=${source}&destination=${destination}";
            window.location.href = url;
        }
    </script>
</body>
</html>