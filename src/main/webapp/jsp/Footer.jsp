<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    /* 1. Reset Body to allow Sticky Footer */
    html, body {
        height: 100%;
        margin: 0;
    }

    body {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
    }

    /* 2. Main Content Wrapper (Add this class to your main content div if needed) */
    .main-content {
        flex: 1;
    }

    /* 3. Footer Container */
    .irctc-footer {
        width: 100%;
        background-color: #213d77; /* Deep Blue */
        color: #ffffff;
        text-align: center;
        padding: 15px 0;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        box-shadow: 0 -4px 10px rgba(0,0,0,0.2);
        
        /* Sticky Footer Logic: Pushes to bottom naturally */
        margin-top: auto; 
        flex-shrink: 0;
    }

    .footer-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
    }

    /* Label: "Designed & Developed by" */
    .footer-label {
        font-size: 0.8rem;
        color: #cfd8dc; /* Light Gray */
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-bottom: 5px;
    }

    /* Developer Row: Name + Icons */
    .developer-row {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 15px; /* Space between Name and Icons */
        font-size: 0.95rem;
    }

    .dev-name {
        color: #fb792b; /* IRCTC Orange */
        font-weight: 700;
        text-transform: uppercase;
    }

    /* Social Icons Group */
    .social-group {
        display: flex;
        gap: 12px;
    }

    .social-icon {
        color: #ffffff;
        font-size: 1.1rem;
        transition: transform 0.2s, color 0.2s;
        display: flex;
        align-items: center;
        text-decoration: none;
    }

    .social-icon:hover {
        color: #fb792b;
        transform: scale(1.2);
    }

    /* Copyright */
    .copyright {
        font-size: 0.75rem;
        color: #adb5bd;
        margin-top: 10px;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
        padding-top: 8px;
        width: 90%;
        max-width: 350px;
    }

    /* Responsive: Stack nicely on very small screens */
    @media (max-width: 450px) {
        .developer-row {
            flex-direction: column; /* Stack Name above icons on tiny screens */
            gap: 5px;
            margin-bottom: 8px;
        }
    }
</style>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<footer class="irctc-footer">
    <div class="footer-content">
        
        <span class="footer-label">Designed & Developed by</span>

        <div class="developer-row">
            <span class="dev-name">Vardhan Adheli</span>
            <div class="social-group">
                <a href="https://www.linkedin.com/in/vardhanadheli/" target="_blank" class="social-icon" title="Vardhan's LinkedIn">
                    <i class="fab fa-linkedin"></i>
                </a>
                <a href="https://www.instagram.com/vardhan__1506/" target="_blank" class="social-icon" title="Vardhan's Instagram">
                    <i class="fab fa-instagram"></i>
                </a>
            </div>
        </div>

        <div class="developer-row">
            <span class="dev-name">Ashutosh Kumar</span>
            <div class="social-group">
                <a href="https://www.linkedin.com/in/ashutosh-kumar-329699259" target="_blank" class="social-icon" title="Ashutosh's LinkedIn">
                    <i class="fab fa-linkedin"></i>
                </a>
                <a href="https://www.instagram.com/axhutosh_/" target="_blank" class="social-icon" title="Ashutosh's Instagram">
                    <i class="fab fa-instagram"></i>
                </a>
            </div>
        </div>

        <div class="copyright"><b>All rights to us only &copy; 2025</b></div>

    </div>
</footer>