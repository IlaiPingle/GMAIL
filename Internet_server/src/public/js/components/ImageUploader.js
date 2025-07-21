/*
 * ImageUploader Component
 * This component allows users to upload a profile picture either from their device or by entering a URL
 * It provides a preview of the selected image.
 * The component is designed to be used in a web application where users can manage their profile settings
 * and customize their profile picture.
 */
class ImageUploader {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.render();
        this.setupEventListeners();
    }
    // Render the component HTML structure
    render() {
        this.container.innerHTML = `
            <div class="image-uploader">
                <h3>Profile Picture</h3>
                <div class="upload-options">
                    <div class="upload-file">
                        <label for="picture" class="file-label">
                            <span class="material-icon">upload file:</span>
                            <span>Choose from device</span>
                        </label>
                        <input type="file" id="picture" name="picture" accept="image/*" class="file-input">
                    </div>
                    
                    <div class="upload-url">
                        <label for="picture_url">Or enter an image URL:</label>
                        <input type="url" id="picture_url" name="picture_url" placeholder="https://example.com/image.jpg">
                    </div>
                </div>
                
                <div id="image-preview-container" class="preview-container">
                    <img id="image-preview" alt="Profile picture preview">
                </div>
            </div>
        `;
    }
    // Setup event listeners for file input and URL input
    setupEventListeners() {
        const fileInput = this.container.querySelector('#picture');
        const urlInput = this.container.querySelector('#picture_url');
        const imagePreview = this.container.querySelector('#image-preview');
        const previewContainer = this.container.querySelector('#image-preview-container');
        
        // Handle image file selection
        fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                urlInput.value = '';
                
                const reader = new FileReader();
                reader.onload = (e) => {
                    imagePreview.src = e.target.result;
                    previewContainer.classList.add('active');
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.classList.remove('active');
            }
        });
        
        // Handle URL input
        urlInput.addEventListener('input', (e) => {
            const url = e.target.value;
            if (url) {
                fileInput.value = '';
                
                imagePreview.src = url;
                imagePreview.onerror = () => {
                    previewContainer.classList.remove('active');
                    imagePreview.onerror = null;
                };
                imagePreview.onload = () => {
                    previewContainer.classList.add('active');
                    imagePreview.onload = null;
                };
            } else {
                previewContainer.classList.remove('active');
            }
        });
    }
    // Get the image data from the input fields
    getImageData() {
        const fileInput = this.container.querySelector('#picture');
        const urlInput = this.container.querySelector('#picture_url');
        
        if (fileInput.files && fileInput.files[0]) {
            return fileInput.files[0];
        }
        
        return urlInput.value;
    }
}