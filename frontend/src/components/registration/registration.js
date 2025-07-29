import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './registration.css';
import Client from '../../services/Client';
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
			await Client.register(formData);
			alert('Registration successful! You can now log in.');
			navigate('/login');
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
					<img
						src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
						alt="Google"
						style={{ height: '30px' }}
					/>
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
						<button type="submit" className="next-button">Sign Up</button>
					</div>
				</form>
			</div>
		</div>
	);
};

export default Registration;