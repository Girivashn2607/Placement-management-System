import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import {
  FileText,
  Download,
  Sparkles,
  Save,
  Plus,
  Trash2,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

const ResumeBuilderPage = () => {
  const [resumeData, setResumeData] = useState({
    personalInfo: {
      fullName: '',
      email: '',
      phone: '',
      location: '',
      linkedin: '',
      github: '',
    },
    summary: 'Aspiring Software Engineer with solid foundational knowledge in Java, Spring Boot, and modern web architectures. Passionate about designing robust distributed systems and clean algorithms.',
    education: [
      {
        institution: 'University Institute of Technology',
        degree: 'B.Tech',
        fieldOfStudy: 'Computer Science & Engineering',
        startYear: 2022,
        endYear: 2026,
        gradeOrCgpa: '8.5 CGPA',
      },
    ],
    skills: ['Java', 'Spring Boot', 'React.js', 'MySQL', 'Git', 'Data Structures', 'REST APIs'],
    projects: [
      {
        title: 'Placement Management System',
        technologies: 'React, Spring Boot, MySQL',
        description: 'Engineered an end-to-end full-stack portal with automated student profile matching, role-based security, and AI integrations.',
      },
    ],
    experiences: [
      {
        title: 'Full Stack Development Intern',
        organization: 'Tech Innovations Corp',
        experienceType: 'INTERNSHIP',
        description: 'Developed scalable microservices and optimized SQL queries, reducing API response times by 25%.',
      },
    ],
    certifications: ['AWS Certified Cloud Practitioner', 'Oracle Certified Professional: Java SE'],
  });

  const [newSkill, setNewSkill] = useState('');
  const [savedResumeId, setSavedResumeId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [downloading, setDownloading] = useState(false);
  const [aiLoading, setAiLoading] = useState(false);
  const [message, setMessage] = useState(null);

  // Pre-fill student data from profile
  useEffect(() => {
    api.get('/student/profile')
      .then((res) => {
        if (res.data?.success) {
          const p = res.data.data;
          setResumeData((prev) => ({
            ...prev,
            personalInfo: {
              ...prev.personalInfo,
              fullName: p.fullName || '',
              email: p.email || '',
              phone: p.phoneNumber || '',
            },
            skills: p.skills?.length > 0 ? p.skills.map((s) => s.name) : prev.skills,
            projects: p.projects?.length > 0 ? p.projects : prev.projects,
          }));
        }
      })
      .catch(() => {});
  }, []);

  const handlePersonalInfo = (field, val) => {
    setResumeData((prev) => ({
      ...prev,
      personalInfo: { ...prev.personalInfo, [field]: val },
    }));
  };

  const handleAiImprove = async (textToImprove, type) => {
    setAiLoading(true);
    setMessage(null);
    try {
      const res = await api.post('/resumes/ai/suggest', { content: textToImprove, targetRole: 'Software Engineer' });
      if (res.data?.success) {
        const improved = res.data.data.improvedContent;
        if (type === 'summary') {
          setResumeData((prev) => ({ ...prev, summary: improved }));
        }
        setMessage({ type: 'success', text: 'Content enhanced using AI action formula!' });
      }
    } catch (err) {
      setMessage({ type: 'error', text: 'AI suggestion could not be generated.' });
    } finally {
      setAiLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setMessage(null);
    try {
      const res = await api.post('/resumes/builder/save', {
        title: 'Resume - ' + (resumeData.personalInfo.fullName || 'Student'),
        content: resumeData,
      });
      if (res.data?.success) {
        setSavedResumeId(res.data.data);
        setMessage({ type: 'success', text: 'Resume saved to your profile in MySQL!' });
      }
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to save resume.' });
    } finally {
      setSaving(false);
    }
  };

  const handleDownload = async () => {
    setDownloading(true);
    setMessage(null);
    try {
      // First save to guarantee ID
      const saveRes = await api.post('/resumes/builder/save', {
        title: 'Resume - ' + (resumeData.personalInfo.fullName || 'Student'),
        content: resumeData,
      });
      const id = saveRes.data.data;
      setSavedResumeId(id);

      // Download PDF byte array
      const response = await api.get(`/resumes/${id}/download`, { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Resume_${resumeData.personalInfo.fullName || 'Candidate'}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to download PDF.' });
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>AI Resume Builder</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Build ATS-compliant single-page resumes with AI summary and bullet improvement.
              </p>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button onClick={handleSave} className="btn btn-secondary" disabled={saving}>
                <Save size={16} /> {saving ? 'Saving...' : 'Save'}
              </button>
              <button onClick={handleDownload} className="btn btn-primary" disabled={downloading}>
                <Download size={16} /> {downloading ? 'Generating PDF...' : 'Download PDF'}
              </button>
            </div>
          </div>

          {message && (
            <div style={{
              background: message.type === 'success' ? 'var(--success-bg)' : 'var(--danger-bg)',
              color: message.type === 'success' ? 'var(--success)' : 'var(--danger)',
              padding: '0.75rem 1rem',
              borderRadius: 'var(--radius-sm)',
              marginBottom: '1.5rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              border: `1px solid ${message.type === 'success' ? '#bbf7d0' : '#fecaca'}`
            }}>
              {message.type === 'success' ? <CheckCircle size={16} /> : <AlertCircle size={16} />}
              <span>{message.text}</span>
            </div>
          )}

          {/* Builder + Live Preview Split Layout */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
            {/* Editor Panel */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              {/* Personal Info */}
              <div className="card">
                <h3 className="card-title" style={{ marginBottom: '1rem' }}>Personal & Contact Details</h3>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Full Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={resumeData.personalInfo.fullName}
                      onChange={(e) => handlePersonalInfo('fullName', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      value={resumeData.personalInfo.email}
                      onChange={(e) => handlePersonalInfo('email', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Phone</label>
                    <input
                      type="tel"
                      className="form-control"
                      value={resumeData.personalInfo.phone}
                      onChange={(e) => handlePersonalInfo('phone', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Location (City, Country)</label>
                    <input
                      type="text"
                      className="form-control"
                      value={resumeData.personalInfo.location}
                      onChange={(e) => handlePersonalInfo('location', e.target.value)}
                    />
                  </div>
                </div>
              </div>

              {/* Summary with AI Enhance */}
              <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                  <h3 className="card-title">Professional Summary</h3>
                  <button
                    type="button"
                    onClick={() => handleAiImprove(resumeData.summary, 'summary')}
                    className="btn btn-secondary btn-sm"
                    disabled={aiLoading}
                    style={{ color: 'var(--primary)', borderColor: 'var(--primary)' }}
                  >
                    <Sparkles size={14} /> {aiLoading ? 'Enhancing...' : 'AI Enhance'}
                  </button>
                </div>
                <textarea
                  rows={4}
                  className="form-control"
                  value={resumeData.summary}
                  onChange={(e) => setResumeData({ ...resumeData, summary: e.target.value })}
                />
              </div>

              {/* Skills */}
              <div className="card">
                <h3 className="card-title" style={{ marginBottom: '0.75rem' }}>Technical Skills</h3>
                <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Add skill (e.g. Docker, Python)"
                    value={newSkill}
                    onChange={(e) => setNewSkill(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        if (newSkill.trim()) {
                          setResumeData({ ...resumeData, skills: [...resumeData.skills, newSkill.trim()] });
                          setNewSkill('');
                        }
                      }
                    }}
                  />
                  <button
                    type="button"
                    onClick={() => {
                      if (newSkill.trim()) {
                        setResumeData({ ...resumeData, skills: [...resumeData.skills, newSkill.trim()] });
                        setNewSkill('');
                      }
                    }}
                    className="btn btn-secondary"
                  >
                    <Plus size={16} /> Add
                  </button>
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                  {resumeData.skills.map((s, i) => (
                    <span key={i} className="badge badge-primary" style={{ padding: '0.35rem 0.65rem' }}>
                      {s}{' '}
                      <button
                        onClick={() => {
                          const updated = [...resumeData.skills];
                          updated.splice(i, 1);
                          setResumeData({ ...resumeData, skills: updated });
                        }}
                        style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', marginLeft: '4px' }}
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>
              </div>
            </div>

            {/* Live Resume Sheet Preview */}
            <div style={{
              background: '#ffffff',
              border: '1px solid #cbd5e1',
              borderRadius: 'var(--radius-sm)',
              padding: '2.5rem',
              boxShadow: 'var(--shadow-md)',
              minHeight: '700px',
              color: '#0f172a',
              fontFamily: "'Inter', sans-serif",
            }}>
              <div style={{ textAlign: 'center', borderBottom: '2px solid #1e3a8a', paddingBottom: '1rem', marginBottom: '1.25rem' }}>
                <h2 style={{ fontSize: '1.6rem', fontWeight: 800, textTransform: 'uppercase', color: '#1e3a8a', letterSpacing: '0.5px' }}>
                  {resumeData.personalInfo.fullName || 'YOUR NAME'}
                </h2>
                <p style={{ fontSize: '0.85rem', color: '#475569', marginTop: '0.25rem' }}>
                  {[resumeData.personalInfo.email, resumeData.personalInfo.phone, resumeData.personalInfo.location].filter(Boolean).join('  |  ')}
                </p>
              </div>

              {resumeData.summary && (
                <div style={{ marginBottom: '1.25rem' }}>
                  <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#1e3a8a', textTransform: 'uppercase', borderBottom: '1px solid #e2e8f0', paddingBottom: '2px', marginBottom: '0.4rem' }}>
                    Professional Summary
                  </h4>
                  <p style={{ fontSize: '0.85rem', lineHeight: 1.5, color: '#334155' }}>
                    {resumeData.summary}
                  </p>
                </div>
              )}

              {resumeData.education?.length > 0 && (
                <div style={{ marginBottom: '1.25rem' }}>
                  <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#1e3a8a', textTransform: 'uppercase', borderBottom: '1px solid #e2e8f0', paddingBottom: '2px', marginBottom: '0.4rem' }}>
                    Education
                  </h4>
                  {resumeData.education.map((edu, idx) => (
                    <div key={idx} style={{ marginBottom: '0.4rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', fontWeight: 700 }}>
                        <span>{edu.degree} - {edu.fieldOfStudy}</span>
                        <span>{edu.endYear}</span>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: '#475569' }}>
                        {edu.institution} | {edu.gradeOrCgpa}
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {resumeData.skills?.length > 0 && (
                <div style={{ marginBottom: '1.25rem' }}>
                  <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#1e3a8a', textTransform: 'uppercase', borderBottom: '1px solid #e2e8f0', paddingBottom: '2px', marginBottom: '0.4rem' }}>
                    Technical Skills
                  </h4>
                  <p style={{ fontSize: '0.85rem', lineHeight: 1.5, color: '#334155' }}>
                    {resumeData.skills.join(' • ')}
                  </p>
                </div>
              )}

              {resumeData.projects?.length > 0 && (
                <div style={{ marginBottom: '1.25rem' }}>
                  <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#1e3a8a', textTransform: 'uppercase', borderBottom: '1px solid #e2e8f0', paddingBottom: '2px', marginBottom: '0.4rem' }}>
                    Key Projects
                  </h4>
                  {resumeData.projects.map((proj, idx) => (
                    <div key={idx} style={{ marginBottom: '0.6rem' }}>
                      <div style={{ fontSize: '0.85rem', fontWeight: 700 }}>
                        {proj.title} <span style={{ fontWeight: 400, color: '#64748b' }}>({proj.technologies})</span>
                      </div>
                      <p style={{ fontSize: '0.8rem', color: '#334155', lineHeight: 1.4 }}>
                        {proj.description}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default ResumeBuilderPage;
