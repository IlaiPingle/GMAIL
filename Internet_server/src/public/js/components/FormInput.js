/*
 * FormInput Component
 * This component creates a form input field with validation and error handling.
 * It can be used for various input types like text, email, password, etc.
 * The component supports features like focus handling, error display, and basic validation.
 */
class FormInput {
    constructor(containerId, config) {
        this.container = document.getElementById(containerId);
        this.config = config;
        this.render();
        this.setupEventListeners();
    }
    // Render the input field based on the provided configuration
    render() {
        const { id, type, label, placeholder, required, name, autocomplete } = this.config;
        
        this.container.innerHTML = `
            <div class="form-input">
                <label for="${id}">${label}</label>
                <input 
                    type="${type || 'text'}" 
                    id="${id}" 
                    name="${name || id}"
                    placeholder="${placeholder || ''}"
                    ${required ? 'required' : ''}
                    ${autocomplete ? `autocomplete="${autocomplete}"` : ''}
                />
                <div class="input-error" id="${id}-error"></div>
            </div>
        `;
    }
    // Setup event listeners for focus, blur, and validation
    setupEventListeners() {
        const input = this.container.querySelector('input');
        
        input.addEventListener('focus', () => {
            this.container.querySelector('.form-input').classList.add('focused');
        });
        
        input.addEventListener('blur', () => {
            if (!input.value) {
                this.container.querySelector('.form-input').classList.remove('focused');
            }
            
            // Basic validation
            if (this.config.required && !input.value) {
                this.showError(`${this.config.label} is required`);
            } else {
                this.hideError();
            }
        });
    }
    // Show error message if validation fails
    showError(message) {
        const errorElement = this.container.querySelector('.input-error');
        errorElement.textContent = message;
        errorElement.style.display = 'block';
        this.container.querySelector('.form-input').classList.add('error');
    }
    // Hide error message when input is valid or cleared
    hideError() {
        const errorElement = this.container.querySelector('.input-error');
        errorElement.textContent = '';
        errorElement.style.display = 'none';
        this.container.querySelector('.form-input').classList.remove('error');
    }
    // Method to get the current value of the input field
    getValue() {
        return this.container.querySelector('input').value;
    }
}