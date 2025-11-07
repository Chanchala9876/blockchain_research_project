import React, { useState, useEffect } from 'react';
import './Paper.css';

const Paper = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [papers, setPapers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeFilter, setActiveFilter] = useState('All Papers');

  // Load all papers when component mounts or filter changes
  useEffect(() => {
    loadAllPapers();
  }, [activeFilter]);

  // Load all viewable papers from backend
  const loadAllPapers = async () => {
    setLoading(true);
    setError(null);

    try {
      let url = `http://localhost:8090/api/public/papers?page=0&size=100`;
      
      // Add department filter if not "All Papers"
      if (activeFilter !== 'All Papers') {
        url = `http://localhost:8090/api/public/papers/search/department?department=${encodeURIComponent(activeFilter)}&page=0&size=100`;
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
          // No Authorization header needed for public endpoints
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setPapers(data.papers || data.data?.content || []);
      } else {
        throw new Error(data.message || 'Failed to load papers');
      }
    } catch (err) {
      console.error('Load papers error:', err);
      setError(err.message || 'Failed to load papers. Please try again.');
      setPapers([]);
    } finally {
      setLoading(false);
    }
  };

  // Search papers function
  const searchPapers = async (query) => {
    if (!query.trim()) {
      // Reset to showing all papers for current filter
      loadAllPapers();
      return;
    }

    setLoading(true);
    setError(null);

    try {
      let url = `http://localhost:8090/api/public/papers/search?query=${encodeURIComponent(query)}&page=0&size=100`;
      
      // Add department filter if not "All Papers"
      if (activeFilter !== 'All Papers') {
        url = `http://localhost:8090/api/public/papers/search/department?department=${encodeURIComponent(activeFilter)}&query=${encodeURIComponent(query)}&page=0&size=100`;
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
          // No Authorization header needed for public endpoints
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setPapers(data.papers || data.data?.content || []);
      } else {
        throw new Error(data.message || 'Failed to search papers');
      }
    } catch (err) {
      console.error('Search error:', err);
      setError(err.message || 'Failed to search papers. Please try again.');
      setPapers([]);
    } finally {
      setLoading(false);
    }
  };

  // Handle search input change
  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
  };

  // Handle search submit
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    searchPapers(searchQuery);
  };

  // Handle filter change
  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setSearchQuery(''); // Clear search when changing filters
    // loadAllPapers will be called by useEffect
  };

  // Format date function
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).getFullYear();
    } catch {
      return 'N/A';
    }
  };

  // Get paper category from keywords or department
  const getPaperCategory = (paper) => {
    // Use department if available
    if (paper.department) {
      return paper.department;
    }
    
    // Fallback to keywords
    if (!paper.keywords || !Array.isArray(paper.keywords)) return 'Research';
    
    const keywordStr = paper.keywords.join(' ').toLowerCase();
    if (keywordStr.includes('blockchain')) return 'Blockchain';
    if (keywordStr.includes('education')) return 'Education';
    if (keywordStr.includes('technology')) return 'Technology';
    return 'Research';
  };

  return (
    <div className="paper-page">
      <div className="container">
        <div className="page-header">
          <h1>🔍 Research Papers</h1>
          <p>Search and explore academic papers and research on blockchain education</p>
          <div className="stats">
            <span>📊 {papers.length} papers available</span>
            {activeFilter !== 'All Papers' && (
              <span>📂 Filtered by: {activeFilter}</span>
            )}
          </div>
        </div>
        
        {/* Search Section */}
        <div className="search-section">
          <form onSubmit={handleSearchSubmit} className="search-form">
            <div className="search-input-group">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="Search papers by title, author, keywords, or abstract..."
                className="search-input"
              />
              <button type="submit" className="search-btn" disabled={loading}>
                {loading ? '🔄' : '🔍'}
              </button>
            </div>
          </form>
          
          {searchQuery && (
            <div className="search-info">
              <p>Search results for: "<strong>{searchQuery}</strong>"</p>
              {papers.length > 0 && (
                <span className="results-count">Found {papers.length} paper(s)</span>
              )}
            </div>
          )}
        </div>
        
        <div className="paper-content">
          <div className="paper-filters">
            <button 
              className={`filter-btn ${activeFilter === 'All Papers' ? 'active' : ''}`}
              onClick={() => handleFilterChange('All Papers')}
            >
              All Papers
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'Blockchain' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Blockchain')}
            >
              Blockchain
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'Education' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Education')}
            >
              Education
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'Technology' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Technology')}
            >
              Technology
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'Computer Science' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Computer Science')}
            >
              Computer Science
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'Engineering' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Engineering')}
            >
              Engineering
            </button>
          </div>
          
          {/* Loading State */}
          {loading && (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Loading papers...</p>
            </div>
          )}
          
          {/* Error State */}
          {error && (
            <div className="error-state">
              <p className="error-message">❌ {error}</p>
              <button onClick={() => window.location.reload()} className="retry-btn">
                🔄 Retry
              </button>
            </div>
          )}
          
          {/* Empty State */}
          {!loading && !error && papers.length === 0 && searchQuery && (
            <div className="empty-state">
              <h3>No papers found</h3>
              <p>Try searching with different keywords or check your spelling.</p>
            </div>
          )}
          
          {/* Default State - No search performed */}
          {!loading && !error && papers.length === 0 && !searchQuery && (
            <div className="default-state">
              <h3>🔍 Welcome to Research Papers</h3>
              <p>Browse research papers by department or use the search function.</p>
              <p>Click on the department filters above to explore papers by category.</p>
            </div>
          )}
          
          {/* Papers Grid */}
          {!loading && !error && papers.length > 0 && (
            <div className="papers-grid">
              {papers.map((paper) => (
                <div key={paper.id} className="paper-card">
                  <div className="paper-header">
                    <h3>{paper.title}</h3>
                    <span className="paper-category">
                      {getPaperCategory(paper)}
                    </span>
                  </div>
                  <p className="paper-abstract">
                    {paper.abstractText || paper.abstract || 'No abstract available'}
                  </p>
                  <div className="paper-meta">
                    <span className="author">👤 {paper.author}</span>
                    <span className="date">📅 {formatDate(paper.submissionDate || paper.createdAt)}</span>
                    {paper.department && (
                      <span className="department">🏛️ {paper.department}</span>
                    )}
                    {paper.institution && (
                      <span className="institution">🏫 {paper.institution}</span>
                    )}
                  </div>
                  
                  {/* Keywords */}
                  {paper.keywords && paper.keywords.length > 0 && (
                    <div className="paper-keywords">
                      {paper.keywords.slice(0, 3).map((keyword, index) => (
                        <span key={index} className="keyword-tag">
                          {keyword}
                        </span>
                      ))}
                      {paper.keywords.length > 3 && (
                        <span className="keyword-tag more">
                          +{paper.keywords.length - 3} more
                        </span>
                      )}
                    </div>
                  )}
                  
                  <div className="paper-actions">
                    <button className="btn btn-primary">
                      📄 Read Paper
                    </button>
                    {(paper.status === 'VERIFIED' || paper.verified) && (
                      <span className="verified-badge">✅ Verified</span>
                    )}
                    {paper.blockchainTxId && (
                      <span className="blockchain-badge">🔗 On Blockchain</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Paper;