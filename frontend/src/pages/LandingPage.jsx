import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { GraduationCap, Briefcase, FileCheck, Bot, CheckCircle, ArrowRight, Building, Award } from 'lucide-react';
import api from '../services/api';

const LandingPage = () => {
  const [opportunities, setOpportunities] = useState([]);

  useEffect(() => {
    api.get('/opportunities/public?size=3')
      .then(res => {
        if (res.data?.success) setOpportunities(res.data.data.content || []);
      })
      .catch(() => {});
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section style={{
        background: 'linear-gradient(135deg, #1e3a8a 0%, #0f172a 100%)',
        color: '#ffffff',
        padding: '5rem 2rem',
        textAlign: 'center',
      }}>
        <div style={{ maxWidth: '900px', margin: '0 auto' }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.35rem 0.85rem',
            background: 'rgba(255, 255, 255, 0.1)',
            borderRadius: '9999px',
            fontSize: '0.85rem',
            fontWeight: 600,
            marginBottom: '1.5rem',
            border: '1px solid rgba(255, 255, 255, 0.2)'
          }}>
            <Bot size={16} color="#fbbf24" /> Official Campus AI Placement Portal
          </div>
          <h1 style={{ fontSize: '3rem', fontWeight: 800, lineHeight: 1.15, marginBottom: '1.25rem' }}>
            Empowering Campus Careers With Transparent Intelligence
          </h1>
          <p style={{ fontSize: '1.15rem', color: '#cbd5e1', lineHeight: 1.6, marginBottom: '2.5rem' }}>
            Connecting university students with verified recruiters. Build ATS-compliant resumes with AI,
            receive explainable job eligibility breakdowns, and get 24/7 placement mentorship with Teximo.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', flexWrap: 'wrap' }}>
            <Link to="/register" className="btn btn-primary" style={{ background: '#2563eb', padding: '0.8rem 1.8rem', fontSize: '1rem' }}>
              Student Registration <ArrowRight size={18} />
            </Link>
            <Link to="/opportunities" className="btn btn-secondary" style={{ padding: '0.8rem 1.8rem', fontSize: '1rem' }}>
              Explore Opportunities
            </Link>
          </div>
        </div>
      </section>

      {/* Core Features */}
      <section style={{ maxWidth: '1200px', margin: '4rem auto', padding: '0 2rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
          <h2 style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--text-primary)' }}>
            Designed For Modern Placement Operations
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
            Comprehensive tools for students, recruiters, and placement officers.
          </p>
        </div>

        <div className="grid-3">
          <div className="card" style={{ padding: '2rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '10px', background: 'var(--primary-light)', color: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem' }}>
              <FileCheck size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              AI Resume Builder & Analyzer
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
              Upload existing PDFs or create ATS-formatted resumes. Extract skills, detect gaps, and get instant suggestions powered by official AI APIs.
            </p>
          </div>

          <div className="card" style={{ padding: '2rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '10px', background: 'var(--secondary-light)', color: 'var(--secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem' }}>
              <CheckCircle size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Explainable Eligibility Engine
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
              Zero guesswork. See exactly why you meet or miss CGPA cutoffs, batch criteria, or required technical skills for every single opening.
            </p>
          </div>

          <div className="card" style={{ padding: '2rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '10px', background: 'var(--accent-light)', color: 'var(--accent)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem' }}>
              <Bot size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Teximo AI Placement Mentor
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
              Meet Teximo, the friendly elephant assistant. Get quick answers on campus placement drives, technical interview preparation, and aptitude tests.
            </p>
          </div>
        </div>
      </section>

      {/* Featured Openings */}
      <section style={{ background: '#ffffff', borderTop: '1px solid var(--border)', padding: '4rem 2rem' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
            <div>
              <h2 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Featured Career Opportunities</h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Verified opportunities from university recruitment partners</p>
            </div>
            <Link to="/opportunities" className="btn btn-secondary btn-sm">
              View All <ArrowRight size={14} />
            </Link>
          </div>

          <div className="grid-3">
            {opportunities.length > 0 ? (
              opportunities.map(opp => (
                <div key={opp.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                      <span className="badge badge-primary">{opp.jobType}</span>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{opp.workMode}</span>
                    </div>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.35rem' }}>{opp.title}</h3>
                    <p style={{ color: 'var(--primary)', fontWeight: 600, fontSize: '0.9rem', marginBottom: '0.75rem' }}>
                      {opp.companyName}
                    </p>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', lineHeight: 1.5, marginBottom: '1rem' }}>
                      {opp.description?.substring(0, 110)}...
                    </p>
                  </div>
                  <div style={{ borderTop: '1px solid var(--border)', paddingTop: '0.75rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
                      Deadline: {opp.deadline}
                    </span>
                    <Link to={`/opportunities/${opp.id}`} className="btn btn-primary btn-sm">
                      Details
                    </Link>
                  </div>
                </div>
              ))
            ) : (
              <div className="card" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem' }}>
                <p style={{ color: 'var(--text-muted)' }}>Sign in to view live opportunities published for the current academic session.</p>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer style={{ background: '#0f172a', color: '#94a3b8', padding: '3rem 2rem', textAlign: 'center', fontSize: '0.85rem' }}>
        <p>© 2026 University Placement Cell. AI-Powered Career & Placement Management Platform.</p>
        <p style={{ marginTop: '0.5rem' }}>Compliant with university placement policies. Automated scraping & external auto-application strictly disabled.</p>
      </footer>
    </div>
  );
};

export default LandingPage;
