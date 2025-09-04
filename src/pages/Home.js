import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Home.css';

const Home = () => {
  const { isAuthenticated, user } = useAuth();
  const navigate = useNavigate();
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [intendedAction, setIntendedAction] = useState('');

  // Debug: Check localStorage
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    console.log('Home: localStorage user data:', savedUser);
    console.log('Home: Auth context state:', { isAuthenticated, user });
  }, [isAuthenticated, user]);

  const handleProtectedAction = (action, path) => {
    if (isAuthenticated) {
      navigate(path);
    } else {
      setIntendedAction(action);
      setShowAuthModal(true);
    }
  };

  const handleAuthChoice = (choice) => {
    setShowAuthModal(false);
    if (choice === 'login') {
      navigate('/login', { state: { from: { pathname: intendedAction === 'submit' ? '/submit-paper' : intendedAction === 'verify' ? '/verify-paper' : '/paper' } } });
    } else {
      navigate('/signup', { state: { from: { pathname: intendedAction === 'submit' ? '/submit-paper' : intendedAction === 'verify' ? '/verify-paper' : '/paper' } } });
    }
  };

  return (
    <div className="home">
      {/* Debug Section - Remove this later */}
      <div style={{ 
        background: '#f0f0f0', 
        padding: '10px', 
        margin: '10px', 
        borderRadius: '5px', 
        fontSize: '12px',
        position: 'fixed',
        top: '100px',
        right: '10px',
        zIndex: 1000,
        maxWidth: '300px'
      }}>
        <strong>Debug Info:</strong><br/>
        Auth: {isAuthenticated ? 'Yes' : 'No'}<br/>
        User: {user ? user.name : 'None'}<br/>
        localStorage: {localStorage.getItem('user') ? 'Has data' : 'Empty'}<br/>
        <button 
          onClick={() => {
            localStorage.removeItem('user');
            window.location.reload();
          }}
          style={{marginTop: '5px', padding: '2px 5px'}}
        >
          Clear localStorage
        </button>
      </div>

      {/* Hero Section */}
      <section className="hero">
        <div className="container">
          <div className="hero-content">
            {/* Left Side - Text Content */}
            <div className="hero-text">
              <h1 className="hero-title">
                Protect Your Research with
                <span className="gradient-text"> Blockchain Authentication</span>
              </h1>
              <p className="hero-description">
                Submit your research papers with blockchain hashes to prove authorship and timestamp. 
                Our AI-powered system detects plagiarism before submission, ensuring academic integrity.
              </p>
              <div className="hero-features">
                <div className="feature">
                  <span className="feature-icon">🔒</span>
                  <span>Blockchain Hash Submission</span>
                </div>
                <div className="feature">
                  <span className="feature-icon">🤖</span>
                  <span>AI Plagiarism Detection</span>
                </div>
                <div className="feature">
                  <span className="feature-icon">⏰</span>
                  <span>Timestamp Authentication</span>
                </div>
              </div>
              <div className="hero-buttons">
                <button 
                  onClick={() => handleProtectedAction('submit', '/submit-paper')} 
                  className="btn btn-primary"
                >
                  Submit Research Paper
                </button>
                <button 
                  onClick={() => handleProtectedAction('verify', '/verify-paper')} 
                  className="btn btn-secondary"
                >
                  Verify Paper
                </button>
              </div>
            </div>

            {/* Right Side - Image */}
            <div className="hero-image">
              <div className="image-container">
                <div className="floating-card card-1">
                  <div className="card-icon">📄</div>
                  <div className="card-text">
                    <h4>Research Paper</h4>
                    <p>Submit with hash</p>
                  </div>
                </div>
                <div className="floating-card card-2">
                  <div className="card-icon">🔗</div>
                  <div className="card-text">
                    <h4>Blockchain Hash</h4>
                    <p>Prove authorship</p>
                  </div>
                </div>
                <div className="floating-card card-3">
                  <div className="card-icon">🤖</div>
                  <div className="card-text">
                    <h4>AI Detection</h4>
                    <p>Check plagiarism</p>
                  </div>
                </div>
                <div className="main-image">
                  <div className="blockchain-visual">
                    <div className="block block-1">
                      <span className="block-text">Paper</span>
                    </div>
                    <div className="block block-2">
                      <span className="block-text">Hash</span>
                    </div>
                    <div className="block block-3">
                      <span className="block-text">AI Check</span>
                    </div>
                    <div className="block block-4">
                      <span className="block-text">Verify</span>
                    </div>
                    <div className="block block-5">
                      <span className="block-text">Store</span>
                    </div>
                    <div className="connection-line line-1"></div>
                    <div className="connection-line line-2"></div>
                    <div className="connection-line line-3"></div>
                    <div className="connection-line line-4"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <div className="container">
          <h2 className="section-title">How It Works</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">📝</div>
              <h3>Submit Research Paper</h3>
              <p>Upload your research paper and generate a unique blockchain hash to establish authorship and timestamp.</p>
              <button 
                onClick={() => handleProtectedAction('submit', '/submit-paper')} 
                className="feature-action-btn"
              >
                Start Submission
              </button>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🤖</div>
              <h3>AI Plagiarism Detection</h3>
              <p>Our advanced AI system scans your paper against millions of academic sources to detect any potential plagiarism.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🔗</div>
              <h3>Blockchain Authentication</h3>
              <p>Your paper's hash is stored on the blockchain, providing immutable proof of authorship and submission time.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">✅</div>
              <h3>Verification & Storage</h3>
              <p>Get instant verification results and store your authenticated research paper securely on the blockchain.</p>
              <button 
                onClick={() => handleProtectedAction('verify', '/verify-paper')} 
                className="feature-action-btn"
              >
                Verify Now
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Research Papers Preview Section */}
      <section className="papers-preview">
        <div className="container">
          <h2 className="section-title">Explore Verified Research Papers</h2>
          <p className="section-subtitle">Discover groundbreaking research authenticated on the blockchain</p>
          <div className="papers-grid">
            <div className="paper-preview-card">
              <div className="paper-icon">📊</div>
              <h3>Machine Learning in Healthcare</h3>
              <p>Advanced algorithms for disease prediction and diagnosis using blockchain-verified data.</p>
              <button 
                onClick={() => handleProtectedAction('explore', '/paper')} 
                className="paper-explore-btn"
              >
                Explore Papers
              </button>
            </div>
            <div className="paper-preview-card">
              <div className="paper-icon">🌱</div>
              <h3>Climate Change Research</h3>
              <p>Environmental studies and sustainability research with timestamped blockchain verification.</p>
              <button 
                onClick={() => handleProtectedAction('explore', '/paper')} 
                className="paper-explore-btn"
              >
                Explore Papers
              </button>
            </div>
            <div className="paper-preview-card">
              <div className="paper-icon">💻</div>
              <h3>Blockchain Technology</h3>
              <p>Innovative applications and research in distributed ledger technology and smart contracts.</p>
              <button 
                onClick={() => handleProtectedAction('explore', '/paper')} 
                className="paper-explore-btn"
              >
                Explore Papers
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="stats-section">
        <div className="container">
          <div className="stats-grid">
            <div className="stat-item">
              <div className="stat-number">10,000+</div>
              <div className="stat-label">Papers Submitted</div>
            </div>
            <div className="stat-item">
              <div className="stat-number">99.9%</div>
              <div className="stat-label">Detection Accuracy</div>
            </div>
            <div className="stat-item">
              <div className="stat-number">50+</div>
              <div className="stat-label">Universities</div>
            </div>
            <div className="stat-item">
              <div className="stat-number">24/7</div>
              <div className="stat-label">AI Monitoring</div>
            </div>
          </div>
        </div>
      </section>

      {/* Authentication Modal */}
      {showAuthModal && (
        <div className="auth-modal-overlay" onClick={() => setShowAuthModal(false)}>
          <div className="auth-modal" onClick={(e) => e.stopPropagation()}>
            <div className="auth-modal-header">
              <h3>🔐 Authentication Required</h3>
              <button 
                className="modal-close-btn" 
                onClick={() => setShowAuthModal(false)}
              >
                ×
              </button>
            </div>
            <div className="auth-modal-content">
              <p>To {intendedAction === 'submit' ? 'submit research papers' : intendedAction === 'verify' ? 'verify papers' : 'explore research papers'}, you need to create an account or sign in.</p>
              <div className="auth-modal-buttons">
                <button 
                  onClick={() => handleAuthChoice('login')} 
                  className="btn btn-primary"
                >
                  Sign In
                </button>
                <button 
                  onClick={() => handleAuthChoice('signup')} 
                  className="btn btn-secondary"
                >
                  Create Account
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
