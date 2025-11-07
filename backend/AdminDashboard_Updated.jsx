import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import ThesisUpload from './admin/ThesisUpload';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const [activeSection, setActiveSection] = useState('dashboard');
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalPapers: 0,
    pendingVerifications: 0,
    recentActivities: []
  });
  const [blockchainRecords, setBlockchainRecords] = useState([]);
  const [pendingTheses, setPendingTheses] = useState([]);
  const [loadingRecords, setLoadingRecords] = useState(false);
  const [loadingPending, setLoadingPending] = useState(false);
  const [recordsError, setRecordsError] = useState(null);
  const [pendingError, setPendingError] = useState(null);

  // Get user data from either AuthContext or localStorage
  const userData = user || JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = userData?.role === 'ADMIN' || 
                 userData?.role === 'ROLE_ADMIN' || 
                 userData?.isAdmin === true;

  console.log('🔧 [AdminDashboard] User data:', userData);
  console.log('🔧 [AdminDashboard] Is admin:', isAdmin);

  // Function to fetch blockchain records from backend
  const fetchBlockchainRecords = async () => {
    setLoadingRecords(true);
    setRecordsError(null);
    
    try {
      console.log('🔗 [AdminDashboard] Fetching blockchain records...');
      
      const response = await fetch('http://localhost:8090/api/research-papers/blockchain-records?page=0&size=50', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userData.token || localStorage.getItem('authToken')}`
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('✅ [AdminDashboard] Blockchain records fetched:', data);
      
      // Backend returns ApiResponse with records property
      const recordsArray = data.success ? (data.records || []) : [];
      setBlockchainRecords(recordsArray);
      
    } catch (error) {
      console.error('❌ [AdminDashboard] Error fetching blockchain records:', error);
      setRecordsError(`Failed to load blockchain records: ${error.message}`);
      setBlockchainRecords([]);
    } finally {
      setLoadingRecords(false);
    }
  };

  // Function to fetch pending theses awaiting approval
  const fetchPendingTheses = async () => {
    setLoadingPending(true);
    setPendingError(null);
    
    try {
      console.log('⏳ [AdminDashboard] Fetching pending theses...');
      
      const response = await fetch('http://localhost:8090/api/pending-thesis/pending', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userData.token || localStorage.getItem('authToken')}`
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('✅ [AdminDashboard] Pending theses fetched:', data);
      
      // Backend returns ApiResponse with data property containing the array
      const thesesArray = data.success ? (data.data || []) : [];
      setPendingTheses(thesesArray);
      
      // Update stats
      setStats(prev => ({
        ...prev,
        pendingVerifications: thesesArray.length
      }));
      
    } catch (error) {
      console.error('❌ [AdminDashboard] Error fetching pending theses:', error);
      setPendingError(`Failed to load pending theses: ${error.message}`);
      setPendingTheses([]);
    } finally {
      setLoadingPending(false);
    }
  };

  // Function to approve a thesis
  const approveThesis = async (thesisId) => {
    try {
      console.log('✅ [AdminDashboard] Approving thesis:', thesisId);
      
      const response = await fetch(`http://localhost:8090/api/pending-thesis/${thesisId}/approve`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${userData.token || localStorage.getItem('authToken')}`
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || errorData.error || 'Failed to approve thesis');
      }

      const result = await response.json();
      console.log('✅ [AdminDashboard] Thesis approved:', result);
      
      // Check if thesis was fully approved and moved to blockchain
      if (result.message && result.message.includes('moved to blockchain')) {
        alert('Thesis fully approved and added to blockchain!');
        await fetchBlockchainRecords();
      } else {
        alert('Your approval recorded successfully!');
      }
      
      // Refresh pending list
      await fetchPendingTheses();
      
    } catch (error) {
      console.error('❌ [AdminDashboard] Error approving thesis:', error);
      alert(`Failed to approve thesis: ${error.message}`);
    }
  };

  // Function to reject a thesis
  const rejectThesis = async (thesisId, reason) => {
    try {
      console.log('❌ [AdminDashboard] Rejecting thesis:', thesisId);
      
      // Backend expects form data with 'reason' parameter
      const formData = new URLSearchParams();
      formData.append('reason', reason || 'Does not meet requirements');
      
      const response = await fetch(`http://localhost:8090/api/pending-thesis/${thesisId}/reject`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': `Bearer ${userData.token || localStorage.getItem('authToken')}`
        },
        body: formData
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || errorData.error || 'Failed to reject thesis');
      }

      const result = await response.json();
      console.log('✅ [AdminDashboard] Thesis rejected:', result);
      
      alert('Thesis rejected successfully');
      
      // Refresh pending list
      await fetchPendingTheses();
      
    } catch (error) {
      console.error('❌ [AdminDashboard] Error rejecting thesis:', error);
      alert(`Failed to reject thesis: ${error.message}`);
    }
  };

  useEffect(() => {
    // Load admin statistics
    const loadAdminStats = async () => {
      try {
        const response = await fetch('http://localhost:8090/api/admin/stats', {
          headers: {
            'Authorization': `Bearer ${userData.token || localStorage.getItem('authToken')}`
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          setStats(prev => ({
            ...prev,
            ...data
          }));
        }
      } catch (error) {
        console.warn('Could not load stats, using defaults');
      }
    };

    if ((isAuthenticated || userData?.token) && isAdmin) {
      loadAdminStats();
    }
  }, [isAuthenticated, userData, isAdmin]);

  // Load data when switching sections
  useEffect(() => {
    if (activeSection === 'blockchain-records') {
      fetchBlockchainRecords();
    } else if (activeSection === 'pending-approvals') {
      fetchPendingTheses();
    }
  }, [activeSection]);

  if ((!isAuthenticated && !userData?.token) || !isAdmin) {
    return (
      <div className="admin-dashboard">
        <div className="container">
          <div className="access-denied">
            <div className="access-denied-icon">🚫</div>
            <h2>Access Denied</h2>
            <p>You don't have permission to access this admin area.</p>
            <p>Debug: Auth: {isAuthenticated ? 'Yes' : 'No'}, Role: {userData?.role}, IsAdmin: {isAdmin ? 'Yes' : 'No'}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      <div className="container">
        {/* Navigation Tabs */}
        <div className="admin-nav">
          <button 
            className={`nav-tab ${activeSection === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActiveSection('dashboard')}
          >
            📊 Dashboard
          </button>
          <button 
            className={`nav-tab ${activeSection === 'pending-approvals' ? 'active' : ''}`}
            onClick={() => setActiveSection('pending-approvals')}
          >
            ⏳ Pending Approvals {pendingTheses.length > 0 && <span className="badge">{pendingTheses.length}</span>}
          </button>
          <button 
            className={`nav-tab ${activeSection === 'upload-thesis' ? 'active' : ''}`}
            onClick={() => setActiveSection('upload-thesis')}
          >
            📋 Upload Thesis
          </button>
          <button 
            className={`nav-tab ${activeSection === 'blockchain-records' ? 'active' : ''}`}
            onClick={() => setActiveSection('blockchain-records')}
          >
            ⛓️ Blockchain Records
          </button>
          <button 
            className={`nav-tab ${activeSection === 'manage-users' ? 'active' : ''}`}
            onClick={() => setActiveSection('manage-users')}
          >
            👥 Manage Users
          </button>
        </div>

        {/* Conditional Content Based on Active Section */}
        {activeSection === 'upload-thesis' ? (
          <ThesisUpload />
        ) : activeSection === 'pending-approvals' ? (
          <div className="pending-approvals-section">
            <div className="section-header">
              <h2>⏳ Pending Thesis Approvals</h2>
              <p>Review and approve submitted theses for blockchain verification</p>
              <button 
                className="btn btn-primary refresh-btn"
                onClick={fetchPendingTheses}
                disabled={loadingPending}
              >
                {loadingPending ? '🔄 Loading...' : '🔄 Refresh List'}
              </button>
            </div>

            {pendingError && (
              <div className="error-banner">
                <span className="error-icon">⚠️</span>
                <span>{pendingError}</span>
              </div>
            )}

            <div className="pending-container">
              {loadingPending ? (
                <div className="loading-state">
                  <div className="loading-spinner"></div>
                  <p>Loading pending theses...</p>
                </div>
              ) : pendingTheses.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">✅</div>
                  <h3>No Pending Approvals</h3>
                  <p>All submitted theses have been reviewed.</p>
                </div>
              ) : (
                <div className="pending-grid">
                  {pendingTheses.map((thesis) => {
                    const isOwnUpload = thesis.uploadedBy === (userData.email || userData.username);
                    const hasApproved = thesis.approvals && thesis.approvals.includes(userData.email || userData.username);
                    const approvalProgress = thesis.totalAdminsRequired > 0 
                      ? Math.round((thesis.currentApprovals / thesis.totalAdminsRequired) * 100)
                      : 0;
                    
                    // Debug log to check validation document data
                    console.log('📄 Thesis data:', {
                      id: thesis.id,
                      title: thesis.title,
                      validationDocumentPath: thesis.validationDocumentPath,
                      validationDocumentName: thesis.validationDocumentName,
                      hasValidationDoc: !!thesis.validationDocumentPath
                    });
                    
                    return (
                    <div key={thesis.id} className="pending-card">
                      <div className="pending-header">
                        <h3 className="thesis-title">{thesis.title}</h3>
                        <div className="status-badge pending">
                          {isOwnUpload ? 'YOUR UPLOAD' : hasApproved ? 'YOU APPROVED' : 'PENDING REVIEW'}
                        </div>
                      </div>
                      
                      {/* Approval Progress Bar */}
                      <div className="approval-progress">
                        <div className="progress-header">
                          <span className="progress-label">
                            📊 Approval Progress: {thesis.currentApprovals} / {thesis.totalAdminsRequired}
                          </span>
                          <span className="progress-percentage">{approvalProgress}%</span>
                        </div>
                        <div className="progress-bar">
                          <div 
                            className="progress-fill" 
                            style={{ width: `${approvalProgress}%` }}
                          ></div>
                        </div>
                        {thesis.approvals && thesis.approvals.length > 0 && (
                          <div className="approved-by">
                            <strong>✅ Approved by:</strong> {thesis.approvals.join(', ')}
                          </div>
                        )}
                      </div>

                      <div className="thesis-details">
                        <div className="detail-row">
                          <span className="label">👤 Author:</span>
                          <span className="value">{thesis.author}</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">🏛️ Department:</span>
                          <span className="value">{thesis.department}</span>
                        </div>
                        {thesis.institution && (
                          <div className="detail-row">
                            <span className="label">🏫 Institution:</span>
                            <span className="value">{thesis.institution}</span>
                          </div>
                        )}
                        {thesis.supervisor && (
                          <div className="detail-row">
                            <span className="label">👨‍🏫 Supervisor:</span>
                            <span className="value">{thesis.supervisor}</span>
                          </div>
                        )}
                        <div className="detail-row">
                          <span className="label">� Uploaded by:</span>
                          <span className="value">
                            {isOwnUpload ? '🔵 You' : thesis.uploadedBy}
                          </span>
                        </div>
                        <div className="detail-row">
                          <span className="label">📅 Submitted:</span>
                          <span className="value">{new Date(thesis.submissionDate || thesis.createdAt).toLocaleDateString('en-US', {
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}</span>
                        </div>
                      </div>

                      {thesis.abstractText && (
                        <div className="thesis-abstract">
                          <strong>Abstract:</strong>
                          <p>{thesis.abstractText.substring(0, 200)}{thesis.abstractText.length > 200 ? '...' : ''}</p>
                        </div>
                      )}

                      {thesis.keywords && thesis.keywords.length > 0 && (
                        <div className="thesis-keywords">
                          <strong>Keywords:</strong>
                          <div className="keywords-list">
                            {thesis.keywords.map((keyword, idx) => (
                              <span key={idx} className="keyword-tag">{keyword}</span>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* Validation Document Viewer */}
                      <div className="validation-document-section">
                        <div className="validation-header">
                          <strong>📄 University/Government Signature Document</strong>
                          <span className="validation-badge">Required for Verification</span>
                        </div>
                        {thesis.validationDocumentPath ? (
                          <div className="validation-viewer">
                            <div className="document-info">
                              <p>
                                <strong>Document:</strong> {thesis.validationDocumentName || 'Validation Document'}
                              </p>
                              {thesis.validationDocumentSize && (
                                <p>
                                  <strong>Size:</strong> {(thesis.validationDocumentSize / 1024).toFixed(2)} KB
                                </p>
                              )}
                            </div>
                            <div className="document-actions">
                              <a 
                                href={`http://localhost:8090/api/pending-thesis/${thesis.id}/validation-document`}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="btn btn-view-doc"
                              >
                                👁️ View Signature Document
                              </a>
                              <a 
                                href={`http://localhost:8090/api/pending-thesis/${thesis.id}/validation-document?download=true`}
                                download
                                className="btn btn-download-doc"
                              >
                                📥 Download Document
                              </a>
                            </div>
                            <div className="validation-notice">
                              ⚠️ Please review the signature document carefully before approving
                            </div>
                          </div>
                        ) : (
                          <div className="validation-viewer">
                            <p style={{color: '#dc3545', padding: '10px'}}>
                              ⚠️ No validation document uploaded. Path: {thesis.validationDocumentPath || 'null'}
                            </p>
                          </div>
                        )}
                      </div>

                      <div className="approval-actions">
                        {isOwnUpload ? (
                          <div className="info-message">
                            ℹ️ You uploaded this thesis. Waiting for approval from other admins.
                          </div>
                        ) : hasApproved ? (
                          <div className="success-message">
                            ✅ You have already approved this thesis. Waiting for {thesis.totalAdminsRequired - thesis.currentApprovals} more approval(s).
                          </div>
                        ) : (
                          <>
                            <button 
                              className="btn btn-success"
                              onClick={() => {
                                if (window.confirm(`Approve thesis "${thesis.title}"?\n\nProgress: ${thesis.currentApprovals}/${thesis.totalAdminsRequired} approvals received`)) {
                                  approveThesis(thesis.id);
                                }
                              }}
                            >
                              ✅ Approve Thesis
                            </button>
                            <button 
                              className="btn btn-danger"
                              onClick={() => {
                                const reason = window.prompt('Enter reason for rejection:');
                                if (reason !== null && reason.trim() !== '') {
                                  rejectThesis(thesis.id, reason);
                                }
                              }}
                            >
                              ❌ Reject
                            </button>
                          </>
                        )}
                        <button className="btn btn-outline">👁️ View Full Details</button>
                      </div>
                    </div>
                  );
                  })}
                </div>
              )}
            </div>
          </div>
        ) : activeSection === 'dashboard' ? (
          <>
            <div className="admin-header">
              <div className="admin-welcome">
                <div className="admin-avatar">👨‍💼</div>
                <div className="admin-info">
                  <h1>Welcome back, Admin</h1>
                  <p>Manage your research platform from here</p>
                </div>
              </div>
              <div className="admin-actions">
                <button 
                  className="btn btn-primary"
                  onClick={() => setActiveSection('pending-approvals')}
                >
                  ⏳ Review Pending ({stats.pendingVerifications})
                </button>
                <button 
                  className="btn btn-secondary"
                  onClick={() => setActiveSection('upload-thesis')}
                >
                  📋 Upload Verified Thesis
                </button>
                <button 
                  className="btn btn-secondary"
                  onClick={() => setActiveSection('blockchain-records')}
                >
                  ⛓️ View Blockchain Records
                </button>
              </div>
            </div>

        {/* Statistics Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon users-icon">👥</div>
            <div className="stat-content">
              <h3>{stats.totalUsers}</h3>
              <p>Total Users</p>
            </div>
          </div>
          
          <div className="stat-card">
            <div className="stat-icon papers-icon">📄</div>
            <div className="stat-content">
              <h3>{stats.totalPapers}</h3>
              <p>Research Papers</p>
            </div>
          </div>
          
          <div className="stat-card" onClick={() => setActiveSection('pending-approvals')} style={{cursor: 'pointer'}}>
            <div className="stat-icon pending-icon">⏳</div>
            <div className="stat-content">
              <h3>{stats.pendingVerifications}</h3>
              <p>Pending Approvals</p>
            </div>
          </div>
          
          <div className="stat-card">
            <div className="stat-icon growth-icon">📈</div>
            <div className="stat-content">
              <h3>+12%</h3>
              <p>Growth This Month</p>
            </div>
          </div>
        </div>

        {/* Admin Management Sections */}
        <div className="admin-sections">
          <div className="admin-section">
            <div className="section-header">
              <h2>Thesis Management</h2>
              <button className="btn btn-outline" onClick={() => setActiveSection('pending-approvals')}>
                View Pending ({stats.pendingVerifications})
              </button>
            </div>
            <div className="section-content">
              <div className="management-options">
                <div className="management-option">
                  <div className="option-icon">⏳</div>
                  <div className="option-content">
                    <h4>Pending Approvals</h4>
                    <p>Review and approve submitted theses</p>
                  </div>
                  <button className="btn btn-small" onClick={() => setActiveSection('pending-approvals')}>
                    Review
                  </button>
                </div>
                
                <div className="management-option">
                  <div className="option-icon">⛓️</div>
                  <div className="option-content">
                    <h4>Blockchain Records</h4>
                    <p>View all verified theses on blockchain</p>
                  </div>
                  <button className="btn btn-small" onClick={() => setActiveSection('blockchain-records')}>
                    View
                  </button>
                </div>
                
                <div className="management-option">
                  <div className="option-icon">📋</div>
                  <div className="option-content">
                    <h4>Upload Thesis</h4>
                    <p>Directly upload pre-approved theses</p>
                  </div>
                  <button className="btn btn-small" onClick={() => setActiveSection('upload-thesis')}>
                    Upload
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="admin-section">
            <div className="section-header">
              <h2>User Management</h2>
              <button className="btn btn-outline">View All Users</button>
            </div>
            <div className="section-content">
              <div className="management-options">
                <div className="management-option">
                  <div className="option-icon">👤</div>
                  <div className="option-content">
                    <h4>Manage Users</h4>
                    <p>Add, edit, or remove user accounts</p>
                  </div>
                  <button className="btn btn-small">Manage</button>
                </div>
                
                <div className="management-option">
                  <div className="option-icon">🔐</div>
                  <div className="option-content">
                    <h4>Role Management</h4>
                    <p>Assign and modify user roles</p>
                  </div>
                  <button className="btn btn-small">Configure</button>
                </div>
                
                <div className="management-option">
                  <div className="option-icon">📊</div>
                  <div className="option-content">
                    <h4>User Analytics</h4>
                    <p>View user activity and statistics</p>
                  </div>
                  <button className="btn btn-small">View</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Activities */}
        <div className="admin-section">
          <div className="section-header">
            <h2>Recent Activities</h2>
            <button className="btn btn-outline">View Activity Log</button>
          </div>
          <div className="section-content">
            <div className="activity-list">
              {stats.recentActivities && stats.recentActivities.map((activity) => (
                <div key={activity.id} className="activity-item">
                  <div className="activity-icon">📝</div>
                  <div className="activity-content">
                    <h4>{activity.action}</h4>
                    <p>{activity.user || activity.paper}</p>
                  </div>
                  <div className="activity-time">{activity.time}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
          </>
        ) : activeSection === 'manage-users' ? (
          <div className="coming-soon">
            <h2>👥 User Management</h2>
            <p>User management functionality coming soon...</p>
          </div>
        ) : activeSection === 'blockchain-records' ? (
          <div className="blockchain-records-section">
            <div className="section-header">
              <h2>⛓️ Blockchain Records</h2>
              <p>View all research papers stored on the blockchain</p>
              <button 
                className="btn btn-primary refresh-btn"
                onClick={fetchBlockchainRecords}
                disabled={loadingRecords}
              >
                {loadingRecords ? '🔄 Loading...' : '🔄 Refresh Records'}
              </button>
            </div>

            {recordsError && (
              <div className="error-banner">
                <span className="error-icon">⚠️</span>
                <span>{recordsError}</span>
              </div>
            )}

            <div className="records-container">
              {loadingRecords ? (
                <div className="loading-state">
                  <div className="loading-spinner"></div>
                  <p>Loading blockchain records...</p>
                </div>
              ) : blockchainRecords.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">📭</div>
                  <h3>No Records Found</h3>
                  <p>No blockchain records available. Approve pending theses to add records.</p>
                  <button 
                    className="btn btn-primary"
                    onClick={() => setActiveSection('pending-approvals')}
                  >
                    View Pending Approvals
                  </button>
                </div>
              ) : (
                <div className="records-grid">
                  {blockchainRecords.map((record) => (
                    <div key={record.id} className="record-card">
                      <div className="record-header">
                        <h3 className="record-title">{record.title}</h3>
                        <div className={`status-badge ${(record.status || 'verified').toLowerCase()}`}>
                          {record.status || 'VERIFIED'}
                        </div>
                      </div>
                      
                      <div className="record-details">
                        <div className="detail-row">
                          <span className="label">👤 Author:</span>
                          <span className="value">{record.author}</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">🏛️ Department:</span>
                          <span className="value">{record.department}</span>
                        </div>
                        {record.institution && (
                          <div className="detail-row">
                            <span className="label">🏫 Institution:</span>
                            <span className="value">{record.institution}</span>
                          </div>
                        )}
                        <div className="detail-row">
                          <span className="label">📅 Submission:</span>
                          <span className="value">{new Date(record.submissionDate || record.createdAt).toLocaleDateString('en-US', {
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}</span>
                        </div>
                        {record.verificationDate && (
                          <div className="detail-row">
                            <span className="label">✅ Verified:</span>
                            <span className="value">{new Date(record.verificationDate).toLocaleDateString('en-US', {
                              year: 'numeric',
                              month: 'short', 
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}</span>
                          </div>
                        )}
                        {record.fileSize && (
                          <div className="detail-row">
                            <span className="label">💾 File Size:</span>
                            <span className="value">{(record.fileSize / 1024 / 1024).toFixed(2)} MB</span>
                          </div>
                        )}
                      </div>

                      <div className="blockchain-info">
                        {record.blockchainHash && (
                          <div className="blockchain-row">
                            <span className="label">🔗 Blockchain Hash:</span>
                            <span className="value hash-value" title={record.blockchainHash}>
                              {record.blockchainHash.substring(0, 20)}...{record.blockchainHash.substring(record.blockchainHash.length - 10)}
                            </span>
                          </div>
                        )}
                        {record.blockchainTxId && (
                          <div className="blockchain-row">
                            <span className="label">📋 Transaction ID:</span>
                            <span className="value">{record.blockchainTxId}</span>
                          </div>
                        )}
                        {record.fileHash && (
                          <div className="blockchain-row">
                            <span className="label">🔐 File Hash:</span>
                            <span className="value hash-value" title={record.fileHash}>
                              {record.fileHash.substring(0, 20)}...
                            </span>
                          </div>
                        )}
                      </div>

                      <div className="record-actions">
                        <button className="btn btn-small btn-outline">View Details</button>
                        <button className="btn btn-small btn-secondary">Download</button>
                        <button className="btn btn-small btn-info">Verify Hash</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="records-summary">
              <div className="summary-stats">
                <div className="summary-item">
                  <span className="summary-number">{blockchainRecords.length}</span>
                  <span className="summary-label">Total Records</span>
                </div>
                <div className="summary-item">
                  <span className="summary-number">{blockchainRecords.filter(r => r.status === 'VERIFIED' || !r.status).length}</span>
                  <span className="summary-label">Verified Papers</span>
                </div>
                <div className="summary-item">
                  <span className="summary-number">
                    {blockchainRecords.length > 0 && blockchainRecords[0].fileSize
                      ? (blockchainRecords.reduce((acc, r) => acc + (r.fileSize || 0), 0) / 1024 / 1024).toFixed(1)
                      : 0} MB
                  </span>
                  <span className="summary-label">Total Storage</span>
                </div>
                <div className="summary-item">
                  <span className="summary-number">100%</span>
                  <span className="summary-label">Blockchain Verified</span>
                </div>
              </div>
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default AdminDashboard;
