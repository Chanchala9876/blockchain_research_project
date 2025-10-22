import React from 'react';
import './Student.css';

const Student = () => {
  return (
    <div className="student-page">
      <div className="container">
        <div className="page-header">
          <h1>Student Portal</h1>
          <p>Access your educational records and manage your blockchain credentials</p>
        </div>
        
        <div className="student-content">
          <div className="student-dashboard">
            <div className="dashboard-card">
              <h3>My Certificates</h3>
              <p>View and manage your digital certificates</p>
              <button className="btn btn-primary">View Certificates</button>
            </div>
            
            <div className="dashboard-card">
              <h3>Course Progress</h3>
              <p>Track your learning journey</p>
              <button className="btn btn-primary">View Progress</button>
            </div>
            
            <div className="dashboard-card">
              <h3>Verification Requests</h3>
              <p>Manage credential verification</p>
              <button className="btn btn-primary">View Requests</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Student;
