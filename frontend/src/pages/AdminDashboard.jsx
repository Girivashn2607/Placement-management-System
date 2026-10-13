import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import {
  Users,
  Building2,
  Briefcase,
  Calendar,
  Download,
  Check,
  X,
  Plus,
  AlertCircle
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';

const AdminDashboard = () => {
  const [overview, setOverview] = useState(null);
  const [students, setStudents] = useState([]);
  const [pendingOpps, setPendingOpps] = useState([]);
  const [loading, setLoading] = useState(true);

  // Filters for students
  const [deptFilter, setDeptFilter] = useState('');
  const [gradYearFilter, setGradYearFilter] = useState('');
  const [minCgpaFilter, setMinCgpaFilter] = useState('');

  // Announcement state
  const [announcementText, setAnnouncementText] = useState({ title: '', content: '', priority: 'MEDIUM' });

  useEffect(() => {
    fetchData();
  }, [deptFilter, gradYearFilter, minCgpaFilter]);

  const fetchData = async () => {
    try {
      const [ovRes, stRes, oppRes] = await Promise.all([
        api.get('/admin/overview'),
        api.get('/admin/students', {
          params: {
            department: deptFilter || undefined,
            graduationYear: gradYearFilter || undefined,
            minCgpa: minCgpaFilter || undefined,
          },
        }),
        api.get('/admin/opportunities/pending'),
      ]);

      if (ovRes.data?.success) setOverview(ovRes.data.data);
      if (stRes.data?.success) setStudents(stRes.data.data.content || []);
      if (oppRes.data?.success) setPendingOpps(oppRes.data.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveOpportunity = async (id, status) => {
    try {
      await api.patch(`/admin/opportunities/${id}/status?status=${status}`);
      fetchData();
    } catch (err) {
      alert('Error updating status: ' + err.message);
    }
  };

  const handlePostAnnouncement = async (e) => {
    e.preventDefault();
    if (!announcementText.title.trim() || !announcementText.content.trim()) return;

    try {
      await api.post('/admin/announcements', {
        title: announcementText.title,
        content: announcementText.content,
        priority: announcementText.priority,
        targetRole: 'ALL',
      });
      setAnnouncementText({ title: '', content: '', priority: 'MEDIUM' });
      alert('Announcement published successfully.');
    } catch (err) {
      alert('Error posting announcement: ' + err.message);
    }
  };

  const handleExportCsv = async () => {
    try {
      const res = await api.get('/admin/students/export-csv', { responseType: 'blob' });
      const blob = new Blob([res.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'students_placement_report.csv';
      document.body.appendChild(a);
      a.click();
      a.remove();
    } catch (err) {
      alert('CSV export failed: ' + err.message);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-layout">
        <Sidebar />
        <main className="dashboard-main" style={{ padding: '2rem' }}>
          <p>Loading administration console...</p>
        </main>
      </div>
    );
  }

  // Format Recharts data
  const deptData = Object.entries(overview?.departmentDistribution || {}).map(([name, count]) => ({
    name,
    students: count,
  }));

  const complaintData = Object.entries(overview?.complaintsByStatus || {}).map(([status, count]) => ({
    name: status,
    value: count,
  }));

  const COLORS = ['#1e3a8a', '#0f766e', '#d97706', '#16a34a', '#dc2626'];

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Placement Administrator Console</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                University placement records, company partner management, and live grievance oversight.
              </p>
            </div>
            <button onClick={handleExportCsv} className="btn btn-secondary">
              <Download size={16} /> Export Students CSV
            </button>
          </div>

          {/* Overview KPIs */}
          <div className="grid-4" style={{ marginBottom: '2rem' }}>
            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Registered Students</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, marginTop: '0.25rem' }}>
                    {overview?.totalStudents || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--primary-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--primary)' }}>
                  <Users size={22} />
                </div>
              </div>
            </div>

            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Active Companies</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, marginTop: '0.25rem' }}>
                    {overview?.totalCompanies || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--secondary-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--secondary)' }}>
                  <Building2 size={22} />
                </div>
              </div>
            </div>

            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Published Opportunities</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--primary)', marginTop: '0.25rem' }}>
                    {overview?.activeOpportunities || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--primary-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--primary)' }}>
                  <Briefcase size={22} />
                </div>
              </div>
            </div>

            <div className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Scheduled Drives</p>
                  <h3 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--accent)', marginTop: '0.25rem' }}>
                    {overview?.totalDrives || 0}
                  </h3>
                </div>
                <div style={{ background: 'var(--accent-light)', padding: '0.65rem', borderRadius: '10px', color: 'var(--accent)' }}>
                  <Calendar size={22} />
                </div>
              </div>
            </div>
          </div>

          {/* Charts Row */}
          <div className="grid-2" style={{ marginBottom: '2rem' }}>
            <div className="card">
              <h2 className="card-title" style={{ marginBottom: '1rem' }}>Department-Wise Student Distribution</h2>
              <div style={{ width: '100%', height: 260 }}>
                {deptData.length > 0 ? (
                  <ResponsiveContainer>
                    <BarChart data={deptData}>
                      <XAxis dataKey="name" fontSize={11} interval={0} angle={-15} textAnchor="end" />
                      <YAxis allowDecimals={false} fontSize={12} />
                      <Tooltip />
                      <Bar dataKey="students" fill="#1e3a8a" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <p style={{ color: 'var(--text-muted)', paddingTop: '4rem', textAlign: 'center' }}>No department data available</p>
                )}
              </div>
            </div>

            <div className="card">
              <h2 className="card-title" style={{ marginBottom: '1rem' }}>Complaint Resolution Pipeline</h2>
              <div style={{ width: '100%', height: 260 }}>
                {complaintData.length > 0 ? (
                  <ResponsiveContainer>
                    <PieChart>
                      <Pie
                        data={complaintData}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={85}
                        label
                      >
                        {complaintData.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </PieChart>
                  </ResponsiveContainer>
                ) : (
                  <p style={{ color: 'var(--text-muted)', paddingTop: '4rem', textAlign: 'center' }}>No complaints filed</p>
                )}
              </div>
            </div>
          </div>

          {/* Pending Recruiter Opportunities Approval */}
          {pendingOpps.length > 0 && (
            <div className="card" style={{ marginBottom: '2rem', border: '1px solid #fde68a' }}>
              <div className="card-header">
                <h2 className="card-title" style={{ color: '#b45309' }}>
                  Pending Recruiter Opportunities Requiring Approval ({pendingOpps.length})
                </h2>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {pendingOpps.map((opp) => (
                  <div key={opp.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', background: '#fffbeb', borderRadius: 'var(--radius-sm)' }}>
                    <div>
                      <h4 style={{ fontWeight: 700 }}>{opp.title} ({opp.company?.name})</h4>
                      <p style={{ fontSize: '0.85rem', color: '#92400e' }}>
                        Type: {opp.jobType} | Mode: {opp.workMode} | Min CGPA: {opp.minCgpa} | Deadline: {opp.deadline}
                      </p>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button
                        onClick={() => handleApproveOpportunity(opp.id, 'PUBLISHED')}
                        className="btn btn-success btn-sm"
                      >
                        <Check size={14} /> Approve & Publish
                      </button>
                      <button
                        onClick={() => handleApproveOpportunity(opp.id, 'ARCHIVED')}
                        className="btn btn-danger btn-sm"
                      >
                        <X size={14} /> Reject
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Student Records Table with Filters */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <div className="card-header">
              <h2 className="card-title">Student Records & CGPA Verification</h2>
            </div>

            {/* Filter Bar */}
            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
              <input
                type="text"
                placeholder="Filter department..."
                className="form-control"
                style={{ maxWidth: '220px' }}
                value={deptFilter}
                onChange={(e) => setDeptFilter(e.target.value)}
              />
              <input
                type="number"
                placeholder="Graduation Year (e.g. 2026)"
                className="form-control"
                style={{ maxWidth: '220px' }}
                value={gradYearFilter}
                onChange={(e) => setGradYearFilter(e.target.value)}
              />
              <input
                type="number"
                step="0.1"
                placeholder="Min CGPA cutoff..."
                className="form-control"
                style={{ maxWidth: '180px' }}
                value={minCgpaFilter}
                onChange={(e) => setMinCgpaFilter(e.target.value)}
              />
            </div>

            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>Reg Number</th>
                    <th>Name</th>
                    <th>Department</th>
                    <th>Degree</th>
                    <th>Batch</th>
                    <th>CGPA</th>
                    <th>Skills</th>
                  </tr>
                </thead>
                <tbody>
                  {students.map((st) => (
                    <tr key={st.id}>
                      <td style={{ fontWeight: 600 }}>{st.registrationNumber}</td>
                      <td>{st.fullName}</td>
                      <td>{st.department}</td>
                      <td>{st.degree}</td>
                      <td>{st.graduationYear}</td>
                      <td>
                        <span className="badge badge-primary">{st.cgpa}</span>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                          {(st.skills || []).map((s) => s.name).slice(0, 3).join(', ')}
                          {(st.skills || []).length > 3 ? '...' : ''}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Post University Announcement Form */}
          <div className="card">
            <h2 className="card-title" style={{ marginBottom: '1rem' }}>Post University Placement Notice</h2>
            <form onSubmit={handlePostAnnouncement}>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Title / Headline</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Google India Pre-Placement Talk Announced"
                    value={announcementText.title}
                    onChange={(e) => setAnnouncementText({ ...announcementText, title: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Priority</label>
                  <select
                    className="form-control"
                    value={announcementText.priority}
                    onChange={(e) => setAnnouncementText({ ...announcementText, priority: e.target.value })}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Notice Details</label>
                <textarea
                  rows={3}
                  className="form-control"
                  required
                  placeholder="Enter official schedule, zoom link or dress code requirements..."
                  value={announcementText.content}
                  onChange={(e) => setAnnouncementText({ ...announcementText, content: e.target.value })}
                />
              </div>
              <button type="submit" className="btn btn-primary">
                Publish Announcement
              </button>
            </form>
          </div>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;
