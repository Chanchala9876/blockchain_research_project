import React, { useState } from 'react';
import './VerifyPaper.css';

const VerifyPaper = () => {
  const [verificationData, setVerificationData] = useState({
    hash: '',
    title: '',
    author: ''
  });
  const [verificationResult, setVerificationResult] = useState(null);
  const [isVerifying, setIsVerifying] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setVerificationData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const verifyPaper = async () => {
    if (!verificationData.hash && !verificationData.title) {
      alert('Please provide either a blockchain hash or paper title');
      return;
    }

    setIsVerifying(true);
    // Simulate blockchain verification
    setTimeout(() => {
      const result = {
        verified: Math.random() > 0.2, // 80% chance of being verified
        paper: {
          title: verificationData.title || 'Blockchain Applications in Academic Research',
          authors: ['Dr. Sarah Johnson', 'Prof. Michael Chen'],
          institution: 'Stanford University',
          submissionDate: '2024-01-15T10:30:00Z',
          blockchainHash: verificationData.hash || '0x1234567890abcdef...',
          blockNumber: Math.floor(Math.random() * 1000000),
          timestamp: '2024-01-15T10:30:00Z'
        },
        plagiarismScore: Math.floor(Math.random() * 10),
        authenticity: Math.random() > 0.1
      };
      setVerificationResult(result);
      setIsVerifying(false);
    }, 2000);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="verify-paper-page">
      <div className="container">
        <div className="page-header">
          <h1>Verify Research Paper</h1>
          <p>Verify the authenticity and authorship of research papers using blockchain technology</p>
        </div>

        <div className="verify-content">
          <div className="search-section">
            <h2>Search & Verify</h2>
            <div className="search-form">
              <div className="form-group">
                <label>Blockchain Hash</label>
                <input
                  type="text"
                  name="hash"
                  value={verificationData.hash}
                  onChange={handleInputChange}
                  placeholder="Enter blockchain hash (0x...)"
                />
              </div>

              <div className="form-group">
                <label>Paper Title</label>
                <input
                  type="text"
                  name="title"
                  value={verificationData.title}
                  onChange={handleInputChange}
                  placeholder="Enter paper title to search"
                />
              </div>

              <div className="form-group">
                <label>Author Name</label>
                <input
                  type="text"
                  name="author"
                  value={verificationData.author}
                  onChange={handleInputChange}
                  placeholder="Enter author name (optional)"
                />
              </div>

              <button 
                onClick={verifyPaper} 
                className="btn btn-primary verify-btn"
                disabled={isVerifying}
              >
                {isVerifying ? 'Verifying...' : 'Verify Paper'}
              </button>
            </div>
          </div>

          <div className="results-section">
            <h2>Verification Results</h2>
            
            {isVerifying && (
              <div className="verifying">
                <div className="spinner"></div>
                <p>Verifying paper on blockchain...</p>
              </div>
            )}

            {verificationResult && (
              <div className="verification-result">
                <div className={`status-card ${verificationResult.verified ? 'verified' : 'not-verified'}`}>
                  <div className="status-icon">
                    {verificationResult.verified ? '✅' : '❌'}
                  </div>
                  <div className="status-content">
                    <h3>{verificationResult.verified ? 'Paper Verified' : 'Paper Not Found'}</h3>
                    <p>
                      {verificationResult.verified 
                        ? 'This paper has been verified on the blockchain and is authentic.'
                        : 'This paper was not found in our blockchain database.'
                      }
                    </p>
                  </div>
                </div>

                {verificationResult.verified && (
                  <div className="paper-details">
                    <h3>Paper Information</h3>
                    <div className="detail-grid">
                      <div className="detail-item">
                        <span className="label">Title:</span>
                        <span className="value">{verificationResult.paper.title}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Authors:</span>
                        <span className="value">{verificationResult.paper.authors.join(', ')}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Institution:</span>
                        <span className="value">{verificationResult.paper.institution}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Submission Date:</span>
                        <span className="value">{formatDate(verificationResult.paper.submissionDate)}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Blockchain Hash:</span>
                        <span className="value hash">{verificationResult.paper.blockchainHash}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Block Number:</span>
                        <span className="value">#{verificationResult.paper.blockNumber}</span>
                      </div>
                    </div>

                    <div className="metrics">
                      <div className="metric">
                        <div className="metric-label">Plagiarism Score</div>
                        <div className="metric-value">{verificationResult.plagiarismScore}%</div>
                        <div className="metric-status">
                          {verificationResult.plagiarismScore < 5 ? 'Excellent' : 'Good'}
                        </div>
                      </div>
                      <div className="metric">
                        <div className="metric-label">Authenticity</div>
                        <div className="metric-value">
                          {verificationResult.authenticity ? '100%' : '95%'}
                        </div>
                        <div className="metric-status">
                          {verificationResult.authenticity ? 'Verified' : 'Likely Authentic'}
                        </div>
                      </div>
                    </div>

                    <div className="blockchain-info">
                      <h4>Blockchain Information</h4>
                      <div className="block-info">
                        <div className="block-item">
                          <span className="block-label">Transaction Hash:</span>
                          <span className="block-value">0x{Math.random().toString(16).substr(2, 64)}</span>
                        </div>
                        <div className="block-item">
                          <span className="block-label">Gas Used:</span>
                          <span className="block-value">{Math.floor(Math.random() * 50000) + 20000}</span>
                        </div>
                        <div className="block-item">
                          <span className="block-label">Network:</span>
                          <span className="block-value">Ethereum Mainnet</span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default VerifyPaper;
