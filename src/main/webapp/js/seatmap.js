// Configuration for Class Layouts
const CLASS_LAYOUTS = {
    'SL': { type: 'BERTH_3_2', seatsPerBay: 8 }, 
    '3A': { type: 'BERTH_3_2', seatsPerBay: 8 }, 
    '3E': { type: 'BERTH_3_2', seatsPerBay: 8 }, 
    '2A': { type: 'BERTH_2_2', seatsPerBay: 6 }, 
    '1A': { type: 'CABIN_MIX', seatsPerBay: 4 }, // Updated Type
    'CC': { type: 'CHAIR', seatsPerBay: 5 }      
};

let selectedSeats = [];
let maxSeats = 0;
let bookedSeats = []; 

function initSeatMap(classCode, passengerCount, bookedList) {
    maxSeats = passengerCount;
    bookedSeats = bookedList || [];
    
    const activeClass = (classCode && CLASS_LAYOUTS[classCode]) ? classCode : 'SL';
    const layoutConfig = CLASS_LAYOUTS[activeClass];
    const layoutType = layoutConfig.type;
    const seatsPerBay = layoutConfig.seatsPerBay;
    
    const container = document.getElementById('seat-map-container');
    container.innerHTML = ''; 

    // --- LOGIC FOR 1A (CABIN + COUPE MIX) ---
    if (activeClass === '1A') {
        // Standard Pattern: Cabin (4), Cabin (4), Coupe (2) -> Repeat
        // This gives seats 1-4 (A), 5-8 (B), 9-10 (C), etc.
        const structure = ['CABIN', 'CABIN', 'COUPE', 'CABIN', 'CABIN', 'COUPE'];
        let currentSeat = 1;

        structure.forEach(type => {
            if (type === 'CABIN') {
                container.innerHTML += generateCabin(currentSeat, "CABIN");
                currentSeat += 4;
            } else {
                container.innerHTML += generateCoupe(currentSeat, "COUPE");
                currentSeat += 2;
            }
        });
        return; // Exit function for 1A
    }

    // --- LOGIC FOR OTHER CLASSES (STANDARD LOOP) ---
    const totalBays = 9; 
    for (let i = 0; i < totalBays; i++) {
        let bayHtml = '';
        let startSeat = (i * seatsPerBay) + 1;
        
        if (layoutType === 'BERTH_3_2') {
            bayHtml = generateBerth3x2(startSeat);
        } else if (layoutType === 'BERTH_2_2') {
            bayHtml = generateBerth2x2(startSeat);
        }
        container.innerHTML += bayHtml;
    }
}

// --- GENERATORS ---

function generateBerth3x2(start) {
    return `
    <div class="coach-bay">
        <div class="main-berths">
            <div class="berth-column">
                ${renderSeat(start, 'LB')}
                ${renderSeat(start + 1, 'MB')}
                ${renderSeat(start + 2, 'UB')}
            </div>
            <div class="berth-column">
                ${renderSeat(start + 3, 'LB')}
                ${renderSeat(start + 4, 'MB')}
                ${renderSeat(start + 5, 'UB')}
            </div>
        </div>
        <div class="side-berths">
            <div class="berth-column">
                ${renderSeat(start + 7, 'SU')}
                ${renderSeat(start + 6, 'SL')}
            </div>
        </div>
    </div>`;
}

function generateBerth2x2(start) {
    return `
    <div class="coach-bay">
        <div class="main-berths">
            <div class="berth-column">
                ${renderSeat(start, 'LB')}
                ${renderSeat(start + 1, 'UB')}
            </div>
            <div class="berth-column">
                ${renderSeat(start + 2, 'LB')}
                ${renderSeat(start + 3, 'UB')}
            </div>
        </div>
        <div class="side-berths">
            <div class="berth-column">
                ${renderSeat(start + 5, 'SU')}
                ${renderSeat(start + 4, 'SL')}
            </div>
        </div>
    </div>`;
}

// 4-SEATER CABIN (2x2)
function generateCabin(start, labelText) {
    return `
    <div class="coach-bay" style="justify-content: center;">
        <div class="cabin-box">
            <div class="text-center small text-muted mb-2" style="font-weight:bold; letter-spacing:1px;">${labelText}</div>
            <div class="main-berths" style="justify-content: space-between;">
                <div class="berth-column">
                    ${renderSeat(start, 'LB')}
                    ${renderSeat(start + 1, 'UB')}
                </div>
                <div class="berth-column">
                    ${renderSeat(start + 2, 'LB')}
                    ${renderSeat(start + 3, 'UB')}
                </div>
            </div>
        </div>
    </div>`;
}

// 2-SEATER COUPE (1x1) - NEW
function generateCoupe(start, labelText) {
    return `
    <div class="coach-bay" style="justify-content: center;">
        <div class="cabin-box" style="width: 140px;"> <div class="text-center small text-muted mb-2" style="font-weight:bold; letter-spacing:1px;">${labelText}</div>
            <div class="main-berths" style="justify-content: center;">
                <div class="berth-column">
                    ${renderSeat(start, 'LB')}
                    ${renderSeat(start + 1, 'UB')}
                </div>
            </div>
        </div>
    </div>`;
}

function renderSeat(seatNo, label) {
    let statusClass = '';
    if (bookedSeats.includes(seatNo)) {
        statusClass = 'booked';
    }
    return `
    <div class="seat ${statusClass}" 
         data-number="${seatNo}" 
         data-type="${label}"
         onclick="toggleSeat(this)">
        <span class="seat-num">${seatNo}</span>
        <span class="seat-label">${label}</span>
    </div>`;
}

function toggleSeat(element) {
    if (element.classList.contains('booked')) return;

    const seatNo = element.getAttribute('data-number');
    const berthType = element.getAttribute('data-type');

    if (element.classList.contains('selected')) {
        element.classList.remove('selected');
        selectedSeats = selectedSeats.filter(s => s.no !== seatNo);
    } else {
        if (selectedSeats.length < maxSeats) {
            element.classList.add('selected');
            selectedSeats.push({ no: seatNo, type: berthType });
        } else {
            alert(`You can only select ${maxSeats} seats.`);
        }
    }
    updateSelectionDisplay();
}

function updateSelectionDisplay() {
    const display = document.getElementById('selected-seats-display');
    if (selectedSeats.length === 0) {
        display.innerHTML = 'None';
        document.getElementById('confirm-seats-btn').disabled = true;
    } else {
        display.innerHTML = selectedSeats.map(s => `<b>${s.no}</b> (${s.type})`).join(', ');
        document.getElementById('confirm-seats-btn').disabled = (selectedSeats.length !== maxSeats);
    }
    
    const seatString = selectedSeats.map(s => `${s.no}-${s.type}`).join(',');
    document.getElementById('selectedSeatsData').value = seatString;
}	