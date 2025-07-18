/*
 * Registration Page JavaScript
 * This script handles the registration form functionality including input validation,
 * image upload, and form submission.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Initialize components
    const formHeader = new FormHeader('form-header');
    
    const firstNameInput = new FormInput('first-name-input', {
        id: 'first_name',
        label: 'First name',
        required: true,
        autocomplete: 'given-name'
    });
    
    const lastNameInput = new FormInput('last-name-input', {
        id: 'sur_name',
        label: 'Last name',
        required: true,
        autocomplete: 'family-name'
    });
    
    const usernameInput = new FormInput('username-input', {
        id: 'username',
        label: 'Username',
        required: true,
        autocomplete: 'username'
    });
    
    const passwordInput = new FormInput('password-input', {
        id: 'password',
        type: 'password',
        label: 'Password',
        required: true,
        autocomplete: 'new-password'
    });
    
    const imageUploader = new ImageUploader('image-uploader');
    
    const errorDisplay = new ErrorDisplay('error-display');
    
    const formActions = new FormActions('form-actions');
    
    // Form submission logic
    const form = document.getElementById('registration-form');
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Clear previous errors
        errorDisplay.hideError();
        
        try {
            // Get form values
            const formData = {
                username: usernameInput.getValue(),
                password: passwordInput.getValue(),
                first_name: firstNameInput.getValue(),
                sur_name: lastNameInput.getValue(),
                picture: ""
            };
            
            // Handle image data
            const imageData = imageUploader.getImageData();
            if (imageData) {
                if (typeof imageData === 'string') {
                    // If it's a URL
                    formData.picture = imageData;
                } else {
                    // If it's a File, convert to base64
                    formData.picture = await convertFileToBase64(imageData);
                }
            }
            
            // Send registration request
            const response = await fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Registration failed');
            }
            
            // Success - redirect or show success message
            alert('Registration successful! You can now log in.');
            window.location.href = 'login.html';
            
        } catch (error) {
            // Show error message
            errorDisplay.showError(error.message);
        }
    });
    
    // Function to convert file to base64
    function convertFileToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
            reader.readAsDataURL(file);
        });
    }
});