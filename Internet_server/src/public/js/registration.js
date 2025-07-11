/*
 * registration.js
 * This file contains the JavaScript logic for the user registration page.
 * It handles form submission, image preview for profile pictures, and error handling.
 * The script uses the Fetch API to send registration data to the server and provides user feedback.
 * It also includes functionality to preview an image from a file input or a URL input.
 */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registration-form');
    const errorMessage = document.getElementById('error-message');
    const fileInput = document.getElementById('picture');
    const urlInput = document.getElementById('picture_url');
    const imagePreview = document.getElementById('image-preview');
    const imagePreviewContainer = document.getElementById('image-preview-container');

    // Handle image file selection for preview
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            // Clear URL input when file is selected
            urlInput.value = '';
            
            const reader = new FileReader();
            reader.onload = (e) => {
                imagePreview.src = e.target.result;
                imagePreviewContainer.classList.remove('d-none');
            };
            reader.readAsDataURL(file);
        } else {
            imagePreviewContainer.classList.add('d-none');
        }
    });

    // Handle URL input for preview
    urlInput.addEventListener('input', (e) => {
        const url = e.target.value;
        if (url) {
            // Clear file input when URL is entered
            fileInput.value = '';
            
            imagePreview.src = url;
            imagePreview.onerror = () => {
                imagePreviewContainer.classList.add('d-none');
                imagePreview.onerror = null;
            };
            imagePreview.onload = () => {
                imagePreviewContainer.classList.remove('d-none');
                imagePreview.onload = null;
            };
        } else {
            imagePreviewContainer.classList.add('d-none');
        }
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Clear previous error messages
        errorMessage.classList.add('d-none');
        
        try {
            let pictureData = urlInput.value;
            
            // If file is uploaded, convert to base64 for submission
            if (fileInput.files && fileInput.files[0]) {
                const file = fileInput.files[0];
                pictureData = await convertFileToBase64(file);
            }
            
            // Get form data
            const formData = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value,
                first_name: document.getElementById('first_name').value,
                sur_name: document.getElementById('sur_name').value,
                picture: pictureData || "" // Use base64 data or URL or empty string
            };
            
            // Send registration request to API
            const response = await fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Registration failed'); // If the response is unsuccessful, throw an error message
            }
            
            // Registration successful - redirect to login page or show success message
            alert('Registration successful! You can now log in.');
            window.location.href = 'login.html'; // Redirect to login page
            
        } catch (error) {
            // Show error message
            errorMessage.textContent = error.message;
            errorMessage.classList.remove('d-none'); // Remove 'd-none' class to display the error message
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