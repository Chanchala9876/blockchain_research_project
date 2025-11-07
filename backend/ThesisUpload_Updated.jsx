import React, { useState } from 'react';
import './ThesisUpload.css';

const ThesisUpload = () => {
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    department: '',
    institution: '',
    supervisor: '',
    coSupervisor: '',
    abstractText: '',
    keywords: ''
  });

  const [thesisFile, setThesisFile] = useState(null);
  const [validationDocument, setValidationDocument] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadResult, setUploadResult] = useState(null);
  const [error, setError] = useState(null);

  const userData = JSON.parse(localStorage.getItem('user') || '{}');
  const authToken = userData.token || localStorage.getItem('authToken');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    const { name, files } = e.target;
    if (files && files[0]) {
      if (name === 'thesisFile') {
        setThesisFile(files[0]);
      } else if (name === 'validationDocument') {
        setValidationDocument(files[0]);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setUploadResult(null);
    setUploading(true);
    setUploadProgress(0);

    try {
      // Validation
      if (!thesisFile) {
        throw new Error('Please select a thesis file (PDF or DOCX)');
      }

      if (!validationDocument) {
        throw new Error('Please upload the university/government validation document');
      }

      if (!formData.title || !formData.author || !formData.department) {
        throw new Error('Please fill in all required fields');
      }

      console.log('📤 [ThesisUpload] Uploading thesis for pending approval...');

      // Create FormData
      const uploadFormData = new FormData();
      uploadFormData.append('file', thesisFile);
      uploadFormData.append('validationDocument', validationDocument);
      uploadFormData.append('title', formData.title);
      uploadFormData.append('author', formData.author);
      uploadFormData.append('department', formData.department);
      uploadFormData.append('institution', formData.institution || 'Default Institute');
      uploadFormData.append('supervisor', formData.supervisor || 'N/A');
      uploadFormData.append('coSupervisor', formData.coSupervisor || '');
      uploadFormData.append('abstractText', formData.abstractText || '');
      uploadFormData.append('keywords', formData.keywords || '');
      uploadFormData.append('uploadedBy', userData.email || userData.username || userData.id);

      setUploadProgress(30);

      // IMPORTANT: Using the NEW endpoint that goes to pending_thesis
      const response = await fetch('http://localhost:8090/api/research-papers/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${authToken}`
          // Don't set Content-Type - browser will set it with boundary for multipart/form-data
        },
        body: uploadFormData
      });

      setUploadProgress(70);

      const result = await response.json();
      
      setUploadProgress(100);

      if (!response.ok) {
        // Handle duplicate detection (409 Conflict)
        if (response.status === 409) {
          throw new Error(result.message || 'This thesis appears to be a duplicate of an existing paper');
        }
        throw new Error(result.message || result.error || 'Upload failed');
      }

      console.log('✅ [ThesisUpload] Upload successful:', result);

      // Check if it went to pending approval (NEW workflow)
      if (result.needsApproval) {
        setUploadResult({
          success: true,
          message: result.message,
          status: 'PENDING_APPROVAL',
          pendingThesisId: result.pendingThesisId,
          currentApprovals: result.currentApprovals,
          totalAdminsRequired: result.totalAdminsRequired,
          approvalProgress: result.approvalProgress,
          storedOnBlockchain: false
        });
      } else {
        // Old direct upload (shouldn't happen with new workflow)
        setUploadResult({
          success: true,
          message: result.message,
          status: result.status,
          storedOnBlockchain: result.storedOnBlockchain || false
        });
      }

      // Reset form
      setFormData({
        title: '',
        author: '',
        department: '',
        institution: '',
        supervisor: '',
        coSupervisor: '',
        abstractText: '',
        keywords: ''
      });
      setThesisFile(null);
      setValidationDocument(null);

    } catch (err) {
      console.error('❌ [ThesisUpload] Upload error:', err);
      setError(err.message);
      setUploadResult({
        success: false,
        message: err.message
      });
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="thesis-upload-container">
      <div className="upload-header">
        <h2>📋 Upload Thesis for Multi-Admin Approval</h2>
        <p className="upload-description">
          Upload a thesis along with the university/government validation document. 
          The thesis will be checked for duplicates using AI, and if unique, will require 
          approval from other admins before being added to the blockchain.
        </p>
      </div>

      <div className="upload-workflow-info">
        <h3>📊 Approval Workflow:</h3>
        <div className="workflow-steps">
          <div className="workflow-step">
            <span className="step-number">1</span>
            <span>Upload thesis + validation document</span>
          </div>
          <div className="workflow-step">
            <span className="step-number">2</span>
            <span>AI checks for duplicates</span>
          </div>
          <div className="workflow-step">
            <span className="step-number">3</span>
            <span>Stored in pending approvals (NOT blockchain yet)</span>
          </div>
          <div className="workflow-step">
            <span className="step-number">4</span>
            <span>Other admins review and approve</span>
          </div>
          <div className="workflow-step">
            <span className="step-number">5</span>
            <span>When all approve → Automatic blockchain submission</span>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="upload-form">
        <div className="form-section">
          <h3>📄 Files (Required)</h3>
          
          <div className="form-group">
            <label htmlFor="thesisFile">
              Thesis File (PDF or DOCX) <span className="required">*</span>
            </label>
            <input
              type="file"
              id="thesisFile"
              name="thesisFile"
              accept=".pdf,.docx"
              onChange={handleFileChange}
              required
              className="file-input"
            />
            {thesisFile && (
              <div className="file-selected">
                ✅ Selected: {thesisFile.name} ({(thesisFile.size / 1024 / 1024).toFixed(2)} MB)
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="validationDocument">
              University/Government Validation Document (PDF) <span className="required">*</span>
            </label>
            <input
              type="file"
              id="validationDocument"
              name="validationDocument"
              accept=".pdf"
              onChange={handleFileChange}
              required
              className="file-input"
            />
            {validationDocument && (
              <div className="file-selected">
                ✅ Selected: {validationDocument.name} ({(validationDocument.size / 1024 / 1024).toFixed(2)} MB)
              </div>
            )}
            <small className="field-help">
              This document should be signed by the university/government authority verifying the authenticity of the thesis.
            </small>
          </div>
        </div>

        <div className="form-section">
          <h3>📝 Thesis Information</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="title">
                Thesis Title <span className="required">*</span>
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                required
                placeholder="Enter thesis title"
                className="form-control"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="author">
                Author Name <span className="required">*</span>
              </label>
              <input
                type="text"
                id="author"
                name="author"
                value={formData.author}
                onChange={handleInputChange}
                required
                placeholder="Enter author name"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="department">
                Department <span className="required">*</span>
              </label>
              <input
                type="text"
                id="department"
                name="department"
                value={formData.department}
                onChange={handleInputChange}
                required
                placeholder="e.g., Computer Science"
                className="form-control"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="institution">Institution</label>
              <input
                type="text"
                id="institution"
                name="institution"
                value={formData.institution}
                onChange={handleInputChange}
                placeholder="e.g., Jawaharlal Nehru University"
                className="form-control"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="supervisor">Supervisor</label>
              <input
                type="text"
                id="supervisor"
                name="supervisor"
                value={formData.supervisor}
                onChange={handleInputChange}
                placeholder="Enter supervisor name"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="coSupervisor">Co-Supervisor (Optional)</label>
              <input
                type="text"
                id="coSupervisor"
                name="coSupervisor"
                value={formData.coSupervisor}
                onChange={handleInputChange}
                placeholder="Enter co-supervisor name"
                className="form-control"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="abstractText">Abstract</label>
            <textarea
              id="abstractText"
              name="abstractText"
              value={formData.abstractText}
              onChange={handleInputChange}
              rows="5"
              placeholder="Enter thesis abstract..."
              className="form-control"
            />
          </div>

          <div className="form-group">
            <label htmlFor="keywords">Keywords (comma-separated)</label>
            <input
              type="text"
              id="keywords"
              name="keywords"
              value={formData.keywords}
              onChange={handleInputChange}
              placeholder="e.g., Machine Learning, AI, Healthcare"
              className="form-control"
            />
          </div>
        </div>

        {error && (
          <div className="alert alert-error">
            <span className="alert-icon">⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {uploadResult && (
          <div className={`alert ${uploadResult.success ? 'alert-success' : 'alert-error'}`}>
            <span className="alert-icon">{uploadResult.success ? '✅' : '❌'}</span>
            <div>
              <p><strong>{uploadResult.message}</strong></p>
              {uploadResult.success && uploadResult.status === 'PENDING_APPROVAL' && (
                <div className="approval-status">
                  <p>📊 Status: Pending Approval</p>
                  <p>✅ Current Approvals: {uploadResult.currentApprovals} / {uploadResult.totalAdminsRequired}</p>
                  <p>📈 Progress: {uploadResult.approvalProgress?.toFixed(1)}%</p>
                  <p>⛓️ Blockchain: Will be added after all approvals</p>
                </div>
              )}
            </div>
          </div>
        )}

        {uploading && (
          <div className="upload-progress">
            <div className="progress-bar-container">
              <div className="progress-bar-fill" style={{ width: `${uploadProgress}%` }}>
                {uploadProgress}%
              </div>
            </div>
            <p className="progress-text">Uploading thesis for approval review...</p>
          </div>
        )}

        <div className="form-actions">
          <button
            type="submit"
            disabled={uploading}
            className="btn btn-primary btn-upload"
          >
            {uploading ? '🔄 Uploading...' : '📤 Upload for Approval'}
          </button>
          
          <button
            type="button"
            onClick={() => {
              setFormData({
                title: '',
                author: '',
                department: '',
                institution: '',
                supervisor: '',
                coSupervisor: '',
                abstractText: '',
                keywords: ''
              });
              setThesisFile(null);
              setValidationDocument(null);
              setError(null);
              setUploadResult(null);
            }}
            className="btn btn-secondary"
            disabled={uploading}
          >
            🔄 Reset Form
          </button>
        </div>
      </form>
    </div>
  );
};

export default ThesisUpload;
