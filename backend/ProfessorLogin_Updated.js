import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './ProfessorLogin.css';
import ProfessorAuthService from '../services/professorAuth.service';

const ProfessorLogin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  
  const [formValues, setFormValues] = useState({
    professorId: '',
    password: ''
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');

  // Get the intended destination from location state, or default to professor dashboard
  const from = location.state?.from?.pathname || '/professor-dashboard';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues((prev) => ({ ...prev, [name]: value }));
    // Clear errors when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const nextErrors = {};
    if (!formValues.professorId) nextErrors.professorId = 'Professor ID is required';
    if (!formValues.password) nextErrors.password = 'Password is required';
    return nextErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    
    if (Object.keys(nextErrors).length > 0) return;

    setIsSubmitting(true);
    setServerError('');

    try {
      // Convert professor ID to email format for backend API
      const email = `${formValues.professorId}@jnu.ac.in`;
      
      // Call the backend API
      const professorData = await ProfessorAuthService.loginProfessor(email, formValues.password);
      
      if (professorData && professorData.token) {
        await login(email, formValues.password); // Call AuthContext login
        navigate(from, { replace: true });
      } else {
        throw new Error('Invalid response from server');
      }
    } catch (error) {
      console.error('Professor login error:', error);
      setServerError(error.response?.data?.message || 'Invalid Professor ID or password. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="container">
        <div className="auth-card professor-auth-card">
          <div className="professor-header">
            <div className="professor-icon">👨‍🏫</div>
            <h2 className="auth-title">Professor Login</h2>
            <p className="auth-subtitle">
              Access your academic dashboard and manage research activities
            </p>
          </div>

          {/* Professor Info Banner */}
          <div className="professor-info-banner">
            <div className="info-item">
              <span className="info-icon">🎓</span>
              <span>Academic Access</span>
            </div>
            <div className="info-item">
              <span className="info-icon">📚</span>
              <span>Research Management</span>
            </div>
            <div className="info-item">
              <span className="info-icon">👥</span>
              <span>Student Supervision</span>
            </div>
          </div>

          {serverError && (
            <div className="error-message">
              {serverError}
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className={`form-group ${errors.professorId ? 'has-error' : ''}`}>
              <label htmlFor="professorId">Professor ID</label>
              <input
                id="professorId"
                name="professorId"
                type="text"
                placeholder="Enter your professor ID (e.g., prof.sharma)"
                value={formValues.professorId}
                onChange={handleChange}
                autoComplete="username"
              />
              {errors.professorId && <span className="error-text">{errors.professorId}</span>}
              <small className="field-hint">Your professor ID will be converted to email format automatically</small>
            </div>

            <div className={`form-group ${errors.password ? 'has-error' : ''}`}>
              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                placeholder="Enter your password"
                value={formValues.password}
                onChange={handleChange}
                autoComplete="current-password"
              />
              {errors.password && <span className="error-text">{errors.password}</span>}
            </div>

            <button className="btn btn-primary auth-submit professor-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Authenticating...' : 'Login as Professor'}
            </button>

            <div className="professor-demo-info">
              <h4>Demo Credentials:</h4>
              <div className="demo-credentials">
                <div className="demo-item">
                  <p><strong>Professor ID:</strong> prof.sharma</p>
                  <p><strong>Password:</strong> 1234</p>
                </div>
                <div className="demo-item">
                  <p><strong>Professor ID:</strong> prof.singh</p>
                  <p><strong>Password:</strong> 1234</p>
                </div>
                <div className="demo-item">
                  <p><strong>Professor ID:</strong> prof.kumar</p>
                  <p><strong>Password:</strong> 1234</p>
                </div>
              </div>
              <p className="demo-note">
                <strong>Note:</strong> Professor ID "prof.sharma" becomes "prof.sharma@jnu.ac.in" for authentication
              </p>
            </div>
          </form>

          <div className="auth-alt">
            <span>Not a professor?</span>
            <Link to="/login" className="auth-link">Regular Login</Link>
            <span className="auth-divider">|</span>
            <Link to="/admin-login" className="auth-link">Admin Login</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfessorLogin;