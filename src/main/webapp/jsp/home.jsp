<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.3/themes/base/jquery-ui.css">
    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <%-- Chat Styles --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat.css">
    
    <style>
        /* Swap button style */
        .swap-container {
            text-align: center;
            margin-top: -10px;
            margin-bottom: -10px;
            position: relative;
            z-index: 10;
        }
        .btn-swap {
            background-color: var(--irctc-blue);
            color: white;
            border: 2px solid white;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            padding: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
            transition: all 0.3s ease;
        }
        .btn-swap:hover {
            background-color: var(--irctc-orange);
            color: white;
            transform: rotate(180deg);
        }
        /* Tagline Styles */
        .irctc-tagline {
            color: var(--irctc-orange);
            text-align: center;
            margin-top: -30px;
            margin-left: 198px; 
            position: relative;
            z-index: 5;
            text-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5);
        }
        .irctc-tagline h1 {
            font-weight: 700;
            font-size: 3.8rem;
            margin-bottom: 0;
        }
        .irctc-tagline p {
            font-size: 1.5rem;
            font-weight: 600;
            color: white;
            margin-top: -5px;
            text-shadow: 0px 0px 8px rgba(0,0,0,0.8);
        }
        
        /* Hover effect for the new header buttons */
        .header-btn:hover {
            background-color: #004080 !important; /* Slightly lighter blue on hover */
        }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-5">
        <div class="row">
            
            <%-- LEFT COLUMN: Booking Form --%>
            <div class="col-lg-5 col-md-7">
              
                <div class="card-form" style="overflow: hidden;"> <%-- Added overflow:hidden for rounded corners --%>
                    
                    <%-- === NEW: Integrated Blue Header Buttons === --%>
                    <div class="row g-0 text-center mb-4" style="margin: -30px -30px 30px -30px;">
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/jsp/pnrStatus.jsp" 
                               class="d-block py-3 text-decoration-none fw-bold header-btn" 
                               style="background-color: var(--irctc-blue); color: white; border-right: 1px solid rgba(255,255,255,0.2); transition: background 0.3s;">
                                <i class="bi bi-search me-2"></i> PNR Status
                            </a>
                        </div>
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/jsp/liveStatus.jsp" 
                               class="d-block py-3 text-decoration-none fw-bold header-btn" 
                               style="background-color: var(--irctc-blue); color: white; transition: background 0.3s;">
                                <i class="bi bi-broadcast me-2 text-danger"></i> Live Status
                            </a>
                        </div>
                    </div>
                    <%-- === END NEW HEADER === --%>

                    <h2><i class="bi bi-train-front-fill me-2"></i>Book Ticket</h2>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <c:out value="${errorMessage}" />
                        </div>
                    </c:if>

                    <form id="searchTrainForm" action="${pageContext.request.contextPath}/TrainListServlet" method="GET">
                        <div class="mb-3"> 
                            <label for="source" class="form-label">From</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
                                <input type="text" id="source" name="source" class="form-control" placeholder="Enter source station" required> 
                            </div>
                        </div>

                        <div class="swap-container">
                            <button type="button" class="btn btn-swap" id="swapStations" title="Swap Stations">
                                <i class="bi bi-arrow-down-up"></i>
                            </button>
                        </div>

                        <div class="mb-3">
                            <label for="destination" class="form-label">To</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
                                <input type="text" id="destination" name="destination" class="form-control" placeholder="Enter destination station" required>
                            </div>
                        </div>

                        <div class="mb-4">
                             <label for="journeyDate" class="form-label">Date</label>
                             <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-calendar-event-fill"></i></span>
                                 <%-- Added ID for JS targeting --%>
                                 <input type="date" id="journeyDate" name="journeyDate" class="form-control" required>
                            </div>
                        </div>
                          
                        <button type="submit" class="btn btn-orange w-100">
                            <i class="bi bi-search me-2"></i>Search Trains
                        </button>
                    </form>
                </div>
            </div>
            
            <%-- RIGHT COLUMN: Tagline Text --%>
            <div class="col-lg-7 col-md-5 d-none d-md-block">
                <div class="irctc-tagline">
                    <h1>INDIAN RAILWAYS</h1>
                    <p>Safety | Security | Punctuality</p>
                </div>
            </div>

        </div> <%-- End Row --%>
    </div>

   <%-- Login Modal --%>
   <div id="loginModal" class="modal">
      <div class="modal-content">
        <span class="close-button" onclick="closeModal()">&times;</span>
        <p>Please log in to search for trains.</p>
        <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="btn btn-primary">
             <i class="bi bi-box-arrow-in-right me-1"></i> Go to Login
        </a>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.3/jquery-ui.min.js"></script>

    <script>
        // --- START: Date Restriction Logic ---
        // Get today's date
        var today = new Date();
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); // January is 0!
        var yyyy = today.getFullYear();

        // Format: YYYY-MM-DD
        var currentDate = yyyy + '-' + mm + '-' + dd;

        // Set the 'min' attribute of the date input to today
        document.getElementById("journeyDate").setAttribute("min", currentDate);
        // --- END: Date Restriction Logic ---

        var modal = document.getElementById("loginModal");
        var form = document.getElementById("searchTrainForm");
        var isLoggedIn = ${not empty sessionScope.loggedInUser};
        
        form.addEventListener('submit', function(event) {
            if (!isLoggedIn) {
                event.preventDefault();
                modal.style.display = "block";
            }
        });
        
        function closeModal() {
            modal.style.display = "none";
        }
        
        window.onclick = function(event) {
            if (event.target == modal) closeModal();
        }

        document.getElementById('swapStations').addEventListener('click', function() {
            var sourceInput = document.getElementById('source');
            var destInput = document.getElementById('destination');
            var temp = sourceInput.value;
            sourceInput.value = destInput.value;
            destInput.value = temp;
        });
        
        $(function() {
            function initAutocomplete(selector) {
                $(selector).autocomplete({
                    source: "${pageContext.request.contextPath}/StationSearchServlet",
                    minLength: 2
                });
            }
            initAutocomplete("#source");
            initAutocomplete("#destination");
        });
    </script>
    
    <%-- CHAT WIDGET HTML --%>
    <div id="chat-launcher" class="chat-btn">
        <i class="bi bi-chat-dots-fill"></i>
    </div>

    <div id="chat-window" class="chat-window">
        <div class="chat-header">
            <span><i class="bi bi-robot me-2"></i> Disha - AI Assistant</span>
            <i class="bi bi-x-lg" onclick="document.getElementById('chat-window').style.display='none'" style="cursor:pointer;"></i>
        </div>
        
        <div id="chat-body" class="chat-body">
            <div class="msg msg-ai">
                Hello! I am <b>Disha</b>. Ask me about trains, PNR status, or travel tips!
            </div>
            <%-- Typing indicator is now injected dynamically by JS --%>
        </div>
        
        <div class="chat-footer">
            <input type="text" id="chat-input" placeholder="Ask me anything..." autocomplete="off">
            <button id="chat-send"><i class="bi bi-send-fill"></i></button>
        </div>
    </div>

    <%-- Link CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat.css">

    <%-- DEFINE URL VARIABLE FOR JS --%>
    <script>
        const chatApiUrl = "${pageContext.request.contextPath}/ChatServlet";
    </script>

    <%-- Link JS --%>
    <script src="${pageContext.request.contextPath}/js/chat.js"></script>
    
</body>
</html>