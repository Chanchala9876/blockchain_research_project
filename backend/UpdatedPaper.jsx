import React, { useState, useEffect } from 'react';
import './Paper.css';

const Paper = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [papers, setPapers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeFilter, setActiveFilter] = useState('All Papers');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Load all viewable papers on component mount
  useEffect(() => {
    loadAllPapers();
  }, []);

  // Load all viewable papers function
  const loadAllPapers = async (page = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8090/api/public/papers?page=${page}&size=10&sortBy=uploadedDate&sortDir=desc`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      setPapers(data.papers || []);
      setCurrentPage(data.currentPage || 0);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
      
    } catch (err) {
      console.error('Load papers error:', err);
      setError(err.message || 'Failed to load papers. Please try again.');
      setPapers([]);
    } finally {
      setLoading(false);
    }
  };

  // Search papers function
  const searchPapers = async (query, page = 0) => {
    if (!query.trim()) {
      loadAllPapers(page);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Use the public search endpoint
      const response = await fetch(`http://localhost:8090/api/public/papers/search?query=${encodeURIComponent(query)}&page=${page}&size=10`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      setPapers(data.papers || []);
      setCurrentPage(data.currentPage || 0);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
      
    } catch (err) {
      console.error('Search error:', err);
      setError(err.message || 'Failed to search papers. Please try again.');
      setPapers([]);
    } finally {
      setLoading(false);
    }
  };

  // Search by department/category
  const searchByDepartment = async (department, page = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8090/api/public/papers/search/department?department=${encodeURIComponent(department)}&page=${page}&size=10`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      setPapers(data.papers || []);
      setCurrentPage(data.currentPage || 0);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
      
    } catch (err) {
      console.error('Department search error:', err);
      setError(err.message || 'Failed to search papers by department. Please try again.');
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
    setCurrentPage(0);
    searchPapers(searchQuery, 0);
  };

  // Handle filter change
  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setCurrentPage(0);
    
    if (filter === 'All Papers') {
      if (searchQuery.trim()) {
        searchPapers(searchQuery, 0);
      } else {
        loadAllPapers(0);
      }
    } else {
      // Search by department/category
      searchByDepartment(filter, 0);
    }
  };

  // Handle pagination
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    
    if (activeFilter === 'All Papers') {
      if (searchQuery.trim()) {
        searchPapers(searchQuery, newPage);
      } else {
        loadAllPapers(newPage);
      }
    } else {
      searchByDepartment(activeFilter, newPage);
    }
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
    if (paper.department) {
      return paper.department;
    }
    
    if (!paper.keywords || !Array.isArray(paper.keywords)) return 'Research';
    
    const keywordStr = paper.keywords.join(' ').toLowerCase();
    if (keywordStr.includes('computer science') || keywordStr.includes('css') || keywordStr.includes('scss')) return 'Computer Science';
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
          {totalElements > 0 && (
            <p className="total-papers">Total papers available: {totalElements}</p>
          )}
        </div>
        
        {/* Search Section */}
        <div className="search-section">
          <form onSubmit={handleSearchSubmit} className="search-form">
            <div className="search-input-group">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="Search papers by title, author, keywords, department, or abstract... (try 'computer science', 'css', 'blockchain')"
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
                <span className="results-count">Found {totalElements} paper(s)</span>
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
              className={`filter-btn ${activeFilter === 'Computer Science' ? 'active' : ''}`}
              onClick={() => handleFilterChange('Computer Science')}
            >
              Computer Science
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
          </div>
          
          {/* Loading State */}
          {loading && (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Searching papers...</p>
            </div>
          )}
          
          {/* Error State */}
          {error && (
            <div className="error-state">
              <p className="error-message">❌ {error}</p>
              <button onClick={() => loadAllPapers()} className="retry-btn">
                🔄 Retry
              </button>
            </div>
          )}
          
          {/* Empty State */}
          {!loading && !error && papers.length === 0 && (searchQuery || activeFilter !== 'All Papers') && (
            <div className="empty-state">
              <h3>No papers found</h3>
              <p>Try searching with different keywords or check your spelling.</p>
              <p>Available searches: computer science, css, scss, blockchain, technology, education</p>
              <button onClick={() => {
                setSearchQuery('');
                setActiveFilter('All Papers');
                loadAllPapers();
              }} className="btn btn-secondary">
                View All Papers
              </button>
            </div>
          )}
          
          {/* Default State - Show all papers */}
          {!loading && !error && papers.length === 0 && !searchQuery && activeFilter === 'All Papers' && (
            <div className="default-state">
              <h3>🔍 Start Your Research Journey</h3>
              <p>Use the search bar above to find academic papers and research documents.</p>
              <p>Search by title, author name, keywords, department, or abstract content.</p>
              <p>Try searching for: "computer science", "css", "scss", "blockchain"</p>
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
                    {paper.viewable && (
                      <span className="public-badge">🌐 Public</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
          
          {/* Pagination */}
          {!loading && !error && totalPages > 1 && (
            <div className="pagination">
              <button 
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="pagination-btn"
              >
                ← Previous
              </button>
              
              <span className="pagination-info">
                Page {currentPage + 1} of {totalPages} ({totalElements} total papers)
              </span>
              
              <button 
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className="pagination-btn"
              >
                Next →
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Paper;