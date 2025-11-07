import React, { useState } from 'react';
import './VerifyPaper.css';

const VerifyPaper = () => {
  const [activeTab, setActiveTab] = useState('upload'); // 'upload' or 'search'
  const [isVerifying, setIsVerifying] = useState(false);
  const [verificationResult, setVerificationResult] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);

  // Form data for thesis verification
  const [thesisData, setThesisData] = useState({
    thesisFile: null,
    title: '',
    author: '',
    department: '',
    submissionYear: new Date().getFullYear(),
    institution: '',
    abstract: '',
    keywords: [],
    supervisor: '', // Optional for matching
    coSupervisor: '' // Optional for matching
  });

  // Form data for search-based verification
  const [searchData, setSearchData] = useState({
    hash: '',
    title: '',
    author: '',
    blockchainTxId: ''
  });

  const [errors, setErrors] = useState({});
  const [keywordInput, setKeywordInput] = useState('');

  // Handle file upload
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validate file type - Accept PDF and Word documents
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      ];
      
      if (!allowedTypes.includes(file.type)) {
        setErrors({ ...errors, thesisFile: 'Please upload a PDF or Word document (.pdf, .doc, .docx)' });
        return;
      }
      
      // Validate file size (max 10MB)
      if (file.size > 10 * 1024 * 1024) {
        setErrors({ ...errors, thesisFile: 'File size must be less than 10MB' });
        return;
      }

      setThesisData({ ...thesisData, thesisFile: file });
      setErrors({ ...errors, thesisFile: null });
    }
  };

  // Handle form input changes
  const handleThesisChange = (e) => {
    const { name, value } = e.target;
    setThesisData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error for this field
    if (errors[name]) {
      setErrors({ ...errors, [name]: null });
    }
  };

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Handle keywords
  const addKeyword = () => {
    if (keywordInput.trim() && !thesisData.keywords.includes(keywordInput.trim())) {
      setThesisData({
        ...thesisData,
        keywords: [...thesisData.keywords, keywordInput.trim()]
      });
      setKeywordInput('');
    }
  };

  const removeKeyword = (index) => {
    setThesisData({
      ...thesisData,
      keywords: thesisData.keywords.filter((_, i) => i !== index)
    });
  };

  // Validation
  const validateThesisForm = () => {
    const newErrors = {};
    
    if (!thesisData.thesisFile) newErrors.thesisFile = 'Please upload your thesis document (PDF or Word)';
    if (!thesisData.title.trim()) newErrors.title = 'Thesis title is required';
    if (!thesisData.author.trim()) newErrors.author = 'Author name is required';
    if (!thesisData.department.trim()) newErrors.department = 'Department is required';
    if (!thesisData.submissionYear || thesisData.submissionYear < 1900 || thesisData.submissionYear > new Date().getFullYear() + 1) {
      newErrors.submissionYear = 'Please enter a valid submission year';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateSearchForm = () => {
    if (!searchData.hash && !searchData.title && !searchData.blockchainTxId) {
      alert('Please provide at least one search criterion (hash, title, or transaction ID)');
      return false;
    }
    return true;
  };

  // Submit thesis for verification - UPDATED TO CALL REAL API
  const submitThesisVerification = async () => {
    if (!validateThesisForm()) return;

    setIsVerifying(true);
    setUploadProgress(0);

    try {
      setUploadProgress(25);

      // Prepare form data for file upload
      const formData = new FormData();
      formData.append('thesisFile', thesisData.thesisFile);
      formData.append('title', thesisData.title);
      formData.append('author', thesisData.author);
      formData.append('department', thesisData.department);
      formData.append('institution', thesisData.institution || 'Jawaharlal Nehru University');
      formData.append('submissionYear', thesisData.submissionYear);
      formData.append('abstract', thesisData.abstract || '');
      formData.append('keywords', JSON.stringify(thesisData.keywords));
      formData.append('supervisor', thesisData.supervisor || '');
      formData.append('coSupervisor', thesisData.coSupervisor || '');

      console.log('📄 [VerifyPaper] Submitting thesis verification to real API...');

      setUploadProgress(50);

      // REAL API CALL to your backend
      const response = await fetch('http://localhost:8090/api/papers/verify-thesis', {
        method: 'POST',
        body: formData // Don't set Content-Type header, let browser set it for multipart/form-data
      });

      setUploadProgress(75);

      if (!response.ok) {
        throw new Error(`Server error: ${response.status} ${response.statusText}`);
      }

      const result = await response.json();
      console.log('✅ [VerifyPaper] Verification response:', result);

      setUploadProgress(100);
      setVerificationResult(result);
      setIsVerifying(false);

    } catch (error) {
      console.error('❌ Thesis verification failed:', error);
      setIsVerifying(false);
      setUploadProgress(0);
      alert(`Verification failed: ${error.message}. Please try again.`);
    }
  };

  // Search-based verification - UPDATED TO CALL REAL API
  const searchVerification = async () => {
    if (!validateSearchForm()) return;

    setIsVerifying(true);

    try {
      console.log('🔍 [VerifyPaper] Searching for paper via real API:', searchData);

      // REAL API CALL for search
      const response = await fetch('http://localhost:8090/api/papers/search', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(searchData)
      });

      if (!response.ok) {
        throw new Error(`Server error: ${response.status} ${response.statusText}`);
      }

      const result = await response.json();
      console.log('✅ [VerifyPaper] Search response:', result);

      setVerificationResult(result);
      setIsVerifying(false);

    } catch (error) {
      console.error('❌ Search verification failed:', error);
      setIsVerifying(false);
      alert(`Search failed: ${error.message}. Please try again.`);
    }
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
          <h1>🔍 Verify Research Paper</h1>
          <p>Verify the authenticity and authorship of research papers using AI-powered blockchain technology</p>
        </div>

        {/* Tab Navigation */}
        <div className="verification-tabs">
          <button 
            className={`tab-btn ${activeTab === 'upload' ? 'active' : ''}`}
            onClick={() => setActiveTab('upload')}
          >
            📄 Upload & Verify Thesis
          </button>
          <button 
            className={`tab-btn ${activeTab === 'search' ? 'active' : ''}`}
            onClick={() => setActiveTab('search')}
          >
            🔍 Search Database
          </button>
        </div>

        <div className="verify-content">
          {/* Thesis Upload & Verification Tab */}
          {activeTab === 'upload' && (
            <div className="upload-section">
              <h2>Upload Your Thesis for Verification</h2>
              <p className="section-description">
                Upload your thesis document (PDF or Word) and provide details to verify against our blockchain database
              </p>

              <div className="thesis-form">
                {/* File Upload */}
                <div className="form-group">
                  <label className="file-upload-label">
                    📎 Thesis Document *
                    <input
                      type="file"
                      accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                      onChange={handleFileChange}
                      className="file-input"
                    />
                  </label>
                  {thesisData.thesisFile && (
                    <div className="file-info">
                      <span className="file-name">{thesisData.thesisFile.name}</span>
                      <span className="file-type">
                        ({thesisData.thesisFile.type.includes('pdf') ? 'PDF' : 'Word Document'})
                      </span>
                      <span className="file-size">({(thesisData.thesisFile.size / 1024 / 1024).toFixed(2)} MB)</span>
                    </div>
                  )}
                  {errors.thesisFile && <span className="error-text">{errors.thesisFile}</span>}
                </div>

                {/* Thesis Details */}
                <div className="form-row">
                  <div className="form-group">
                    <label>📖 Thesis Title *</label>
                    <input
                      type="text"
                      name="title"
                      value={thesisData.title}
                      onChange={handleThesisChange}
                      placeholder="Enter your thesis title"
                      className={errors.title ? 'error' : ''}
                    />
                    {errors.title && <span className="error-text">{errors.title}</span>}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>👤 Author Name *</label>
                    <input
                      type="text"
                      name="author"
                      value={thesisData.author}
                      onChange={handleThesisChange}
                      placeholder="Enter your full name"
                      className={errors.author ? 'error' : ''}
                    />
                    {errors.author && <span className="error-text">{errors.author}</span>}
                  </div>

                  <div className="form-group">
                    <label>🏛️ Department *</label>
                    <input
                      type="text"
                      name="department"
                      value={thesisData.department}
                      onChange={handleThesisChange}
                      placeholder="e.g., Computer Science"
                      className={errors.department ? 'error' : ''}
                    />
                    {errors.department && <span className="error-text">{errors.department}</span>}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>📅 Submission Year *</label>
                    <input
                      type="number"
                      name="submissionYear"
                      value={thesisData.submissionYear}
                      onChange={handleThesisChange}
                      min="1900"
                      max={new Date().getFullYear() + 1}
                      className={errors.submissionYear ? 'error' : ''}
                    />
                    {errors.submissionYear && <span className="error-text">{errors.submissionYear}</span>}
                  </div>

                  <div className="form-group">
                    <label>🏫 Institution</label>
                    <input
                      type="text"
                      name="institution"
                      value={thesisData.institution}
                      onChange={handleThesisChange}
                      placeholder="e.g., Jawaharlal Nehru University"
                    />
                  </div>
                </div>

                {/* Abstract */}
                <div className="form-group">
                  <label>📝 Abstract</label>
                  <textarea
                    name="abstract"
                    value={thesisData.abstract}
                    onChange={handleThesisChange}
                    placeholder="Enter thesis abstract (optional but improves matching accuracy)"
                    rows="4"
                  />
                </div>

                {/* Keywords */}
                <div className="form-group">
                  <label>🏷️ Keywords</label>
                  <div className="keywords-input">
                    <input
                      type="text"
                      value={keywordInput}
                      onChange={(e) => setKeywordInput(e.target.value)}
                      placeholder="Enter keyword and press Add"
                      onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addKeyword())}
                    />
                    <button type="button" onClick={addKeyword} className="add-keyword-btn">
                      Add
                    </button>
                  </div>
                  
                  <div className="keywords-list">
                    {thesisData.keywords.map((keyword, index) => (
                      <span key={index} className="keyword-tag">
                        {keyword}
                        <button type="button" onClick={() => removeKeyword(index)}>×</button>
                      </span>
                    ))}
                  </div>
                </div>

                {/* Submit Button */}
                <button 
                  onClick={submitThesisVerification} 
                  className="btn btn-primary verify-btn"
                  disabled={isVerifying}
                >
                  {isVerifying ? `Verifying... ${uploadProgress}%` : '🔍 Verify Thesis'}
                </button>

                {/* Progress Bar */}
                {isVerifying && (
                  <div className="progress-container">
                    <div className="progress-bar">
                      <div 
                        className="progress-fill" 
                        style={{ width: `${uploadProgress}%` }}
                      ></div>
                    </div>
                    <p className="progress-text">
                      {uploadProgress < 25 ? 'Uploading file...' :
                       uploadProgress < 50 ? 'Analyzing document...' :
                       uploadProgress < 75 ? 'Running AI analysis...' :
                       'Generating report...'}
                    </p>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Search Database Tab */}
          {activeTab === 'search' && (
            <div className="search-section">
              <h2>Search Database</h2>
              <p className="section-description">
                Search for papers using blockchain hash, title, or transaction ID
              </p>

              <div className="search-form">
                <div className="form-group">
                  <label>🔗 Blockchain Hash</label>
                  <input
                    type="text"
                    name="hash"
                    value={searchData.hash}
                    onChange={handleSearchChange}
                    placeholder="Enter blockchain hash (0x...)"
                  />
                </div>

                <div className="form-group">
                  <label>📖 Paper Title</label>
                  <input
                    type="text"
                    name="title"
                    value={searchData.title}
                    onChange={handleSearchChange}
                    placeholder="Enter paper title to search"
                  />
                </div>

                <div className="form-group">
                  <label>👤 Author Name</label>
                  <input
                    type="text"
                    name="author"
                    value={searchData.author}
                    onChange={handleSearchChange}
                    placeholder="Enter author name (optional)"
                  />
                </div>

                <div className="form-group">
                  <label>📋 Transaction ID</label>
                  <input
                    type="text"
                    name="blockchainTxId"
                    value={searchData.blockchainTxId}
                    onChange={handleSearchChange}
                    placeholder="Enter blockchain transaction ID"
                  />
                </div>

                <button 
                  onClick={searchVerification} 
                  className="btn btn-primary verify-btn"
                  disabled={isVerifying}
                >
                  {isVerifying ? 'Searching...' : '🔍 Search Database'}
                </button>
              </div>
            </div>
          )}

          {/* Enhanced Results Section */}
          <div className="results-section">
            <h2>Verification Results</h2>
            
            {isVerifying && activeTab === 'search' && (
              <div className="verifying">
                <div className="spinner"></div>
                <p>Searching blockchain database...</p>
              </div>
            )}

            {verificationResult && (
              <div className="verification-result">
                <div className={`status-card ${verificationResult.verified ? 'verified' : 'not-verified'}`}>
                  <div className="status-icon">
                    {verificationResult.verified ? '✅' : '❌'}
                  </div>
                  <div className="status-content">
                    <h3>
                      {verificationResult.verified ? 
                        `Paper ${verificationResult.matchType === 'EXACT_MATCH' ? 'Verified - Exact Match' : 'Found - Analysis Complete'}` : 
                        'Paper Analysis Complete'
                      }
                    </h3>
                    <p>
                      {verificationResult.message || 
                       (verificationResult.verified 
                        ? 'This paper has been analyzed against our database and blockchain records.'
                        : 'Analysis completed. See detailed results below.')
                      }
                    </p>
                  </div>
                </div>

                {/* Paper Details */}
                {verificationResult.paper && (
                  <div className="paper-details">
                    <h3>📄 Paper Information</h3>
                    <div className="detail-grid">
                      <div className="detail-item">
                        <span className="label">Title:</span>
                        <span className="value">{verificationResult.paper.title}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Author:</span>
                        <span className="value">{verificationResult.paper.author}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Department:</span>
                        <span className="value">{verificationResult.paper.department}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Institution:</span>
                        <span className="value">{verificationResult.paper.institution}</span>
                      </div>
                      {verificationResult.paper.supervisor && (
                        <div className="detail-item">
                          <span className="label">Supervisor:</span>
                          <span className="value">{verificationResult.paper.supervisor}</span>
                        </div>
                      )}
                      <div className="detail-item">
                        <span className="label">Submission Date:</span>
                        <span className="value">{formatDate(verificationResult.paper.submissionDate)}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Status:</span>
                        <span className="value status-badge">{verificationResult.paper.status}</span>
                      </div>
                    </div>
                  </div>
                )}

                {/* Enhanced AI Analysis with Two Parts */}
                {verificationResult.aiAnalysis && (
                  <div className="ai-analysis-container">
                    <h3>🤖 AI Analysis Report</h3>
                    
                    {/* Part 1: Similarity Analysis */}
                    <div className="ai-section similarity-section">
                      <h4>📊 Part 1: Similarity Analysis with Database</h4>
                      <p className="section-description">
                        Comparison with {verificationResult.aiAnalysis.matchedPapersCount || 0} existing thesis embeddings in database
                      </p>
                      <div className="metrics-grid">
                        <div className="metric">
                          <div className="metric-label">Overall Similarity</div>
                          <div className="metric-value">{Math.round(verificationResult.similarityScore || 0)}%</div>
                          <div className="metric-status">
                            {(verificationResult.similarityScore || 0) > 95 ? 'Critical - Nearly Identical' : 
                             (verificationResult.similarityScore || 0) > 85 ? 'High Similarity' : 
                             (verificationResult.similarityScore || 0) > 70 ? 'Moderate Similarity' : 'Low Similarity'}
                          </div>
                        </div>
                        <div className="metric">
                          <div className="metric-label">Plagiarism Risk</div>
                          <div className="metric-value">{Math.round(verificationResult.plagiarismScore || 0)}%</div>
                          <div className="metric-status">
                            {(verificationResult.plagiarismScore || 0) > 90 ? 'Critical Risk' : 
                             (verificationResult.plagiarismScore || 0) > 70 ? 'High Risk' : 
                             (verificationResult.plagiarismScore || 0) > 40 ? 'Moderate Risk' : 'Low Risk'}
                          </div>
                        </div>
                        <div className="metric">
                          <div className="metric-label">Title Similarity</div>
                          <div className="metric-value">
                            {Math.round((verificationResult.aiAnalysis.titleSimilarity || 0) * 100)}%
                          </div>
                        </div>
                        <div className="metric">
                          <div className="metric-label">Content Similarity</div>
                          <div className="metric-value">
                            {Math.round((verificationResult.aiAnalysis.contentSimilarity || 0) * 100)}%
                          </div>
                        </div>
                      </div>
                      
                      {/* Similar Papers List */}
                      {verificationResult.aiAnalysis.topMatches && 
                       verificationResult.aiAnalysis.topMatches.length > 0 && (
                        <div className="similar-papers">
                          <h5>🔍 Most Similar Papers Found:</h5>
                          <div className="similar-papers-list">
                            {verificationResult.aiAnalysis.topMatches.slice(0, 3).map((match, idx) => (
                              <div key={idx} className="similar-paper-item">
                                <div className="paper-info">
                                  <div className="paper-title">{match.title}</div>
                                  <div className="paper-author">by {match.author} ({match.department})</div>
                                </div>
                                <div className="similarity-score">{Math.round(match.similarityScore)}%</div>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>

                    {/* Part 2: AI Detection Analysis */}
                    <div className="ai-section ai-detection-section">
                      <h4>🔍 Part 2: AI-Generated Content Detection</h4>
                      <p className="section-description">
                        Analysis of writing patterns to detect potential AI assistance in content generation
                      </p>
                      <div className="ai-detection-results">
                        <div className="ai-detection-score">
                          <div className="detection-label">AI Generation Probability</div>
                          <div className="detection-value">
                            {Math.round(verificationResult.aiAnalysis.aiDetectionScore || 0)}%
                          </div>
                          <div className={`detection-status ${
                            (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 80 ? 'high-risk' :
                            (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 60 ? 'moderate-risk' :
                            (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 40 ? 'low-moderate-risk' :
                            (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 20 ? 'low-risk' : 'very-low-risk'
                          }`}>
                            {(verificationResult.aiAnalysis.aiDetectionScore || 0) >= 80 ? 'HIGH PROBABILITY' :
                             (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 60 ? 'MODERATE PROBABILITY' :
                             (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 40 ? 'LOW-MODERATE PROBABILITY' :
                             (verificationResult.aiAnalysis.aiDetectionScore || 0) >= 20 ? 'LOW PROBABILITY' : 'VERY LOW PROBABILITY'}
                          </div>
                        </div>
                        
                        {verificationResult.aiAnalysis.aiDetectionConclusion && (
                          <div className="ai-conclusion">
                            <div className="conclusion-title">📋 Analysis Summary:</div>
                            <div className="conclusion-text">{verificationResult.aiAnalysis.aiDetectionConclusion}</div>
                          </div>
                        )}
                        
                        {verificationResult.aiAnalysis.aiDetectionIndicators && 
                         verificationResult.aiAnalysis.aiDetectionIndicators.length > 0 && (
                          <div className="ai-indicators">
                            <div className="indicators-title">🔍 Detected Indicators:</div>
                            <ul className="indicators-list">
                              {verificationResult.aiAnalysis.aiDetectionIndicators.slice(0, 5).map((indicator, idx) => (
                                <li key={idx} className="indicator-item">{indicator}</li>
                              ))}
                              {verificationResult.aiAnalysis.aiDetectionIndicators.length > 5 && (
                                <li className="indicator-more">
                                  +{verificationResult.aiAnalysis.aiDetectionIndicators.length - 5} more indicators detected
                                </li>
                              )}
                            </ul>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                )}

                {/* Blockchain Information */}
                {verificationResult.paper && (
                  <div className="blockchain-info">
                    <h4>⛓️ Blockchain Information</h4>
                    <div className="block-info">
                      {verificationResult.paper.blockchainHash && (
                        <div className="block-item">
                          <span className="block-label">Blockchain Hash:</span>
                          <span className="block-value hash">{verificationResult.paper.blockchainHash}</span>
                        </div>
                      )}
                      {verificationResult.paper.blockchainTxId && (
                        <div className="block-item">
                          <span className="block-label">Transaction ID:</span>
                          <span className="block-value">{verificationResult.paper.blockchainTxId}</span>
                        </div>
                      )}
                      <div className="block-item">
                        <span className="block-label">Verification Date:</span>
                        <span className="block-value">{formatDate(verificationResult.verificationTimestamp || new Date())}</span>
                      </div>
                      {verificationResult.paper.fileHash && (
                        <div className="block-item">
                          <span className="block-label">File Hash:</span>
                          <span className="block-value">{verificationResult.paper.fileHash}</span>
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Keywords if available */}
                {verificationResult.paper && verificationResult.paper.keywords && 
                 verificationResult.paper.keywords.length > 0 && (
                  <div className="paper-keywords">
                    <h4>🏷️ Keywords</h4>
                    <div className="keywords-display">
                      {verificationResult.paper.keywords.map((keyword, index) => (
                        <span key={index} className="keyword-display-tag">{keyword}</span>
                      ))}
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