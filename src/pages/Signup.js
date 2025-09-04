import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Signup.css';

const Signup = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { signup } = useAuth();
  const [formValues, setFormValues] = useState({ name: '', email: '', password: '', confirmPassword: '' });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Get the intended destination from location state, or default to dashboard
  const from = location.state?.from?.pathname || '/dashboard';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues((prev) => ({ ...prev, [name]: value }));
  };

  const validate = () => {
    const nextErrors = {};
    if (!formValues.name) nextErrors.name = 'Name is required';
    if (!formValues.email) nextErrors.email = 'Email is required';
    if (!formValues.password) nextErrors.password = 'Password is required';
    if (formValues.password && formValues.password.length < 6) nextErrors.password = 'Min 6 characters';
    if (formValues.confirmPassword !== formValues.password) nextErrors.confirmPassword = 'Passwords do not match';
    return nextErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setIsSubmitting(true);
    try {
      await signup(formValues.name, formValues.email, formValues.password);
      // Redirect to the intended page after successful signup
      navigate(from, { replace: true });
    } catch (error) {
      setErrors({ general: 'Signup failed. Please try again.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="container">
        <div className="auth-card">
          <h2 className="auth-title">Create your account</h2>
          <p className="auth-subtitle">Sign up to get started</p>

          {errors.general && (
            <div className="error-message">
              {errors.general}
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className={`form-group ${errors.name ? 'has-error' : ''}`}>
              <label htmlFor="name">Full Name</label>
              <input
                id="name"
                name="name"
                type="text"
                placeholder="Jane Doe"
                value={formValues.name}
                onChange={handleChange}
                autoComplete="name"
              />
              {errors.name && <span className="error-text">{errors.name}</span>}
            </div>

            <div className={`form-group ${errors.email ? 'has-error' : ''}`}>
              <label htmlFor="email">Email</label>
              <input
                id="email"
                name="email"
                type="text"
                placeholder="you@example.com"
                value={formValues.email}
                onChange={handleChange}
                autoComplete="email"
              />
              {errors.email && <span className="error-text">{errors.email}</span>}
            </div>

            <div className={`form-group ${errors.password ? 'has-error' : ''}`}>
              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                placeholder="Create a password"
                value={formValues.password}
                onChange={handleChange}
                autoComplete="new-password"
              />
              {errors.password && <span className="error-text">{errors.password}</span>}
            </div>

            <div className={`form-group ${errors.confirmPassword ? 'has-error' : ''}`}>
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="Re-enter your password"
                value={formValues.confirmPassword}
                onChange={handleChange}
                autoComplete="new-password"
              />
              {errors.confirmPassword && <span className="error-text">{errors.confirmPassword}</span>}
            </div>

            <button className="btn btn-primary auth-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Creating account...' : 'Sign Up'}
            </button>
          </form>

          <div className="auth-alt">
            <span>Already have an account?</span>
            <Link to="/login" className="auth-link">Login</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;


