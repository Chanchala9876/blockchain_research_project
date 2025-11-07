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

  // Load all papers on component mount
  useEffect(() => {
    loadAllPapers();
  }, []);

  // Load all public papers
  const loadAllPapers = async (page = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8090/api/public/papers?page=${page}&size=10`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setPapers(data.data.content || []);
        setCurrentPage(data.data.number);
        setTotalPages(data.data.totalPages);
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
      loadAllPapers();
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8090/api/public/papers/search?query=${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setPapers(data.data || []);
        setCurrentPage(0);
        setTotalPages(1);
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

  // Search by department
  const searchByDepartment = async (department) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8090/api/public/papers/search/department?department=${encodeURIComponent(department)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setPapers(data.data || []);
        setCurrentPage(0);
        setTotalPages(1);
      } else {
        throw new Error(data.message || 'Failed to search by department');
      }
    } catch (err) {
      console.error('Department search error:', err);
      setError(err.message || 'Failed to search by department. Please try again.');
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
    
    // Implement filtering logic based on the filter
    if (filter === 'All Papers') {
      loadAllPapers();
    } else if (filter === 'Computer Science') {
      searchByDepartment('Computer Science');
    } else if (filter === 'Engineering') {
      searchByDepartment('Engineering');
    } else if (filter === 'Technology') {
      searchPapers('technology');
    } else if (filter === 'Blockchain') {
      searchPapers('blockchain');
    } else if (filter === 'Education') {
      searchPapers('education');
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
  const getPaperCategory = (keywords, department) => {
    if (department) {
      if (department.toLowerCase().includes('computer')) return 'Computer Science';
      if (department.toLowerCase().includes('engineering')) return 'Engineering';
    }
    
    if (!keywords || !Array.isArray(keywords)) return 'Research';
    
    const keywordStr = keywords.join(' ').toLowerCase();
    if (keywordStr.includes('blockchain')) return 'Blockchain';
    if (keywordStr.includes('education')) return 'Education';
    if (keywordStr.includes('technology')) return 'Technology';
    if (keywordStr.includes('computer')) return 'Computer Science';
    return 'Research';
  };

  // Handle pagination
  const handlePageChange = (newPage) => {
    if (searchQuery) {
      // If searching, don't paginate (search results are not paginated in this implementation)
      return;
    }
    loadAllPapers(newPage);
  };

  return (
    <div className="paper-page">
      <div className="container">
        <div className="page-header">
          <h1>🔍 Research Papers</h1>
          <p>Search and explore academic papers and research on blockchain education</p>
        </div>
        
        {/* Search Section */}
        <div className="search-section">
          <form onSubmit={handleSearchSubmit} className="search-form">
            <div className="search-input-group">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="Search papers by title, author, keywords, department, or abstract..."
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
              <p>Loading papers...</p>
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
          {!loading && !error && papers.length === 0 && searchQuery && (
            <div className="empty-state">
              <h3>No papers found</h3>
              <p>Try searching with different keywords or check your spelling.</p>
              <p>Try searching for: "CSS", "JavaScript", "Machine Learning", "Blockchain", etc.</p>
              <button onClick={() => loadAllPapers()} className="show-all-btn">
                📄 Show All Papers
              </button>
            </div>
          )}
          
          {/* Default State - No search performed */}
          {!loading && !error && papers.length === 0 && !searchQuery && (
            <div className="default-state">
              <h3>🔍 Start Your Research Journey</h3>
              <p>Use the search bar above to find academic papers and research documents.</p>
              <p>Search by title, author name, keywords, department, or abstract content.</p>
              <div className="suggested-searches">
                <p><strong>Try searching for:</strong></p>
                <div className="search-suggestions">
                  <button onClick={() => { setSearchQuery('CSS'); searchPapers('CSS'); }}>CSS</button>
                  <button onClick={() => { setSearchQuery('JavaScript'); searchPapers('JavaScript'); }}>JavaScript</button>
                  <button onClick={() => { setSearchQuery('Machine Learning'); searchPapers('Machine Learning'); }}>Machine Learning</button>
                  <button onClick={() => { setSearchQuery('Blockchain'); searchPapers('Blockchain'); }}>Blockchain</button>
                </div>
              </div>
            </div>
          )}
          
          {/* Papers Grid */}
          {!loading && !error && papers.length > 0 && (
            <>
              <div className="papers-grid">
                {papers.map((paper) => (
                  <div key={paper.id} className="paper-card">
                    <div className="paper-header">
                      <h3>{paper.title}</h3>
                      <span className="paper-category">
                        {getPaperCategory(paper.keywords, paper.department)}
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
                          <span key={index} className="keyword-tag" onClick={() => { setSearchQuery(keyword); searchPapers(keyword); }}>
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
                      {paper.status === 'VERIFIED' && (
                        <span className="verified-badge">✅ Verified</span>
                      )}
                      {paper.viewable && (
                        <span className="public-badge">🌐 Public</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {!searchQuery && totalPages > 1 && (
                <div className="pagination">
                  <button 
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="page-btn"
                  >
                    ← Previous
                  </button>
                  
                  <span className="page-info">
                    Page {currentPage + 1} of {totalPages}
                  </span>
                  
                  <button 
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                    className="page-btn"
                  >
                    Next →
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Paper;