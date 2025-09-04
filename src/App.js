import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Header from './components/Header';
import Home from './pages/Home';
import Student from './pages/Student';
import Paper from './pages/Paper';
import Dashboard from './pages/Dashboard';
import About from './pages/About';
import SubmitPaper from './pages/SubmitPaper';
import VerifyPaper from './pages/VerifyPaper';
import Login from './pages/Login';
import Signup from './pages/Signup';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Header />
          <Routes>
            {/* Public routes - accessible to everyone */}
            <Route path="/" element={<Home />} />
            <Route path="/student" element={<Student />} />
            <Route path="/about" element={<About />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            
            {/* Protected routes - require authentication */}
            <Route path="/paper" element={
              <ProtectedRoute>
                <Paper />
              </ProtectedRoute>
            } />
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } />
            <Route path="/submit-paper" element={
              <ProtectedRoute>
                <SubmitPaper />
              </ProtectedRoute>
            } />
            <Route path="/verify-paper" element={
              <ProtectedRoute>
                <VerifyPaper />
              </ProtectedRoute>
            } />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
