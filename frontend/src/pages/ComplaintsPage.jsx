import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import { AlertCircle, Plus, CheckCircle, Clock } from 'lucide-react';

const ComplaintsPage = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    category: 'Placement Drive Issue',
    description: '',
    priority: 'MEDIUM',
  });
  const [submitting, setSubmitting] = useState(false);
  const [statusMsg, setStatusMsg] = useState(null);

  useEffect(() => {
    fetchComplaints();
  }, []);

  const fetchComplaints = async () => {
    try {
      const res = await api.get('/complaints/my');
      if (res.data?.success) {
        setComplaints(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setStatusMsg(null);

    try {
      const res = await api.post('/complaints', formData);
      if (res.data?.success) {
        setStatusMsg({ type: 'success', text: 'Grievance submitted successfully. The placement cell has been notified.' });
        setFormData({ title: '', category: 'Placement Drive Issue', description: '', priority: 'MEDIUM' });
        setShowForm(false);
        fetchComplaints();
      }
    } catch (err) {
      setStatusMsg({ type: 'error', text: err.response?.data?.message || 'Failed to submit grievance.' });
    } finally {
      setSubmitting(false);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'RESOLVED': return 'badge-success';
      case 'IN_PROGRESS': return 'badge-warning';
      case 'UNDER_REVIEW': return 'badge-primary';
      case 'REJECTED': return 'badge-danger';
      default: return 'badge-subtle';
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Student Grievances & Support</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Report issues concerning placement drives, schedule clashes, or profile verification directly to administrators.
              </p>
            </div>
            <button
              onClick={() => setShowForm(!showForm)}
              className="btn btn-primary"
            >
              <Plus size={16} /> {showForm ? 'Cancel' : 'Submit New Grievance'}
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

          {/* Submission Form Modal / Card */}
          {showForm && (
            <div className="card" style={{ marginBottom: '2rem' }}>
              <h2 className="card-title" style={{ marginBottom: '1.25rem' }}>File a Complaint / Request</h2>
              <form onSubmit={handleSubmit}>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Subject / Title *</label>
                    <input
                      type="text"
                      className="form-control"
                      required
                      placeholder="e.g. Interview schedule clash with Semester exam"
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Category *</label>
                    <select
                      className="form-control"
                      value={formData.category}
                      onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    >
                      <option value="Placement Drive Issue">Placement Drive Issue</option>
                      <option value="Eligibility Discrepancy">Eligibility Discrepancy</option>
                      <option value="Schedule Conflict">Schedule Conflict</option>
                      <option value="Profile Verification">Profile Verification</option>
                      <option value="Technical Support">Technical Support</option>
                    </select>
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Priority Level *</label>
                  <select
                    className="form-control"
                    value={formData.priority}
                    onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
                    style={{ maxWidth: '240px' }}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Detailed Description *</label>
                  <textarea
                    rows={4}
                    className="form-control"
                    required
                    placeholder="Provide detailed facts and timestamps..."
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>

                <button type="submit" disabled={submitting} className="btn btn-primary">
                  {submitting ? 'Submitting...' : 'Submit Grievance'}
                </button>
              </form>
            </div>
          )}

          {/* Complaints History */}
          <div className="card">
            <h2 className="card-title" style={{ marginBottom: '1.25rem' }}>Your Complaint History</h2>
            {loading ? (
              <p>Loading complaints history...</p>
            ) : complaints.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', padding: '1rem 0' }}>
                You have not submitted any complaints.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {complaints.map((item) => (
                  <div key={item.id} style={{
                    padding: '1.25rem',
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-sm)',
                    background: '#ffffff'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>{item.title}</h3>
                        <span className={`badge ${getStatusBadge(item.status)}`}>{item.status}</span>
                        <span className="badge badge-subtle">{item.priority}</span>
                      </div>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        Submitted on: {new Date(item.createdAt).toLocaleDateString()}
                      </span>
                    </div>

                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', lineHeight: 1.5, marginBottom: '0.75rem' }}>
                      {item.description}
                    </p>

                    {item.adminResponse && (
                      <div style={{
                        marginTop: '0.75rem',
                        padding: '0.75rem 1rem',
                        background: 'var(--bg-subtle)',
                        borderLeft: '3px solid var(--primary)',
                        borderRadius: '0 4px 4px 0',
                        fontSize: '0.85rem'
                      }}>
                        <strong style={{ color: 'var(--primary)' }}>Administrator Response: </strong>
                        <span style={{ color: 'var(--text-primary)' }}>{item.adminResponse}</span>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default ComplaintsPage;
