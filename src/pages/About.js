import React from 'react';
import './About.css';

const About = () => {
  return (
    <div className="about-page">
      <div className="container">
        <div className="page-header">
          <h1>About AcademicGuard</h1>
          <p>Protecting academic integrity through blockchain technology</p>
        </div>
        
        <div className="about-content">
          <div className="about-section">
            <div className="about-text">
              <h2>Our Mission</h2>
              <p>
                AcademicGuard is dedicated to safeguarding academic integrity by leveraging blockchain technology 
                to create a secure, transparent, and tamper-proof system for research paper authentication. We believe that 
                academic achievements should be protected and verifiable, ensuring that intellectual property rights are respected.
              </p>
              <p>
                Our platform enables researchers and academic institutions to submit papers with blockchain hashes, 
                providing immutable proof of authorship and timestamp. This innovative approach eliminates plagiarism, 
                prevents research theft, and ensures that original work is properly attributed and protected.
              </p>
            </div>
            <div className="about-image">
              <div className="mission-visual">
                <div className="visual-element">🎓</div>
              </div>
            </div>
          </div>
          
          <div className="about-section reverse">
            <div className="about-text">
              <h2>Our Vision</h2>
              <p>
                We envision a world where academic research is universally protected and instantly verifiable. 
                Through our blockchain-based platform, we're building the foundation for a secure academic ecosystem 
                that empowers researchers, institutions, and the global academic community.
              </p>
              <p>
                By combining cutting-edge blockchain technology with advanced AI plagiarism detection, we're making 
                academic integrity accessible to everyone, from individual researchers to major universities and 
                research institutions worldwide.
              </p>
            </div>
            <div className="about-image">
              <div className="vision-visual">
                <div className="visual-element">🔒</div>
              </div>
            </div>
          </div>
          
          <div className="team-section">
            <h2>Our Team</h2>
            <div className="team-grid">
              <div className="team-member">
                <div className="member-avatar">👨‍💼</div>
                <h3>Dr. Sarah Johnson</h3>
                <p className="member-role">CEO & Founder</p>
                <p className="member-bio">
                  Blockchain expert with 10+ years in academic technology and decentralized systems.
                </p>
              </div>
              
              <div className="team-member">
                <div className="member-avatar">👩‍💻</div>
                <h3>Michael Chen</h3>
                <p className="member-role">CTO</p>
                <p className="member-bio">
                  Full-stack developer specializing in blockchain architecture and smart contracts.
                </p>
              </div>
              
              <div className="team-member">
                <div className="member-avatar">👨‍🎓</div>
                <h3>Emily Rodriguez</h3>
                <p className="member-role">Head of Academic Affairs</p>
                <p className="member-bio">
                  Academic consultant with expertise in research integrity and plagiarism detection.
                </p>
              </div>
            </div>
          </div>
          
          <div className="values-section">
            <h2>Our Values</h2>
            <div className="values-grid">
              <div className="value-card">
                <div className="value-icon">🔒</div>
                <h3>Academic Integrity</h3>
                <p>We prioritize the protection and verification of academic work and research.</p>
              </div>
              
              <div className="value-card">
                <div className="value-icon">🌍</div>
                <h3>Global Access</h3>
                <p>Academic protection should be accessible to researchers worldwide, regardless of location.</p>
              </div>
              
              <div className="value-card">
                <div className="value-icon">🔍</div>
                <h3>Transparency</h3>
                <p>All verification processes and blockchain transactions are transparent and verifiable.</p>
              </div>
              
              <div className="value-card">
                <div className="value-icon">⚡</div>
                <h3>Innovation</h3>
                <p>We continuously innovate to improve academic technology and user experience.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default About;
