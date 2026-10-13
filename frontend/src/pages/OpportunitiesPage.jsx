import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import { useAuth } from '../context/AuthContext';
import {
  Briefcase,
  Search,
  Filter,
  CheckCircle,
  XCircle,
  ExternalLink,
  MapPin,
  Calendar,
  DollarSign,
  AlertTriangle
} from 'lucide-react';

const OpportunitiesPage = () => {
  const { user } = useAuth();
  const [opportunities, setOpportunities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [keyword, setKeyword] = useState('');
  const [jobType, setJobType] = useState('');
  const [workMode, setWorkMode] = useState('');
  const [selectedOpp, setSelectedOpp] = useState(null);

  useEffect(() => {
    fetchOpportunities();
  }, [jobType, workMode]);

  const fetchOpportunities = async () => {
    setLoading(true);
    try {
      const params = {};
      if (keyword.trim()) params.keyword = keyword.trim();
      if (jobType) params.jobType = jobType;
      if (workMode) params.workMode = workMode;

      const endpoint = user ? '/opportunities' : '/opportunities/public';
      const res = await api.get(endpoint, { params });
      if (res.data?.success) {
        setOpportunities(res.data.data.content || []);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchOpportunities();
  };

  return (
    <div className="dashboard-layout">
      {user && <Sidebar />}
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ marginBottom: '2rem' }}>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Campus Career Opportunities</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Explore campus drives and off-campus opportunities. Check your real-time eligibility evaluation below.
            </p>
          </div>

          {/* Search & Filters */}
          <div className="card" style={{ marginBottom: '2rem', padding: '1.25rem' }}>
            <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
              <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Search by job title or company..."
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
              </div>

              <select
                className="form-control"
                style={{ width: '180px' }}
                value={jobType}
                onChange={(e) => setJobType(e.target.value)}
              >
                <option value="">All Job Types</option>
                <option value="FULL_TIME">Full Time</option>
                <option value="INTERNSHIP">Internship</option>
                <option value="PART_TIME">Part Time</option>
              </select>

              <select
                className="form-control"
                style={{ width: '180px' }}
                value={workMode}
                onChange={(e) => setWorkMode(e.target.value)}
              >
                <option value="">All Work Modes</option>
                <option value="ON_SITE">On-Site</option>
                <option value="HYBRID">Hybrid</option>
                <option value="REMOTE">Remote</option>
              </select>

              <button type="submit" className="btn btn-primary">
                <Search size={16} /> Search
              </button>
            </form>
          </div>

          {/* Opportunities List */}
          {loading ? (
            <p>Loading opportunities...</p>
          ) : opportunities.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
              <p style={{ color: 'var(--text-muted)' }}>No opportunities matching your criteria were found.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              {opportunities.map((opp) => {
                const el = opp.eligibility;
                return (
                  <div key={opp.id} className="card" style={{ padding: '1.75rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.4rem' }}>
                          <h2 style={{ fontSize: '1.25rem', fontWeight: 800 }}>{opp.title}</h2>
                          <span className="badge badge-primary">{opp.jobType}</span>
                          <span className="badge badge-subtle">{opp.workMode}</span>
                        </div>
                        <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--primary)', marginBottom: '0.75rem' }}>
                          {opp.companyName}
                        </h3>
                        <div style={{ display: 'flex', gap: '1.25rem', flexWrap: 'wrap', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
                          {opp.location && (
                            <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                              <MapPin size={15} /> {opp.location}
                            </span>
                          )}
                          {opp.salaryOrStipend && (
                            <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                              <DollarSign size={15} /> {opp.salaryOrStipend}
                            </span>
                          )}
                          <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                            <Calendar size={15} /> Deadline: {opp.deadline}
                          </span>
                        </div>
                      </div>

                      {/* Eligibility evaluation tag for students */}
                      {user?.role === 'STUDENT' && el && (
                        <div style={{ textAlign: 'right' }}>
                          {el.eligible ? (
                            <div style={{
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: '0.4rem',
                              padding: '0.4rem 0.85rem',
                              borderRadius: '9999px',
                              background: 'var(--success-bg)',
                              color: 'var(--success)',
                              fontWeight: 700,
                              fontSize: '0.85rem'
                            }}>
                              <CheckCircle size={16} /> Eligible ({el.skillMatchPercentage}% Match)
                            </div>
                          ) : (
                            <div style={{
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: '0.4rem',
                              padding: '0.4rem 0.85rem',
                              borderRadius: '9999px',
                              background: 'var(--danger-bg)',
                              color: 'var(--danger)',
                              fontWeight: 700,
                              fontSize: '0.85rem'
                            }}>
                              <XCircle size={16} /> Criteria Missing
                            </div>
                          )}
                        </div>
                      )}
                    </div>

                    <p style={{ marginTop: '1rem', color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>
                      {opp.description}
                    </p>

                    {/* Required Skills */}
                    {opp.requiredSkills && opp.requiredSkills.length > 0 && (
                      <div style={{ marginTop: '1rem' }}>
                        <p style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '0.35rem', textTransform: 'uppercase' }}>
                          Required Skills:
                        </p>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                          {opp.requiredSkills.map((sk) => (
                            <span key={sk} className="badge badge-subtle" style={{ fontSize: '0.8rem' }}>
                              {sk}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Eligibility breakdown if ineligible */}
                    {user?.role === 'STUDENT' && el && !el.eligible && el.unmetCriteriaReasons?.length > 0 && (
                      <div style={{
                        marginTop: '1rem',
                        padding: '0.85rem 1rem',
                        background: '#fffbeb',
                        border: '1px solid #fde68a',
                        borderRadius: 'var(--radius-sm)',
                        fontSize: '0.85rem'
                      }}>
                        <p style={{ fontWeight: 700, color: '#b45309', display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.35rem' }}>
                          <AlertTriangle size={15} /> Unmet Eligibility Conditions:
                        </p>
                        <ul style={{ paddingLeft: '1.25rem', color: '#92400e' }}>
                          {el.unmetCriteriaReasons.map((reason, idx) => (
                            <li key={idx}>{reason}</li>
                          ))}
                        </ul>
                      </div>
                    )}

                    {/* Actions: View Details / External Official Apply */}
                    <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border)', display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                      {opp.externalApplyUrl ? (
                        <a
                          href={opp.externalApplyUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="btn btn-secondary btn-sm"
                        >
                          Official Opportunity Page <ExternalLink size={15} />
                        </a>
                      ) : (
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', alignSelf: 'center' }}>
                          Campus On-Campus Drive Registration
                        </span>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default OpportunitiesPage;
