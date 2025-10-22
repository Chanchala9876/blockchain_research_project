import React from 'react';
import './Paper.css';

const Paper = () => {
  return (
    <div className="paper-page">
      <div className="container">
        <div className="page-header">
          <h1>Research Papers</h1>
          <p>Explore academic papers and research on blockchain education</p>
        </div>
        
        <div className="paper-content">
          <div className="paper-filters">
            <button className="filter-btn active">All Papers</button>
            <button className="filter-btn">Blockchain</button>
            <button className="filter-btn">Education</button>
            <button className="filter-btn">Technology</button>
          </div>
          
          <div className="papers-grid">
            <div className="paper-card">
              <div className="paper-header">
                <h3>Blockchain in Education: A Comprehensive Review</h3>
                <span className="paper-category">Blockchain</span>
              </div>
              <p className="paper-abstract">
                This paper explores the potential applications of blockchain technology in educational systems, 
                focusing on credential verification and academic record management.
              </p>
              <div className="paper-meta">
                <span className="author">nitya</span>
                <span className="date">2024</span>
              </div>
              <button className="btn btn-primary">Read Paper</button>
            </div>
            
            <div className="paper-card">
              <div className="paper-header">
                <h3>Decentralized Learning Platforms</h3>
                <span className="paper-category">Education</span>
              </div>
              <p className="paper-abstract">
                An analysis of decentralized learning platforms and their impact on global education accessibility.
              </p>
              <div className="paper-meta">
                <span className="author">Prof. Michael Chen</span>
                <span className="date">2023</span>
              </div>
              <button className="btn btn-primary">Read Paper</button>
            </div>
            
            <div className="paper-card">
              <div className="paper-header">
                <h3>Smart Contracts for Academic Credentials</h3>
                <span className="paper-category">Technology</span>
              </div>
              <p className="paper-abstract">
                Implementation of smart contracts for automated credential verification and issuance in educational institutions.
              </p>
              <div className="paper-meta">
                <span className="author">Dr. Emily Rodriguez</span>
                <span className="date">2024</span>
              </div>
              <button className="btn btn-primary">Read Paper</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Paper;
