/*
 * FormActions.js
 * This module defines a FormActions class that renders form action buttons.
 * It includes a link to sign in and a button to proceed to the next step.
 */
class FormActions {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.render();
    }
    // Method to render the form actions
    render() {
        this.container.innerHTML = `
            <div class="form-actions">
                <div class="sign-in-link">
                    <a href="login.html">Sign in instead</a>
                </div>
                <button type="submit" class="next-button">Next</button>
            </div>
        `;
    }
}