/*
 * ErrorDisplay.js
 * This module defines an ErrorDisplay class that renders error messages.
 * It includes methods to show and hide error messages.
 */
class ErrorDisplay {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.render();
    }
    // Method to render the error display
    render() {
        this.container.innerHTML = `
            <div class="error-container">
                <div class="error-message" id="error-message"></div>
            </div>
        `;
    }
    // Method to show an error message
    showError(message) {
        const errorElement = this.container.querySelector('#error-message');
        errorElement.textContent = message;
        errorElement.classList.add('active');
    }
    // Method to hide the error message
    hideError() {
        const errorElement = this.container.querySelector('#error-message');
        errorElement.textContent = '';
        errorElement.classList.remove('active');
    }
}