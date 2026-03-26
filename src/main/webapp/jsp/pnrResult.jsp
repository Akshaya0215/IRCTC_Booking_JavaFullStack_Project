<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>PNR Status Result - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        .ticket-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
            overflow: hidden;
            margin-top: 30px;
            margin-bottom: 30px;
            border: none;
        }
        .ticket-header {
            background: linear-gradient(to right, #f8f9fa, #fff);
            padding: 20px 25px;
            border-bottom: 2px dashed #ccc;
            display: flex; justify-content: space-between; align-items: center;
        }
        .ticket-body { padding: 30px; }
        .route-line { color: var(--irctc-blue); font-size: 1.2rem; font-weight: 700; }
        .info-label { color: #888; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 4px; }
        .info-value { font-size: 1.1rem; font-weight: 700; color: #333; }
        
        .status-badge { padding: 6px 12px; border-radius: 50px; font-weight: 700; font-size: 0.85rem; display: inline-flex; align-items: center; gap: 5px;}
        .status-cnf { background: #e6f4ea; color: #1e7e34; }
        .status-wl { background: #fff3cd; color: #856404; }
        .status-can { background: #f8d7da; color: #721c24; }
        .source-badge { font-size: 0.75rem; padding: 4px 8px; border-radius: 4px; letter-spacing: 0.5px; text-transform: uppercase; font-weight: bold; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container">
        
        <%-- Set Data Variables --%>
        <c:choose>
            <c:when test="${sourceType == 'LOCAL'}">
                <c:set var="pnrNo" value="${booking.bookingId}" />
                <c:set var="trainInfo" value="${booking.train.trainNumber} / ${booking.train.trainName}" />
                <c:set var="doj"><fmt:formatDate value="${booking.journeyDate}" pattern="dd-MMM-yyyy"/></c:set>
                <c:set var="fromStn" value="${booking.segmentSource}" />
                <c:set var="toStn" value="${booking.segmentDestination}" />
                <c:set var="paxList" value="${booking.passengersList}" />
                <c:set var="isLocal" value="true" />
            </c:when>
            <c:when test="${sourceType == 'API'}">
                <c:set var="pnrNo" value="${param.pnr}" />
                <c:set var="trainInfo" value="${apiData.get('trainNumber').asString} / ${apiData.get('trainName').asString}" />
                <c:set var="doj" value="${apiData.get('journeyDate').asString}" />
                <c:set var="fromStn" value="${apiData.get('source').asString}" />
                <c:set var="toStn" value="${apiData.get('destination').asString}" />
                <c:set var="paxList" value="${passengerList}" />
                <c:set var="isLocal" value="false" />
            </c:when>
        </c:choose>

        <div class="row justify-content-center">
            <div class="col-lg-10">
                
                <div class="ticket-card">
                    <div class="ticket-header">
                        <div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="fs-4 fw-bold text-dark">PNR: ${pnrNo}</span>
                                <span class="source-badge ${isLocal ? 'bg-primary text-white' : 'bg-warning text-dark'}">
                                    ${isLocal ? 'Internal Booking' : 'Live IRCTC Data'}
                                </span>
                            </div>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/jsp/pnrStatus.jsp" class="btn btn-outline-secondary btn-sm fw-bold">
                                <i class="bi bi-search me-1"></i> Check Another
                            </a>
                        </div>
                    </div>

                    <div class="ticket-body">
                        <div class="row mb-5">
                            <div class="col-md-4 mb-3 mb-md-0">
                                <div class="info-label">Train</div>
                                <div class="info-value"><i class="bi bi-train-front text-secondary me-2"></i>${trainInfo}</div>
                            </div>
                            <div class="col-md-4 mb-3 mb-md-0 text-md-center">
                                <div class="info-label">Journey Route</div>
                                <div class="route-line">
                                    ${fromStn} <i class="bi bi-arrow-right mx-2 text-muted"></i> ${toStn}
                                </div>
                            </div>
                            <div class="col-md-4 text-md-end">
                                <div class="info-label">Date of Journey</div>
                                <div class="info-value"><i class="bi bi-calendar-event text-secondary me-2"></i>${doj}</div>
                            </div>
                        </div>

                        <h6 class="text-muted text-uppercase fw-bold mb-3 pb-2 border-bottom">Passenger Details</h6>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Passenger</th>
                                        <th>Current Status</th>
                                        <th>Coach / Berth</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${paxList}" varStatus="s">
                                        <tr>
                                            <td class="fw-bold text-muted">
                                                <i class="bi bi-person-fill me-2"></i> 
                                                <%-- Name Logic --%>
                                                <c:choose>
                                                    <c:when test="${isLocal}"><c:out value="${p.name}"/></c:when>
                                                    <c:otherwise>Passenger ${s.count}</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <%-- Status Logic --%>
                                                <c:choose>
                                                    <c:when test="${isLocal}">
                                                        <c:set var="currStatus" value="${booking.status}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="currStatus" value="${p.asJsonObject.get('currentStatus').asString}" />
                                                    </c:otherwise>
                                                </c:choose>

                                                <c:choose>
                                                    <c:when test="${currStatus.contains('CNF') || currStatus == 'CONFIRMED'}">
                                                        <span class="status-badge status-cnf"><i class="bi bi-check-circle-fill"></i> ${currStatus}</span>
                                                    </c:when>
                                                    <c:when test="${currStatus.contains('CAN') || currStatus.contains('MOD') || currStatus == 'CANCELLED'}">
                                                        <span class="status-badge status-can"><i class="bi bi-x-circle-fill"></i> ${currStatus}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-wl"><i class="bi bi-hourglass-split"></i> ${currStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <%-- Seat Logic (Explicit 'p' usage) --%>
                                                <c:choose>
                                                    <c:when test="${isLocal}">
                                                        <c:if test="${booking.status == 'CONFIRMED'}">
                                                            <strong><c:out value="${p.coachCode}"/> / <c:out value="${p.seatNumber}"/></strong> 
                                                            <span class="text-muted small">[<c:out value="${p.berthType}"/>]</span>
                                                        </c:if>
                                                        <c:if test="${booking.status != 'CONFIRMED'}">--</c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <strong>${p.asJsonObject.get('coach').asString} / ${p.asJsonObject.get('berth').asInt}</strong>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>