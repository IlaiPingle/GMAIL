import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './registration.css';

const Registration = () => {
	const navigate = useNavigate();

	const [formData, setFormData] = useState({
		first_name: '',
		sur_name: '',
		username: '',
		password: '',
		confirm_password: '',
		picture: ''
	});

	const [passwordVisible, setPasswordVisible] = useState(false);
	const [error, setError] = useState('');
	const [imagePreview, setImagePreview] = useState(null);
	const [passwordValidation, setPasswordValidation] = useState({
		valid: false,
		minLength: false,
		hasNumber: false,
		hasUpperCase: false,
		hasLowerCase: false,
		hasSymbol: false
	});

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setFormData({
			...formData,
			[name]: value
		});
		if (name === 'password') {
			setPasswordValidation(validatePassword(value));
		}
	};

	const handleImageUpload = (e) => {
		const file = e.target.files[0];
		if (file) {
			const previewUrl = URL.createObjectURL(file);
			setImagePreview(previewUrl);

			const reader = new FileReader();
			reader.onload = () => {
				setFormData({
					...formData,
					picture: reader.result
				});
			};
			reader.readAsDataURL(file);
		}
	};

	const togglePasswordVisibility = () => {
		setPasswordVisible(!passwordVisible);
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setError('');

		// Check if password meets requirements
		if (!passwordValidation.valid) {
			setError('Password does not meet the requirements');
			return;
		}

		// Validation
		if (formData.password !== formData.confirm_password) {
			setError("Passwords don't match");
			return;
		}

		try {
			const response = await fetch('http://localhost:8080/api/users', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(formData)
			});

			const data = await response.json();

			if (!response.ok) {
				throw new Error(data.message || 'Registration failed');
			}

			// Success - redirect to login/inbox
			alert('Registration successful! You can now log in.');
			navigate('/inbox');

		} catch (error) {
			setError(error.message);
		}
	};

	const validatePassword = (password) => {
		const minLength = password.length >= 8;
		const hasNumber = /\d/.test(password);
		const hasUpperCase = /[A-Z]/.test(password);
		const hasLowerCase = /[a-z]/.test(password);
		const hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(password);
		return {
			valid: minLength && hasNumber && hasUpperCase && hasLowerCase && hasSymbol,
			minLength,
			hasNumber,
			hasUpperCase,
			hasLowerCase,
			hasSymbol
		};
	};

	return (
		<div className="registration-container">
			<div className="registration-content">
				{/* Google Logo */}
				<div className="google-logo">
					<svg viewBox="0 0 75 24" width="75" height="24" xmlns="http://www.w3.org/2000/svg">
						<g id="qaEJec">
							<path fill="#ea4335" d="M67.954 16.303c-1.33 0-2.278-.608-2.886-1.804l7.967-3.3-.27-.68c-.495-1.33-2.008-3.79-5.102-3.79-3.068 0-5.622 2.41-5.622 5.96 0 3.34 2.53 5.96 5.92 5.96 2.73 0 4.31-1.67 4.97-2.64l-2.03-1.35c-.673.98-1.6 1.64-2.93 1.64zm-.203-7.27c1.04 0 1.92.52 2.21 1.264l-5.32 2.21c-.06-2.3 1.79-3.474 3.12-3.474z"></path>
						</g>
						<g id="YGlOvc">
							<path fill="#34a853" d="M58.193.67h2.564v17.44h-2.564z"></path>
						</g>
						<g id="BWfIk">
							<path fill="#4285f4" d="M54.152 8.066h-.088c-.588-.697-1.716-1.33-3.136-1.33-2.98 0-5.71 2.614-5.71 5.98 0 3.338 2.73 5.933 5.71 5.933 1.42 0 2.548-.64 3.136-1.36h.088v.86c0 2.28-1.217 3.5-3.183 3.5-1.61 0-2.6-1.15-3-2.12l-2.28.94c.65 1.58 2.39 3.52 5.28 3.52 3.06 0 5.66-1.807 5.66-6.206V7.21h-2.48v.858zm-3.006 8.237c-1.804 0-3.318-1.513-3.318-3.588 0-2.1 1.514-3.635 3.318-3.635 1.784 0 3.183 1.534 3.183 3.635 0 2.075-1.4 3.588-3.19 3.588z"></path>
						</g>
						<g id="e6m3fd">
							<path fill="#fbbc05" d="M38.17 6.735c-3.28 0-5.953 2.506-5.953 5.96 0 3.432 2.673 5.96 5.954 5.96 3.29 0 5.96-2.528 5.96-5.96 0-3.46-2.67-5.96-5.95-5.96zm0 9.568c-1.798 0-3.348-1.487-3.348-3.61 0-2.14 1.55-3.608 3.35-3.608s3.348 1.467 3.348 3.61c0 2.116-1.55 3.608-3.35 3.608z"></path>
						</g>
						<g id="vbkDmc">
							<path fill="#ea4335" d="M25.17 6.71c-3.28 0-5.954 2.505-5.954 5.958 0 3.433 2.673 5.96 5.954 5.96 3.282 0 5.955-2.527 5.955-5.96 0-3.453-2.673-5.96-5.955-5.96zm0 9.567c-1.8 0-3.35-1.487-3.35-3.61 0-2.14 1.55-3.608 3.35-3.608s3.35 1.46 3.35 3.6c0 2.12-1.55 3.61-3.35 3.61z"></path>
						</g>
						<g id="idEJde">
							<path fill="#4285f4" d="M14.11 14.182c.722-.723 1.205-1.78 1.387-3.334H9.423V8.373h8.518c.09.452.16 1.07.16 1.664 0 1.903-.52 4.26-2.19 5.934-1.63 1.7-3.71 2.61-6.48 2.61-5.12 0-9.42-4.17-9.42-9.29C0 4.17 4.31 0 9.43 0c2.83 0 4.843 1.108 6.362 2.56L14 4.347c-1.087-1.02-2.56-1.81-4.577-1.81-3.74 0-6.662 3.01-6.662 6.75s2.93 6.75 6.67 6.75c2.43 0 3.81-.972 4.69-1.856z"></path>
						</g>
					</svg>
				</div>

				<h1 className="registration-title">Create your Google Account</h1>
				<p className="registration-subtitle">to continue to Gmail</p>

				<form onSubmit={handleSubmit} className="registration-form">
					<div className="name-row">
						<div className="form-group">
							<input
								type="text"
								id="first_name"
								name="first_name"
								value={formData.first_name}
								onChange={handleInputChange}
								required
								placeholder=" "
							/>
							<label htmlFor="first_name">First name</label>
						</div>

						<div className="form-group">
							<input
								type="text"
								id="sur_name"
								name="sur_name"
								value={formData.sur_name}
								onChange={handleInputChange}
								required
								placeholder=" "
							/>
							<label htmlFor="sur_name">Last name</label>
						</div>
					</div>

					<div className="form-group">
						<input
							type="text"
							id="username"
							name="username"
							value={formData.username}
							onChange={handleInputChange}
							required
							placeholder=" "
						/>
						<label htmlFor="username">Username</label>
						<div className="input-hint">You can use letters, numbers & periods</div>
					</div>

					<div className="password-row">
						<div className="form-group">
							<input
								type={passwordVisible ? "text" : "password"}
								id="password"
								name="password"
								value={formData.password}
								onChange={handleInputChange}
								required
								placeholder=" "
							/>
							<label htmlFor="password">Password</label>
						</div>

						<div className="form-group">
							<input
								type={passwordVisible ? "text" : "password"}
								id="confirm_password"
								name="confirm_password"
								value={formData.confirm_password}
								onChange={handleInputChange}
								required
								placeholder=" "
							/>
							<label htmlFor="confirm_password">Confirm</label>
						</div>
					</div>

					<div className="show-password">
						<input
							type="checkbox"
							id="show-password"
							onChange={togglePasswordVisibility}
							checked={passwordVisible}
						/>
						<label htmlFor="show-password">Show password</label>
					</div>

					<div className="password-requirements">
						<p>Password must:</p>
						<ul>
							<li className={passwordValidation.minLength ? "valid" : "invalid"}>
								Have at least 8 characters
							</li>
							<li className={passwordValidation.hasUpperCase ? "valid" : "invalid"}>
								Have at least 1 uppercase letter
							</li>
							<li className={passwordValidation.hasLowerCase ? "valid" : "invalid"}>
								Have at least 1 lowercase letter
							</li>
							<li className={passwordValidation.hasNumber ? "valid" : "invalid"}>
								Have at least 1 number
							</li>
							<li className={passwordValidation.hasSymbol ? "valid" : "invalid"}>
								Have at least 1 special character (!@#$%^&*...)
							</li>
						</ul>
					</div>

					<div className="image-uploader">
						<p>Profile picture (optional)</p>
						<div className="upload-container">
							<input
								type="file"
								id="profile-image"
								accept="image/*"
								onChange={handleImageUpload}
								className="file-input"
							/>
							<label htmlFor="profile-image" className="upload-button">
								Choose a file
							</label>
							{imagePreview && (
								<div className="image-preview">
									<img src={imagePreview} alt="Profile preview" />
								</div>
							)}
						</div>
					</div>

					{error && <div className="error-message">{error}</div>}

					<div className="form-actions">
						<button
							type="button"
							className="signin-link"
							onClick={() => navigate('/login')}
						>
							Sign in instead
						</button>
						<button type="submit" className="next-button">Next</button>
					</div>
				</form>
			</div>
		</div>
	);
};

export default Registration;