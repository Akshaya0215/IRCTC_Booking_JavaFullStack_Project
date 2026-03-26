<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Live Status - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        .live-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
            margin-top: 30px;
            margin-bottom: 30px;
            border: 1px solid #e0e0e0;
        }
        .live-header {
            background: linear-gradient(135deg, var(--irctc-blue) 0%, #002a55 100%);
            color: white;
            padding: 25px 30px;
        }
        
        /* === FLIGHT TRACKER PROGRESS BAR === */
        .tracker-container {
            padding: 30px;
            background: #f8f9fa;
            border-bottom: 1px solid #eee;
        }
        .progress-track {
            height: 6px;
            background: #e0e0e0;
            border-radius: 3px;
            position: relative;
            margin: 20px 0;
        }
        .progress-fill {
            height: 100%;
            background: var(--irctc-orange); /* Orange Fill */
            border-radius: 3px;
            position: relative;
            box-shadow: 0 0 10px rgba(251, 121, 43, 0.4);
            transition: width 1.5s ease-in-out; /* Smooth animation on load */
        }
        .train-icon-puck {
            position: absolute;
            right: -16px; /* Center on the tip of the fill */
            top: -13px;
            width: 32px;
            height: 32px;
            background: white;
            border: 3px solid var(--irctc-orange);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--irctc-orange);
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
            z-index: 2;
        }
        .track-labels {
            display: flex;
            justify-content: space-between;
            font-size: 0.85rem;
            color: #666;
            font-weight: 600;
            text-transform: uppercase;
        }

        /* === VERTICAL TIMELINE STYLES === */
        .timeline-wrapper { padding: 20px 30px; }
        
        .timeline-row {
            display: flex;
            position: relative;
            padding-bottom: 30px;
        }
        .timeline-row:last-child { padding-bottom: 0; }
        
        /* Left Time Column */
        .col-time {
            width: 90px;
            text-align: right;
            padding-right: 20px;
            display: flex;
            flex-direction: column;
            padding-top: 0px;
        }
        .t-label { font-size: 0.75rem; color: #999; font-weight: 600; }
        .t-val { font-size: 0.95rem; color: #333; font-weight: 700; }
        
        /* Center Line Column */
        .col-line {
            width: 30px;
            position: relative;
            display: flex;
            justify-content: center;
        }
        .v-line {
            position: absolute;
            top: 5px; bottom: -5px;
            width: 3px;
            background: #e9ecef;
            z-index: 1;
        }
        .timeline-row:last-child .v-line { display: none; }
        
        .v-dot {
            width: 14px; height: 14px;
            background: #fff;
            border: 3px solid #ccc;
            border-radius: 50%;
            z-index: 2;
            margin-top: 5px;
        }
        
        /* States for Timeline */
        .timeline-row.passed .v-line { background: #28a745; }
        .timeline-row.passed .v-dot { background: #28a745; border-color: #28a745; }
        
        .timeline-row.current .v-dot {
            width: 22px; height: 22px;
            background: var(--irctc-orange);
            border: 4px solid #fff;
            box-shadow: 0 0 0 3px var(--irctc-orange);
            margin-top: 1px;
        }
        
        .timeline-row.next .v-dot {
            border-color: var(--irctc-blue);
            background: #eef5ff;
        }

        /* Right Content Column */
        .col-content { flex: 1; padding-left: 10px; }
        .station-title { font-size: 1.1rem; font-weight: 800; color: #333; margin-bottom: 2px; }
        .station-desc { font-size: 0.9rem; color: #666; }
        
        /* Badge */
        .status-pill {
            background: #f8f9fa; border: 1px solid #eee;
            padding: 2px 8px; border-radius: 4px;
            font-size: 0.75rem; font-weight: 700;
            color: #555; display: inline-block; margin-bottom: 4px;
        }
        .pill-current { background: #fff3cd; color: #856404; border-color: #ffeeba; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container">
        
        <div class="live-card">
            <%-- HEADER --%>
            <div class="live-header">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h2 class="mb-1 fw-bold">${liveData.get("train_name").asString}</h2>
                        <div class="opacity-75">
                            Train #${liveData.get("train_number").asString} &bull; 
                            <c:set var="delay" value="${liveData.get('delay').asInt}" />
                            <c:choose>
                                <c:when test="${delay > 0}">
                                    <span class="text-warning fw-bold"><i class="bi bi-clock-history"></i> Late by ${delay} min</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-success fw-bold"><i class="bi bi-check-circle-fill"></i> On Time</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <button onclick="location.reload()" class="btn btn-light btn-sm fw-bold text-primary border-0 shadow-sm">
                        <i class="bi bi-arrow-clockwise"></i> Refresh
                    </button>
                </div>
            </div>

            <%-- === FLIGHT TRACKER VISUALIZATION === --%>
            <div class="tracker-container">
                <div class="d-flex justify-content-between align-items-end mb-2">
                    <div>
                        <small class="text-muted text-uppercase fw-bold">Distance Covered</small>
                        <div class="fs-4 fw-bold text-primary">${liveData.get("distance_from_source").asInt} km</div>
                    </div>
                    <div class="text-end">
                        <small class="text-muted text-uppercase fw-bold">Total Distance</small>
                        <div class="fs-4 fw-bold text-dark">${liveData.get("total_distance").asInt} km</div>
                    </div>
                </div>

                <%-- Calculate Percentage for Progress Bar --%>
                <c:set var="distCovered" value="${liveData.get('distance_from_source').asDouble}" />
                <c:set var="distTotal" value="${liveData.get('total_distance').asDouble}" />
                <c:set var="percent" value="${(distCovered / distTotal) * 100}" />
                
                <%-- Ensure percent is between 0 and 100 to avoid CSS errors --%>
                <c:if test="${percent > 100}"><c:set var="percent" value="100"/></c:if>
                <c:if test="${percent < 0}"><c:set var="percent" value="0"/></c:if>

                <div class="progress-track">
                    <%-- The orange fill width depends on the calculation above --%>
                    <div class="progress-fill" style="width: ${percent}%;">
                        <div class="train-icon-puck">
                            <i class="bi bi-train-front-fill"></i>
                        </div>
                    </div>
                </div>
                
                <div class="track-labels">
                    <span>Start</span>
                    <span>Destination</span>
                </div>
            </div>

            <%-- === TIMELINE SECTION === --%>
            <div class="timeline-wrapper">
                <h6 class="text-muted text-uppercase fw-bold mb-4 ps-2">
                    <i class="bi bi-list-ul me-2"></i>Journey Timeline
                </h6>

                <%-- 1. NEXT STOP (Future) --%>
                <div class="timeline-row next">
                    <div class="col-time">
                        <span class="t-label">ETA</span>
                        <span class="t-val text-primary">
                            ${liveData.getAsJsonObject("next_stoppage_info").get("next_stoppage_time_diff").asString.replace("in ", "")}
                        </span>
                    </div>
                    <div class="col-line">
                        <div class="v-line"></div>
                        <div class="v-dot"></div>
                    </div>
                    <div class="col-content">
                        <span class="status-pill text-primary">UPCOMING</span>
                        <div class="station-title">
                            ${liveData.getAsJsonObject("next_stoppage_info").get("next_stoppage").asString}
                        </div>
                        <div class="station-desc">
                            Next scheduled halt.
                        </div>
                    </div>
                </div>

                <%-- 2. CURRENT LOCATION (Present) --%>
                <div class="timeline-row current">
                    <div class="col-time">
                        <span class="t-label">ARRIVAL</span>
                        <span class="t-val text-danger">${liveData.get("eta").asString}</span>
                    </div>
                    <div class="col-line">
                        <div class="v-line" style="background: linear-gradient(to bottom, #e9ecef 0%, #28a745 100%);"></div>
                        <div class="v-dot"></div>
                    </div>
                    <div class="col-content">
                        <span class="status-pill pill-current">CURRENT LOCATION</span>
                        <div class="station-title text-dark" style="font-size: 1.3rem;">
                            ${liveData.get("current_station_name").asString}
                        </div>
                        <div class="station-desc text-dark">
                            <c:if test="${not empty updateList}">
                                <i class="bi bi-info-circle-fill text-warning me-1"></i>
                                ${updateList[0].get("readable_message").asString}
                            </c:if>
                        </div>
                    </div>
                </div>

                <%-- 3. PAST UPDATES (History) --%>
                <%-- Loop through history items to show what has passed --%>
                <c:forEach var="update" items="${historyList}" begin="1"> <%-- Skip first as it duplicates current --%>
                    <div class="timeline-row passed">
                        <div class="col-time">
                            <span class="t-label">PASSED</span>
                            <span class="t-val text-muted" style="font-weight:500;">
                                ${update.get("label").asString.replace("As of ", "").replace("ago", "")}
                            </span>
                        </div>
                        <div class="col-line">
                            <div class="v-line"></div>
                            <div class="v-dot"></div>
                        </div>
                        <div class="col-content">
                            <div class="station-title fs-6 text-secondary">
                                ${update.get("readable_message").asString}
                            </div>
                            <c:if test="${update.has('hint') && not empty update.get('hint').asString}">
                                <div class="station-desc text-danger small">
                                    ${update.get("hint").asString}
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>

            </div>
        </div>
        
        <div class="text-center mb-5">
            <a href="${pageContext.request.contextPath}/jsp/liveStatus.jsp" class="btn btn-secondary">Track Another Train</a>
        </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>