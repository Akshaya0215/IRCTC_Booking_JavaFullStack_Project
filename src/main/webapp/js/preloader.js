document.addEventListener("DOMContentLoaded", function() {
    
    // === 1. RANDOM FACTS LIBRARY ===
    const irFacts = [
        "Did you know? Indian Railways is the 4th largest rail network in the world.",
        "Trivia: The first train in India ran on 16th April 1853 between Mumbai and Thane.",
        "Fact: IRCTC books over 800,000 tickets every single day!",
        "Wow! The Gorakhpur Railway Station has the world's longest platform (1,366m).",
        "Did you know? The 'Fairy Queen' is the oldest working steam engine in the world.",
        "Trivia: Indian Railways has a mascot named 'Bholu', the guard elephant.",
        "Fact: The Vivek Express covers the longest route: 4,273 km from Dibrugarh to Kanyakumari.",
        "Amazing: Indian Railways carries over 23 million passengers daily (Population of Australia!)",
        "History: The Chhatrapati Shivaji Maharaj Terminus is a UNESCO World Heritage Site.",
        "Tech: IRCTC's website handles over 300,000 concurrent users during Tatkal hours."
    ];

    // === 2. SELECT & INJECT ===
    const textElement = document.querySelector('.loading-text');
    if(textElement) {
        const randomFact = irFacts[Math.floor(Math.random() * irFacts.length)];
        textElement.innerText = randomFact;
    }

    // === 3. HIDE LOGIC (Increased to 3s to allow reading) ===
    setTimeout(hidePreloader, 3000); 
});

// Fallback
window.addEventListener("load", function() {
    // We rely on the timeout above for consistency
});

function hidePreloader() {
    const loader = document.getElementById("preloader");
    if (loader) {
        loader.style.opacity = "0"; // Fade out
        setTimeout(() => {
            loader.style.display = "none"; // Remove
        }, 800); 
    }
}