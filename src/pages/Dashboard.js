import React from 'react';
import './Dashboard.css';

const Dashboard = () => {
  return (
    <div className="dashboard-page">
      <div className="container">
        <div className="page-header">
          <h1>Dashboard</h1>
          <p>Monitor your blockchain education platform analytics</p>
        </div>
        
        <div className="dashboard-content">
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon">👥</div>
              <div className="stat-info">
                <h3>Total Students</h3>
                <p className="stat-number">1,247</p>
                <span className="stat-change positive">+12% this month</span>
              </div>
            </div>
            
            <div className="stat-card">
              <div className="stat-icon">📜</div>
              <div className="stat-info">
                <h3>Certificates Issued</h3>
                <p className="stat-number">3,891</p>
                <span className="stat-change positive">+8% this month</span>
              </div>
            </div>
            
            <div className="stat-card">
              <div className="stat-icon">🔗</div>
              <div className="stat-info">
                <h3>Blockchain Transactions</h3>
                <p className="stat-number">15,234</p>
                <span className="stat-change positive">+15% this month</span>
              </div>
            </div>
            
            <div className="stat-card">
              <div className="stat-icon">✅</div>
              <div className="stat-info">
                <h3>Verifications</h3>
                <p className="stat-number">2,156</p>
                <span className="stat-change positive">+5% this month</span>
              </div>
            </div>
          </div>
          
          <div className="dashboard-grid">
            <div className="dashboard-card">
              <h3>Recent Activity</h3>
              <div className="activity-list">
                <div className="activity-item">
                  <div className="activity-icon">🎓</div>
                  <div className="activity-content">
                    <p>New certificate issued to John Doe</p>
                    <span className="activity-time">2 hours ago</span>
                  </div>
                </div>
                <div className="activity-item">
                  <div className="activity-icon">🔍</div>
                  <div className="activity-content">
                    <p>Credential verification completed</p>
                    <span className="activity-time">4 hours ago</span>
                  </div>
                </div>
                <div className="activity-item">
                  <div className="activity-icon">👤</div>
                  <div className="activity-content">
                    <p>New student registration</p>
                    <span className="activity-time">6 hours ago</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div className="dashboard-card">
              <h3>Quick Actions</h3>
              <div className="quick-actions">
                <button className="action-btn">
                  <span className="action-icon">➕</span>
                  <span>Add New Course</span>
                </button>
                <button className="action-btn">
                  <span className="action-icon">📋</span>
                  <span>Issue Certificate</span>
                </button>
                <button className="action-btn">
                  <span className="action-icon">🔍</span>
                  <span>Verify Credential</span>
                </button>
                <button className="action-btn">
                  <span className="action-icon">📊</span>
                  <span>Generate Report</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
