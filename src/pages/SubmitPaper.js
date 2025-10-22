import React, { useState } from 'react';
import './SubmitPaper.css';

const SubmitPaper = () => {
  const [formData, setFormData] = useState({
    title: '',
    authors: '',
    abstract: '',
    keywords: '',
    paperFile: null,
    blockchainHash: '',
    institution: '',
    email: ''
  });

  const [isProcessing, setIsProcessing] = useState(false);
  const [plagiarismResult, setPlagiarismResult] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, files } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: files ? files[0] : value
    }));
  };

  const generateHash = () => {
    // Simulate blockchain hash generation
    const hash = '0x' + Math.random().toString(16).substr(2, 64);
    setFormData(prev => ({
      ...prev,
      blockchainHash: hash
    }));
  };

  const checkPlagiarism = async () => {
    setIsProcessing(true);
    // Simulate AI plagiarism detection
    setTimeout(() => {
      const result = {
        score: Math.floor(Math.random() * 20), // 0-20% similarity
        details: [
          { source: 'Academic Database', similarity: Math.floor(Math.random() * 15) },
          { source: 'Published Papers', similarity: Math.floor(Math.random() * 10) },
          { source: 'Online Sources', similarity: Math.floor(Math.random() * 5) }
        ],
        status: 'clean'
      };
      setPlagiarismResult(result);
      setIsProcessing(false);
    }, 3000);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!plagiarismResult) {
      alert('Please run plagiarism check first');
      return;
    }
    if (plagiarismResult.score > 15) {
      alert('Plagiarism score too high. Please review your paper.');
      return;
    }
    // Submit to blockchain
    alert('Paper submitted successfully to blockchain!');
  };

  return (
    <div className="submit-paper-page">
      <div className="container">
        <div className="page-header">
          <h1>Submit Research Paper</h1>
          <p>Upload your research paper and authenticate it with blockchain technology</p>
        </div>

        <div className="submit-content">
          <div className="form-section">
            <h2>Paper Information</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Paper Title *</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your research paper title"
                />
              </div>

              <div className="form-group">
                <label>Authors *</label>
                <input
                  type="text"
                  name="authors"
                  value={formData.authors}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter author names (comma separated)"
                />
              </div>

              <div className="form-group">
                <label>Abstract *</label>
                <textarea
                  name="abstract"
                  value={formData.abstract}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter paper abstract"
                  rows="4"
                />
              </div>

              <div className="form-group">
                <label>Keywords</label>
                <input
                  type="text"
                  name="keywords"
                  value={formData.keywords}
                  onChange={handleInputChange}
                  placeholder="Enter keywords (comma separated)"
                />
              </div>

              <div className="form-group">
                <label>Institution</label>
                <input
                  type="text"
                  name="institution"
                  value={formData.institution}
                  onChange={handleInputChange}
                  placeholder="Your institution or organization"
                />
              </div>

              <div className="form-group">
                <label>Email *</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                  placeholder="Your email address"
                />
              </div>

              <div className="form-group">
                <label>Upload Paper (PDF) *</label>
                <input
                  type="file"
                  name="paperFile"
                  onChange={handleInputChange}
                  accept=".pdf"
                  required
                />
                <small>Maximum file size: 10MB</small>
              </div>

              <div className="form-group">
                <label>Blockchain Hash</label>
                <div className="hash-input">
                  <input
                    type="text"
                    name="blockchainHash"
                    value={formData.blockchainHash}
                    onChange={handleInputChange}
                    placeholder="Click 'Generate Hash' to create blockchain hash"
                    readOnly
                  />
                  <button type="button" onClick={generateHash} className="btn btn-secondary">
                    Generate Hash
                  </button>
                </div>
              </div>

              <div className="action-buttons">
                <button type="button" onClick={checkPlagiarism} className="btn btn-primary" disabled={isProcessing}>
                  {isProcessing ? 'Checking Plagiarism...' : 'Check Plagiarism'}
                </button>
                <button type="submit" className="btn btn-primary" disabled={!plagiarismResult || plagiarismResult.score > 15}>
                  Submit to Blockchain
                </button>
              </div>
            </form>
          </div>

          <div className="results-section">
            <h2>Plagiarism Check Results</h2>
            {isProcessing && (
              <div className="processing">
                <div className="spinner"></div>
                <p>AI is analyzing your paper for plagiarism...</p>
              </div>
            )}

            {plagiarismResult && (
              <div className="plagiarism-result">
                <div className={`score-card ${plagiarismResult.score > 15 ? 'high' : 'low'}`}>
                  <h3>Similarity Score</h3>
                  <div className="score">{plagiarismResult.score}%</div>
                  <p className="status">
                    {plagiarismResult.score > 15 ? 'High Similarity - Review Required' : 'Low Similarity - Paper is Original'}
                  </p>
                </div>

                <div className="details">
                  <h4>Detailed Analysis</h4>
                  {plagiarismResult.details.map((detail, index) => (
                    <div key={index} className="detail-item">
                      <span className="source">{detail.source}</span>
                      <span className="similarity">{detail.similarity}%</span>
                    </div>
                  ))}
                </div>

                <div className="recommendation">
                  <h4>Recommendation</h4>
                  <p>
                    {plagiarismResult.score > 15 
                      ? 'Please review your paper for potential plagiarism before submission.'
                      : 'Your paper appears to be original and ready for blockchain submission.'
                    }
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SubmitPaper;
