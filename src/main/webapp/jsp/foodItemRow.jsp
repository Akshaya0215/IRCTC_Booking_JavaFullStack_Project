<div class="menu-item">
    <div class="item-icon ${item.type == 'VEG' ? 'veg' : 'non-veg'}">
        <i class="bi bi-circle-fill"></i>
    </div>
    
    <div class="item-content">
        <%-- CLICKABLE TITLE WITH DATA ATTRIBUTES --%>
        <div class="item-title" 
             data-id="${item.itemId}"
             data-name="${item.itemName}"
             data-desc="${item.description}"
             data-price="${item.price}"
             data-type="${item.type}"
             data-cat="${item.category}"
             onclick="openInfoModal(this)">
            ${item.itemName} <i class="bi bi-info-circle-fill text-muted small ms-1" style="font-size: 0.7em;"></i>
        </div>
        <div class="item-desc">${item.description}</div>
    </div>
    
    <div class="item-action">
        <span class="item-price">&#8377;${item.price}</span>
        
        <button id="btn-add-${item.itemId}" class="btn btn-add" onclick="showQtyControl('${item.itemId}', ${item.price}, '${item.itemName.replace("'", "\\'")}')">
            ADD
        </button>

        <div id="qty-control-${item.itemId}" class="qty-control">
            <button class="qty-btn" onclick="updateQty('${item.itemId}', ${item.price}, -1)">-</button>
            <span id="qty-val-${item.itemId}" class="qty-val">1</span>
            <button class="qty-btn" onclick="updateQty('${item.itemId}', ${item.price}, 1)">+</button>
        </div>
    </div>
</div>