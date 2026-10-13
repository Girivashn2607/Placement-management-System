import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import {
  Briefcase,
  CheckCircle,
  FileText,
  Bell,
  Sparkles,
  ArrowRight,
  TrendingUp,
  AlertCircle
} from 'lucide-react';

const StudentDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/student/dashboard')
      .then((res) => {
        if (res.data?.success) setData(res.data.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="dashboard-layout">
        <Sidebar />
        <main className="dashboard-main" style={{ padding: '2rem' }}>
          <p>Loading your placement dashboard...</p>
        </main>
      </div>
    );
  }

  const completion = data?.profileCompletionPercentage || 0;

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          {/* Welcome Banner */}
          <div style={{
            background: 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%)',
            color: '#ffffff',
            padding: '2rem',
            borderRadius: 'var(--radius-lg)',
            marginBottom: '2rem',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: '1rem'
          }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>
                Welcome, {data?.studentName || 'Student'}! 👋
              </h1>
              <p style={{ opacity: 0.9, marginTop: '0.25rem' }}>
                Your centralized portal for campus placements, skill matching, and AI career guidance.
              </p>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <Link to="/resume/builder" className="btn btn-secondary btn-sm" style={{ background: '#ffffff', color: 'var(--primary)' }}>
                <FileText size={16} /> Resume Builder
              </Link>
              <Link to="/opportunities" className="btn btn-primary btn-sm" style={{ background: 'rgba(255,255,255,0.2)', border: '1px solid rgba(255,255,255,0.4)' }}>
                View Drives <ArrowRight size={16} />
              </Link>
            </div>
          </div>

          {/* KPI Cards */}
          <div className="grid-4" style={{ marginBottom: '2rem' }}>
            {/* Profile Completion */}
            <div className="card">
              <div className="card-header" style={{ marginBottom: '0.5rem' }}>
                <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)' }}>Profile Status</span>
                <span style={{ fontWeight: 700, color: 'var(--primary)' }}>{completion}%</span>
              </div>
              <div className="progress-bar-bg" style={{ marginBottom: '0.75rem' }}>
                <div className="progress-bar-fill" style={{ width: `${completion}%` }} />
              </div>
              <Link to="/student/profile" style={{ fontSize: '0.8rem', color: 'var(--primary)', fontWeight: 600 }}>
                {completion < 100 ? 'Complete profile details →' : 'View profile →'}
              </Link>
            </div>

            {/* Total Opportunities */}
            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Available Openings</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, marginTop: '0.25rem' }}>
                    {data?.availableOpportunitiesCount || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--primary-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--primary)' }}>
                  <Briefcase size={22} />
                </div>
              </div>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                Active campus opportunities
              </p>
            </div>

            {/* Eligible / Matching Opportunities */}
            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Eligible For You</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--success)', marginTop: '0.25rem' }}>
                    {data?.matchingOpportunitiesCount || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--success-bg)', padding: '0.65rem', borderRadius: '10px', color: 'var(--success)' }}>
                  <CheckCircle size={22} />
                </div>
              </div>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                Meeting CGPA, batch & skills
              </p>
            </div>

            {/* Notifications */}
            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Unread Alerts</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent)', marginTop: '0.25rem' }}>
                    {data?.unreadNotificationsCount || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--accent-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--accent)' }}>
                  <Bell size={22} />
                </div>
              </div>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                Updates & announcements
              </p>
            </div>
          </div>

          {/* Quick Actions & Recent Announcements Grid */}
          <div className="grid-2">
            {/* Recent Announcements */}
            <div className="card">
              <div className="card-header">
                <h3 className="card-title">Placement Announcements</h3>
                <span className="badge badge-primary">Official Notices</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {data?.recentAnnouncements && data.recentAnnouncements.length > 0 ? (
                  data.recentAnnouncements.map((item) => (
                    <div key={item.id} style={{ padding: '0.75rem', background: 'var(--bg-subtle)', borderRadius: 'var(--radius-sm)' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                        <h4 style={{ fontSize: '0.9rem', fontWeight: 700 }}>{item.title}</h4>
                        <span className={`badge ${item.priority === 'HIGH' ? 'badge-danger' : 'badge-warning'}`} style={{ fontSize: '0.7rem' }}>
                          {item.priority}
                        </span>
                      </div>
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', lineHeight: 1.4 }}>
                        {item.content}
                      </p>
                    </div>
                  ))
                ) : (
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>No new announcements at this time.</p>
                )}
              </div>
            </div>

            {/* AI Tools & Mentorship Highlight */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div className="card-header">
                  <h3 className="card-title">AI Career Assistant Tools</h3>
                  <Sparkles size={20} color="var(--primary)" />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
                  <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)' }}>
                    <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--primary)', marginBottom: '0.25rem' }}>
                      AI Resume Builder
                    </h4>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                      Draft an ATS-friendly, clean single-page resume with instant bullet-point rewriting.
                    </p>
                    <Link to="/resume/builder" className="btn btn-secondary btn-sm">
                      Open Resume Builder
                    </Link>
                  </div>

                  <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)' }}>
                    <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--secondary)', marginBottom: '0.25rem' }}>
                      AI PDF Resume Analyzer
                    </h4>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                      Upload your PDF resume (up to 500 MB) for automated skill extraction, ATS scoring, and gap detection.
                    </p>
                    <Link to="/resume/analyzer" className="btn btn-secondary btn-sm">
                      Upload PDF Resume
                    </Link>
                  </div>
                </div>
              </div>

              <div style={{ marginTop: '1.25rem', padding: '0.85rem', background: 'var(--primary-light)', borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <span style={{ fontSize: '1.5rem' }}>🐘</span>
                <div>
                  <p style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)' }}>
                    Need Placement Advice?
                  </p>
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                    Click the floating elephant widget in the bottom-right corner to chat with Teximo!
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default StudentDashboard;
