import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Header.css';

const Header = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAuthenticated, logout, clearAuth } = useAuth();

  // Debug logging to understand the auth state
  useEffect(() => {
    console.log('Header Auth State:', { user, isAuthenticated });
  }, [user, isAuthenticated]);

  // Items to show after authentication only
  const navItems = [
    { path: '/submit-paper', label: 'Submit Paper', protected: true },
    { path: '/verify-paper', label: 'Verify Paper', protected: true },
    { path: '/paper', label: 'Research Papers', protected: true },
    { path: '/dashboard', label: 'Dashboard', protected: true },
    { path: '/about', label: 'About', protected: true }
  ];

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsMenuOpen(false);
  };

  const handleClearAuth = () => {
    clearAuth();
    console.log('Authentication cleared manually');
  };

  const handleAuthClick = (path) => {
    if (path === '/login' || path === '/signup') {
      // Store the current location to redirect back after auth
      navigate(path, { state: { from: location } });
    }
  };

  const handleProtectedNavClick = (e, item) => {
    if (item.protected && !isAuthenticated) {
      e.preventDefault();
      // Show auth prompt for protected navigation items
      if (item.path === '/submit-paper') {
        navigate('/login', { state: { from: { pathname: '/submit-paper' } } });
      } else if (item.path === '/verify-paper') {
        navigate('/login', { state: { from: { pathname: '/verify-paper' } } });
      } else if (item.path === '/paper') {
        navigate('/login', { state: { from: { pathname: '/paper' } } });
      } else if (item.path === '/dashboard') {
        navigate('/login', { state: { from: { pathname: '/dashboard' } } });
      }
    }
  };

  // Clear any existing auth data on component mount (for debugging)
  useEffect(() => {
    // Check if there's any leftover auth data and clear it if needed
    const savedUser = localStorage.getItem('user');
    if (savedUser && !isAuthenticated) {
      console.log('Found leftover user data, clearing...');
      localStorage.removeItem('user');
    }
  }, [isAuthenticated]);

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          {/* Logo */}
          <div className="logo">
            <Link to="/">
              <div className="logo-content">
                <span className="logo-icon">🎓</span>
                <h2>AuthGuard</h2>
              </div>
            </Link>
          </div>

          {/* Navigation Menu - visible only when authenticated */}
          {isAuthenticated && (
            <nav className={`nav-menu ${isMenuOpen ? 'active' : ''}`}>
              <ul className="nav-list">
                {navItems.map((item) => (
                  <li key={item.path} className="nav-item">
                    <Link
                      to={item.path}
                      className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
                      onClick={(e) => {
                        handleProtectedNavClick(e, item);
                        setIsMenuOpen(false);
                      }}
                    >
                      {item.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </nav>
          )}

          {/* Auth Buttons */
          }
          <div className="auth-buttons">
            {isAuthenticated && user ? (
              <>
                <button onClick={handleLogout} className="btn btn-secondary">
                  Logout
                </button>
              </>
            ) : (
              <>
                <button 
                  onClick={() => handleAuthClick('/login')} 
                  className="btn btn-secondary"
                >
                  Login
                </button>
                <button 
                  onClick={() => handleAuthClick('/signup')} 
                  className="btn btn-primary"
                >
                  Sign Up
                </button>
              </>
            )}
          </div>

          {/* Mobile Menu Toggle */}
          <div className="mobile-menu-toggle" onClick={toggleMenu}>
            <span className={`hamburger ${isMenuOpen ? 'active' : ''}`}></span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
