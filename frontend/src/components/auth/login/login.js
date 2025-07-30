import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Client from '../../../services/Client';
import './login.css';
import { useUser } from '../../../contexts/UserContext';

const Login = () => {
	const { setUser, user } = useUser(); 
	const navigate = useNavigate();
	const [formData, setFormData] = useState({
		username: '',
		password: ''
	});
	const [errors, setErrors] = useState({
		general: ''
	});
	const [passwordVisible, setPasswordVisible] = useState(false);
	const [rememberMe, setRememberMe] = useState(false);
	//const [loading, setLoading] = useState(false);

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setFormData({
			...formData,
			[name]: value
		});
		if (errors[name]) {
			setErrors({
				...errors,
				[name]: ''
			});
		}
	};

	const togglePasswordVisibility = () => {
		setPasswordVisible(!passwordVisible);
	};

	const validateForm = () => {
		if (!formData.username.trim()) {
			setErrors({
				...errors,
				general: 'Enter your email or username',
			});
			return false;
		}
		if (!formData.password.trim()) {
			setErrors({
				...errors,
				general: 'Enter your password',
			});
			return false;
		}
		return true;
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setErrors({
			...errors,
			general: ''
		});
		if (!validateForm()) {
			return;
		}
		try {
			await Client.login(formData.username, formData.password);
			const currentUser = await Client.getCurrentUser();
			setUser(currentUser);
			navigate('/inbox');
		} catch (error) {
			setErrors({
				...errors,
				general: error.message || 'Login failed'
			});
		}
	};

	useEffect(() => {
		if (user) {
			navigate('/inbox');
		}
	}, [user]);

	return (
		<div className="login-container">
			<div className="login-content">
				{/* Google Logo */}
				<div className="google-logo">
					<img
					src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
					alt="Google"
					style={{ height: '30px' }}
					/>
				</div>

				<h1 className="login-title">Sign in</h1>
				<p className="login-subtitle">to continue to Gmail</p>

				<form onSubmit={handleSubmit} className="login-form">
					<div className="form-group">
						<input
							type="text"
							id="username"
							name="username"
							value={formData.username}
							onChange={handleInputChange}
							required
							placeholder=" "
							className={errors.username ? "input-error" : ""}
						/>
						<label htmlFor="username">Email or username</label>
						{errors.username && <div className="field-error">{errors.username}</div>}
					</div>

					<div className="form-group">
						<input
							type={passwordVisible ? "text" : "password"}
							id="password"
							name="password"
							value={formData.password}
							onChange={handleInputChange}
							required
							placeholder=" "
							className={errors.password ? "input-error" : ""}
						/>
						<label htmlFor="password">Password</label>
						{errors.password && <div className="field error">{errors.password}</div>}
					</div>

					<div className="show-password">
						<input
							type="checkbox"
							id="showPassword"
							checked={passwordVisible}
							onChange={togglePasswordVisibility}
						/>
						<label htmlFor="showPassword">Show password</label>
					</div>

					{errors.general && <div className="error message">{errors.general}</div>}

					<div className="forgot-password">
						<a href="/forgot-password">Forgot password?</a>
					</div>

					<div className="form-actions">
						<button
							type="button"
							className="create-account-link"
							onClick={() => navigate('/register')}
						>
							Create account
						</button>
						<button type="submit" className="next-button">Sign in</button>
					</div>
					<div className="remember-me">
						<input
							type="checkbox"
							id="rememberMe"
							checked={rememberMe}
							onChange={() => setRememberMe(!rememberMe)}
						/>
						<label htmlFor="rememberMe">Remember me</label>
					</div>
				</form>
			</div>
		</div>
	);
};

export default Login;