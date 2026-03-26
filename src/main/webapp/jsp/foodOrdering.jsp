<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Meals on Wheels - IRCTC Clone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        /* === HEADER STYLES (Warm Gradient) === */
        .food-header {
            background: linear-gradient(to right, #fff3cd, #ffffff);
            border-left: 5px solid var(--irctc-orange);
            padding: 25px 30px;
            margin-bottom: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
        .header-title { font-weight: 800; font-size: 1.5rem; color: #333; letter-spacing: -0.5px; }

        /* Context Bar */
        .context-bar { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }

        /* === MENU LIST STYLES === */
        .menu-item {
            display: flex; justify-content: space-between; align-items: flex-start;
            padding: 20px 0; border-bottom: 1px solid #f0f0f0; transition: all 0.2s;
            background: white;
        }
        .menu-item:last-child { border-bottom: none; }
        .menu-item:hover { background-color: #fafafa; padding-left: 10px; padding-right: 10px; border-radius: 8px;}

        /* Item Icons */
        .item-icon { width: 18px; height: 18px; display: flex; align-items: center; justify-content: center; border-radius: 4px; margin-top: 3px; flex-shrink: 0;}
        .veg { border: 1px solid #28a745; } .veg i { color: #28a745; font-size: 10px; }
        .non-veg { border: 1px solid #dc3545; } .non-veg i { color: #dc3545; font-size: 10px; }
        
        .item-content { margin-left: 15px; flex-grow: 1; padding-right: 15px; }
        
        /* Clickable Title (No Underline) */
        .item-title { 
            font-weight: 700; color: var(--irctc-blue); font-size: 1.05rem; margin-bottom: 4px; 
            cursor: pointer; display: inline-block;
            transition: color 0.2s;
            text-decoration: none;
        }
        .item-title:hover { color: var(--irctc-orange); text-decoration: none; }
        
        .item-desc { color: #777; font-size: 0.85rem; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
        
        .item-action { text-align: right; min-width: 110px; }
        .item-price { font-weight: 700; font-size: 1.1rem; color: #333; display: block; margin-bottom: 8px; }
        
        /* Buttons */
        .btn-add {
            background: white; color: var(--irctc-orange); border: 1px solid var(--irctc-orange);
            font-weight: 700; font-size: 0.8rem; padding: 6px 20px; border-radius: 4px;
            text-transform: uppercase; transition: all 0.2s; cursor: pointer; width: 90px;
        }
        .btn-add:hover { background: #fff5f0; }

        .qty-control {
            display: none; align-items: center; justify-content: space-between;
            background: white; border: 1px solid var(--irctc-orange);
            border-radius: 4px; width: 90px; height: 32px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .qty-btn {
            width: 28px; height: 100%; border: none; background: transparent;
            color: var(--irctc-orange); font-weight: 900; font-size: 1.1rem; cursor: pointer;
            display: flex; align-items: center; justify-content: center;
        }
        .qty-btn:hover { background-color: #fff5f0; }
        .qty-val { font-weight: 700; font-size: 0.9rem; color: #333; }

        /* Accordion Styles */
        .accordion-item { border: none; border-bottom: 1px solid #e0e0e0; background: transparent; }
        .accordion-button {
            background-color: #f8f9fa; color: var(--irctc-blue); font-weight: 700;
            box-shadow: none !important; border-radius: 8px !important; margin-bottom: 10px;
        }
        .accordion-button:not(.collapsed) {
            background-color: var(--irctc-blue); color: white;
        }
        .accordion-body { padding: 0 10px 20px 10px; }

        /* Floating Cart Bar */
        .cart-bar {
            position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%);
            background: #212529; color: white; padding: 12px 25px; border-radius: 50px;
            display: flex; align-items: center; justify-content: space-between;
            box-shadow: 0 10px 30px rgba(0,0,0,0.4); z-index: 1000;
            display: none; width: 90%; max-width: 500px;
            border: 1px solid rgba(255,255,255,0.1);
        }

        /* === OFFCANVAS CART STYLES (For the Slide-out) === */
        .offcanvas-header { background: var(--irctc-light-bg); border-bottom: 1px solid #ddd; }
        .cart-item-row { display: flex; justify-content: space-between; margin-bottom: 12px; border-bottom: 1px dashed #eee; padding-bottom: 8px; }
        .cart-item-name { font-size: 0.95rem; color: #333; font-weight: 600; }
        .cart-item-price { font-weight: 700; color: var(--irctc-blue); }
        .cart-summary { background: #f8f9fa; padding: 15px; border-radius: 8px; margin-top: 20px; }
    </style>
</head>
<body>

    <jsp:include page="navbar.jsp" />

    <div class="container main-container mt-4">
        
        <div class="card-form mx-auto" style="max-width: 800px; overflow: hidden;">
            
            <%-- HEADER --%>
            <div class="food-header">
                <div class="header-title">
                    <i class="bi bi-cup-hot-fill text-warning me-2"></i> Meals on Wheels
                </div>
                
                <div class="context-bar">
                    <div class="text-muted small">
                        PNR: <strong class="text-dark fs-6 ms-1">${pnr}</strong>
                    </div>
                    
                    <div class="d-flex align-items-center gap-2">
                         <div class="badge bg-light text-dark border px-3 py-2">
                            <c:out value="${mealCategory.replace('_', ' ')}"/> MENU
                        </div>
                        <c:choose>
                            <c:when test="${mealCategory == 'FULL_MENU'}">
                                <a href="${pageContext.request.contextPath}/FoodOrderingServlet?pnr=${pnr}" class="btn btn-sm btn-primary fw-bold">
                                    Smart View
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/FoodOrderingServlet?pnr=${pnr}&view=full" class="btn btn-sm btn-outline-secondary fw-bold">
                                    Full Menu
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <%-- MENU CONTENT --%>
            <c:choose>
                <%-- OPTION A: FULL MENU (ACCORDION) --%>
                <c:when test="${mealCategory == 'FULL_MENU'}">
                    <div class="accordion" id="menuAccordion">
                        <div class="accordion-item">
                            <h2 class="accordion-header"><button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#c1"><i class="bi bi-sunrise me-2"></i> Breakfast</button></h2>
                            <div id="c1" class="accordion-collapse collapse show" data-bs-parent="#menuAccordion"><div class="accordion-body">
                                <c:forEach var="item" items="${menuList}"><c:if test="${item.category == 'BREAKFAST'}"><%@ include file="foodItemRow.jsp" %></c:if></c:forEach>
                            </div></div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#c2"><i class="bi bi-disc me-2"></i> Lunch & Dinner</button></h2>
                            <div id="c2" class="accordion-collapse collapse" data-bs-parent="#menuAccordion"><div class="accordion-body">
                                <c:forEach var="item" items="${menuList}"><c:if test="${item.category == 'MEALS' or item.category == 'LUNCH' or item.category == 'DINNER'}"><%@ include file="foodItemRow.jsp" %></c:if></c:forEach>
                            </div></div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header"><button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#c3"><i class="bi bi-cup-hot me-2"></i> Snacks</button></h2>
                            <div id="c3" class="accordion-collapse collapse" data-bs-parent="#menuAccordion"><div class="accordion-body">
                                <c:forEach var="item" items="${menuList}"><c:if test="${item.category == 'SNACKS' or item.category == 'ALL_DAY'}"><%@ include file="foodItemRow.jsp" %></c:if></c:forEach>
                            </div></div>
                        </div>
                    </div>
                </c:when>

                <%-- OPTION B: SMART MENU (LIST) --%>
                <c:otherwise>
                    <div class="menu-list">
                        <c:forEach var="item" items="${menuList}">
                            <%@ include file="foodItemRow.jsp" %>
                        </c:forEach>
                        <c:if test="${empty menuList}">
                            <div class="text-center py-5 text-muted"><h5>Kitchen Closed</h5><p>No items available. Try Full Menu.</p></div>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

    <%-- FLOATING CART --%>
    <div id="cart-bar" class="cart-bar">
        <div class="d-flex align-items-center gap-3">
            <div class="bg-success rounded-circle d-flex align-items-center justify-content-center" style="width:35px; height:35px;">
                <i class="bi bi-basket2-fill"></i>
            </div>
            <div>
                <div id="cart-count" class="fw-bold text-white" style="font-size: 0.85rem; letter-spacing: 0.5px;">0 ITEMS</div>
                <div id="cart-total" class="fw-bold text-warning fs-6">₹0</div>
            </div>
        </div>
        <button class="btn btn-light rounded-pill fw-bold text-dark px-4" onclick="openCartDrawer()">
            Checkout <i class="bi bi-chevron-right ms-1"></i>
        </button>
    </div>

    <%-- === 1. OFFCANVAS CART DRAWER === --%>
    <div class="offcanvas offcanvas-end" tabindex="-1" id="cartOffcanvas" aria-labelledby="cartOffcanvasLabel">
      <div class="offcanvas-header">
        <h5 class="offcanvas-title fw-bold" id="cartOffcanvasLabel"><i class="bi bi-basket3-fill text-warning me-2"></i> Your Cart</h5>
        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas"></button>
      </div>
      <div class="offcanvas-body d-flex flex-column">
        <div id="cart-items-list" class="flex-grow-1 overflow-auto"></div>
        <div class="cart-summary shadow-sm border-top">
            <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Subtotal</span><span class="fw-bold" id="drawer-total">₹0</span>
            </div>
            <hr>
            <h6 class="fw-bold mb-3">Contact Details</h6>
            <div class="mb-3">
                <input type="text" id="contactName" class="form-control mb-2" placeholder="Receiver Name" value="${sessionScope.loggedInUser.fullName}">
                <input type="tel" id="contactPhone" class="form-control" placeholder="Phone Number" value="${sessionScope.loggedInUser.phone}">
            </div>
            <button class="btn btn-orange w-100 py-3 fw-bold shadow" id="payBtn" onclick="initiatePayment()">
                PAY NOW <i class="bi bi-credit-card-2-front-fill ms-2"></i>
            </button>
        </div>
      </div>
    </div>

    <%-- === 2. TOAST NOTIFICATION === --%>
    <div class="toast-container position-fixed bottom-0 start-50 translate-middle-x p-3" style="z-index: 1100;">
      <div id="successToast" class="toast align-items-center text-white bg-success border-0" role="alert">
        <div class="d-flex">
          <div class="toast-body fs-6"><i class="bi bi-check-circle-fill me-2"></i> <span id="toastMsg">Order Placed!</span></div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
      </div>
      <div id="errorToast" class="toast align-items-center text-white bg-danger border-0" role="alert">
        <div class="d-flex">
          <div class="toast-body fs-6"><i class="bi bi-exclamation-triangle-fill me-2"></i> <span id="errorMsg">Error</span></div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
      </div>
    </div>

    <%-- INFO MODAL --%>
    <div class="modal fade" id="infoModal" tabindex="-1" aria-hidden="true" style="z-index: 1055;">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold" id="modalTitle">Food Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="d-flex align-items-center mb-3">
                        <span id="modalTypeIcon" class="me-2"></span>
                        <span id="modalCategory" class="badge bg-secondary"></span>
                    </div>
                    <h4 id="modalItemName" class="fw-bold text-irctc mb-2"></h4>
                    <h3 id="modalPrice" class="text-success fw-bold mb-3"></h3>
                    <div class="bg-light p-3 rounded border mb-3">
                        <h6 class="fw-bold text-muted text-uppercase small">Contents & Description</h6>
                        <p id="modalDesc" class="mb-0" style="font-size: 0.95rem; color: #555; line-height: 1.6; text-align: left !important;"></p>
                    </div>
                    <button id="modalAddBtn" class="btn btn-orange w-100 py-3 fw-bold rounded-pill shadow-sm">Add to Order</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let cart = {};
        let prices = {};
        let names = {}; // Store names for the cart

        // --- PAGE LOAD: Check Status ---
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('status') === 'success') {
                document.getElementById('toastMsg').innerText = "Order Placed Successfully!";
                new bootstrap.Toast(document.getElementById('successToast')).show();
            }
        });

        // --- MODAL LOGIC ---
        function openInfoModal(element) {
            const data = element.dataset; 
            document.getElementById('modalItemName').innerText = data.name;
            document.getElementById('modalDesc').innerText = data.desc;
            document.getElementById('modalPrice').innerText = "₹" + data.price;
            document.getElementById('modalCategory').innerText = data.cat;
            
            // Store name for cart display
            names[data.id] = data.name;

            const typeIcon = document.getElementById('modalTypeIcon');
            if(data.type === 'VEG') {
                typeIcon.className = "item-icon veg me-2"; typeIcon.innerHTML = '<i class="bi bi-circle-fill"></i>';
            } else {
                typeIcon.className = "item-icon non-veg me-2"; typeIcon.innerHTML = '<i class="bi bi-circle-fill"></i>';
            }

            const btn = document.getElementById('modalAddBtn');
            const newBtn = btn.cloneNode(true);
            btn.parentNode.replaceChild(newBtn, btn);
            
            newBtn.onclick = function() {
                showQtyControl(data.id, parseFloat(data.price), data.name);
                var modal = bootstrap.Modal.getInstance(document.getElementById('infoModal'));
                modal.hide();
            };

            var myModal = new bootstrap.Modal(document.getElementById('infoModal'));
            myModal.show();
        }

        function showQtyControl(id, price, name) {
            prices[id] = price;
            if(name) names[id] = name;
            document.getElementById('btn-add-' + id).style.display = 'none';
            document.getElementById('qty-control-' + id).style.display = 'flex';
            if(!cart[id]) {
                cart[id] = 1;
                document.getElementById('qty-val-' + id).innerText = 1;
            }
            updateCartBar();
        }

        function updateQty(id, price, change) {
            if (!cart[id]) cart[id] = 0;
            cart[id] += change;
            if (cart[id] <= 0) {
                delete cart[id];
                document.getElementById('qty-control-' + id).style.display = 'none';
                document.getElementById('btn-add-' + id).style.display = 'inline-block';
            } else {
                document.getElementById('qty-val-' + id).innerText = cart[id];
            }
            updateCartBar();
        }

        function updateCartBar() {
            let totalQty = 0;
            let totalAmount = 0;
            for (let id in cart) {
                totalQty += cart[id];
                totalAmount += (cart[id] * prices[id]);
            }
            document.getElementById('cart-count').innerText = totalQty + " ITEM" + (totalQty !== 1 ? "S" : "");
            document.getElementById('cart-total').innerText = "₹" + totalAmount;
            const bar = document.getElementById('cart-bar');
            if (totalQty > 0) bar.style.display = "flex";
            else bar.style.display = "none";
        }

        // --- OPEN DRAWER ---
        function openCartDrawer() {
            const list = document.getElementById('cart-items-list');
            list.innerHTML = ''; 
            let grandTotal = 0;
            for (let id in cart) {
                const qty = cart[id];
                const price = prices[id];
                const itemTotal = qty * price;
                grandTotal += itemTotal;
                // Name fallback if not clicked via modal
                const itemName = names[id] || ("Item #" + id);
                
                list.insertAdjacentHTML('beforeend', `
                    <div class="cart-item-row">
                        <div><div class="cart-item-name">`+itemName+`</div><div class="small text-muted">₹`+price+` x `+qty+`</div></div>
                        <div class="cart-item-price">₹`+itemTotal+`</div>
                    </div>
                `);
            }
            document.getElementById('drawer-total').innerText = "₹" + grandTotal;
            new bootstrap.Offcanvas(document.getElementById('cartOffcanvas')).show();
        }

        // --- PAYMENT ---
        function initiatePayment() {
            const btn = document.getElementById('payBtn');
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Processing...';
            
            const params = new URLSearchParams();
            params.append("pnr", "${pnr}");
            params.append("contactName", document.getElementById('contactName').value);
            params.append("contactPhone", document.getElementById('contactPhone').value);
            params.append("cartJson", JSON.stringify(cart));
            
            fetch('${pageContext.request.contextPath}/FoodStripeSessionServlet', {
                method: 'POST', body: params
            })
            .then(res => res.json())
            .then(data => {
                if(data.error) throw new Error(data.error);
                window.location.href = data.url;
            })
            .catch(err => {
                console.error(err);
                alert("Payment Error: " + err.message);
                btn.disabled = false;
                btn.innerText = "PAY NOW";
            });
        }
    </script>

</body>
</html>