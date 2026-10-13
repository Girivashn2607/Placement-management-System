import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import {
  Building2,
  Plus,
  Briefcase,
  Users,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

const RecruiterDashboard = () => {
  const [company, setCompany] = useState(null);
  const [opportunities, setOpportunities] = useState([]);
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showPostModal, setShowPostModal] = useState(false);

  // New Opportunity Form
  const [newOpp, setNewOpp] = useState({
    title: '',
    description: '',
    jobType: 'FULL_TIME',
    workMode: 'ON_SITE',
    location: '',
    minCgpa: 7.00,
    allowedDegrees: 'B.Tech, M.Tech, MCA',
    targetGraduationYear: 2026,
    allowedDepartments: 'Computer Science and Engineering, Information Technology',
    salaryOrStipend: '12 LPA - 16 LPA',
    experienceRequirements: 'Fresher / 0-1 Years',
    deadline: '2026-11-30',
    mandatorySkills: 'Java, Spring Boot, MySQL',
    optionalSkills: 'Docker, AWS',
    externalApplyUrl: '',
  });

  const [statusMsg, setStatusMsg] = useState(null);

  useEffect(() => {
    fetchRecruiterData();
  }, []);

  const fetchRecruiterData = async () => {
    try {
      const [compRes, oppRes, candRes] = await Promise.all([
        api.get('/recruiter/my-company'),
        api.get('/recruiter/my-opportunities'),
        api.get('/recruiter/candidates?size=10'),
      ]);

      if (compRes.data?.success) setCompany(compRes.data.data);
      if (oppRes.data?.success) setOpportunities(oppRes.data.data.content || []);
      if (candRes.data?.success) setCandidates(candRes.data.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handlePostOpportunity = async (e) => {
    e.preventDefault();
    setStatusMsg(null);

    const payload = {
      companyId: company.id,
      title: newOpp.title,
      description: newOpp.description,
      jobType: newOpp.jobType,
      workMode: newOpp.workMode,
      location: newOpp.location,
      minCgpa: Number(newOpp.minCgpa),
      allowedDegrees: newOpp.allowedDegrees,
      targetGraduationYear: Number(newOpp.targetGraduationYear),
      allowedDepartments: newOpp.allowedDepartments,
      salaryOrStipend: newOpp.salaryOrStipend,
      experienceRequirements: newOpp.experienceRequirements,
      deadline: newOpp.deadline,
      externalApplyUrl: newOpp.externalApplyUrl,
      mandatorySkills: newOpp.mandatorySkills.split(',').map((s) => s.trim()).filter(Boolean),
      optionalSkills: newOpp.optionalSkills.split(',').map((s) => s.trim()).filter(Boolean),
    };

    try {
      const res = await api.post('/opportunities', payload);
      setStatusMsg({ type: 'success', text: res.data?.message || 'Opportunity submitted for placement administrator approval.' });
      setShowPostModal(false);
      fetchRecruiterData();
    } catch (err) {
      setStatusMsg({ type: 'error', text: err.response?.data?.message || 'Failed to submit opportunity.' });
    }
  };

  if (loading) {
    return (
      <div className="dashboard-layout">
        <Sidebar />
        <main className="dashboard-main" style={{ padding: '2rem' }}>
          <p>Loading Recruiter Portal...</p>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Recruiter Partner Portal</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Manage {company?.name || 'Company'} campus recruitment drives and explore eligible graduating talent.
              </p>
            </div>
            <button onClick={() => setShowPostModal(!showPostModal)} className="btn btn-primary">
              <Plus size={16} /> Post New Opportunity
            </button>
          </div>

          {statusMsg && (
            <div style={{
              background: statusMsg.type === 'success' ? 'var(--success-bg)' : 'var(--danger-bg)',
              color: statusMsg.type === 'success' ? 'var(--success)' : 'var(--danger)',
              padding: '0.85rem 1rem',
              borderRadius: 'var(--radius-sm)',
              marginBottom: '1.5rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              border: `1px solid ${statusMsg.type === 'success' ? '#bbf7d0' : '#fecaca'}`
            }}>
              {statusMsg.type === 'success' ? <CheckCircle size={18} /> : <AlertCircle size={18} />}
              <span>{statusMsg.text}</span>
            </div>
          )}

          {/* Company Profile Card */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <div className="card-header">
              <h2 className="card-title">Corporate Partner Profile</h2>
              <span className="badge badge-success">Verified Partner</span>
            </div>
            <div className="grid-3">
              <div>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Company Name</p>
                <p style={{ fontSize: '1.1rem', fontWeight: 700 }}>{company?.name}</p>
              </div>
              <div>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Industry</p>
                <p style={{ fontSize: '0.95rem', fontWeight: 600 }}>{company?.industry || 'Technology'}</p>
              </div>
              <div>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Location</p>
                <p style={{ fontSize: '0.95rem', fontWeight: 600 }}>{company?.location || 'India'}</p>
              </div>
            </div>
          </div>

          {/* Post Opportunity Form */}
          {showPostModal && (
            <div className="card" style={{ marginBottom: '2rem', border: '2px solid var(--primary)' }}>
              <div className="card-header">
                <h2 className="card-title">Create Campus Opening</h2>
                <span className="badge badge-warning">Requires Placement Approval</span>
              </div>
              <form onSubmit={handlePostOpportunity}>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Position Title *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. Associate Software Engineer"
                      value={newOpp.title}
                      onChange={(e) => setNewOpp({ ...newOpp, title: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Job Type *</label>
                    <select
                      className="form-control"
                      value={newOpp.jobType}
                      onChange={(e) => setNewOpp({ ...newOpp, jobType: e.target.value })}
                    >
                      <option value="FULL_TIME">Full Time</option>
                      <option value="INTERNSHIP">Internship</option>
                      <option value="PART_TIME">Part Time</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Work Mode *</label>
                    <select
                      className="form-control"
                      value={newOpp.workMode}
                      onChange={(e) => setNewOpp({ ...newOpp, workMode: e.target.value })}
                    >
                      <option value="ON_SITE">On-Site</option>
                      <option value="HYBRID">Hybrid</option>
                      <option value="REMOTE">Remote</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Work Location</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Bangalore / Hyderabad"
                      value={newOpp.location}
                      onChange={(e) => setNewOpp({ ...newOpp, location: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Minimum CGPA Cutoff *</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      required
                      value={newOpp.minCgpa}
                      onChange={(e) => setNewOpp({ ...newOpp, minCgpa: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Eligible Graduation Batch *</label>
                    <input
                      type="number"
                      className="form-control"
                      required
                      value={newOpp.targetGraduationYear}
                      onChange={(e) => setNewOpp({ ...newOpp, targetGraduationYear: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Mandatory Skills (comma separated) *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="Java, Spring Boot, MySQL"
                      value={newOpp.mandatorySkills}
                      onChange={(e) => setNewOpp({ ...newOpp, mandatorySkills: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Application Deadline *</label>
                    <input
                      type="date"
                      className="form-control"
                      required
                      value={newOpp.deadline}
                      onChange={(e) => setNewOpp({ ...newOpp, deadline: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Job Description & Responsibilities *</label>
                  <textarea
                    rows={3}
                    className="form-control"
                    required
                    value={newOpp.description}
                    onChange={(e) => setNewOpp({ ...newOpp, description: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Official External Apply URL (Optional)</label>
                  <input
                    type="url"
                    className="form-control"
                    placeholder="https://company.careers/job/12345"
                    value={newOpp.externalApplyUrl}
                    onChange={(e) => setNewOpp({ ...newOpp, externalApplyUrl: e.target.value })}
                  />
                </div>

                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <button type="submit" className="btn btn-primary">
                    Submit for Approval
                  </button>
                  <button type="button" onClick={() => setShowPostModal(false)} className="btn btn-secondary">
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Company Opportunities Table */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <h2 className="card-title" style={{ marginBottom: '1.25rem' }}>Your Posted Openings</h2>
            {opportunities.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No opportunities posted yet.</p>
            ) : (
              <div className="table-responsive">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Type</th>
                      <th>Mode</th>
                      <th>Min CGPA</th>
                      <th>Deadline</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {opportunities.map((opp) => (
                      <tr key={opp.id}>
                        <td style={{ fontWeight: 600 }}>{opp.title}</td>
                        <td>{opp.jobType}</td>
                        <td>{opp.workMode}</td>
                        <td>{opp.minCgpa}</td>
                        <td>{opp.deadline}</td>
                        <td>
                          <span className={`badge ${opp.status === 'PUBLISHED' ? 'badge-success' : 'badge-warning'}`}>
                            {opp.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Verified Candidates Preview */}
          <div className="card">
            <h2 className="card-title" style={{ marginBottom: '1.25rem' }}>Verified Campus Candidates</h2>
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>Registration No</th>
                    <th>Candidate Name</th>
                    <th>Department</th>
                    <th>Degree</th>
                    <th>Graduation Year</th>
                    <th>CGPA</th>
                    <th>Skills</th>
                  </tr>
                </thead>
                <tbody>
                  {candidates.map((st) => (
                    <tr key={st.id}>
                      <td style={{ fontWeight: 600 }}>{st.registrationNumber}</td>
                      <td>{st.fullName}</td>
                      <td>{st.department}</td>
                      <td>{st.degree}</td>
                      <td>{st.graduationYear}</td>
                      <td><span className="badge badge-primary">{st.cgpa}</span></td>
                      <td>{(st.skills || []).map((s) => s.name).slice(0, 3).join(', ')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default RecruiterDashboard;
