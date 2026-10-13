import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import { Save, Plus, Trash2, CheckCircle, AlertCircle } from 'lucide-react';

const StudentProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [statusMsg, setStatusMsg] = useState(null);
  const [newSkill, setNewSkill] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/student/profile');
      if (res.data?.success) {
        setProfile(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFieldChange = (field, value) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  const handleAddSkill = () => {
    if (!newSkill.trim()) return;
    const skills = profile.skills || [];
    if (!skills.some(s => s.name.toLowerCase() === newSkill.trim().toLowerCase())) {
      setProfile(prev => ({
        ...prev,
        skills: [...skills, { id: Date.now(), name: newSkill.trim(), proficiencyLevel: 'INTERMEDIATE' }]
      }));
    }
    setNewSkill('');
  };

  const handleRemoveSkill = (skillName) => {
    setProfile(prev => ({
      ...prev,
      skills: (prev.skills || []).filter(s => s.name !== skillName)
    }));
  };

  const handleAddProject = () => {
    const projects = profile.projects || [];
    setProfile(prev => ({
      ...prev,
      projects: [
        ...projects,
        {
          id: Date.now(),
          title: 'New Academic Project',
          technologies: 'React, Spring Boot, MySQL',
          description: 'Designed and developed full-stack application for campus placement tracking.',
          projectUrl: '',
        }
      ]
    }));
  };

  const handleProjectChange = (index, field, value) => {
    const projects = [...(profile.projects || [])];
    projects[index][field] = value;
    setProfile(prev => ({ ...prev, projects }));
  };

  const handleRemoveProject = (index) => {
    const projects = [...(profile.projects || [])];
    projects.splice(index, 1);
    setProfile(prev => ({ ...prev, projects }));
  };

  const handleAddExperience = () => {
    const experiences = profile.experiences || [];
    setProfile(prev => ({
      ...prev,
      experiences: [
        ...experiences,
        {
          id: Date.now(),
          title: 'Software Engineering Intern',
          organization: 'Tech Innovations Corp',
          experienceType: 'INTERNSHIP',
          description: 'Assisted in building REST APIs and writing integration test suites.',
        }
      ]
    }));
  };

  const handleExperienceChange = (index, field, value) => {
    const experiences = [...(profile.experiences || [])];
    experiences[index][field] = value;
    setProfile(prev => ({ ...prev, experiences }));
  };

  const handleRemoveExperience = (index) => {
    const experiences = [...(profile.experiences || [])];
    experiences.splice(index, 1);
    setProfile(prev => ({ ...prev, experiences }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setStatusMsg(null);

    const payload = {
      registrationNumber: profile.registrationNumber,
      college: profile.college,
      department: profile.department,
      degree: profile.degree,
      specialization: profile.specialization,
      graduationYear: Number(profile.graduationYear),
      cgpa: profile.cgpa,
      preferredRoles: profile.preferredRoles,
      preferredLocations: profile.preferredLocations,
      phoneNumber: profile.phoneNumber,
      skills: (profile.skills || []).map(s => s.name),
      projects: profile.projects || [],
      experiences: profile.experiences || [],
    };

    try {
      const res = await api.put('/student/profile', payload);
      if (res.data?.success) {
        setProfile(res.data.data);
        setStatusMsg({ type: 'success', text: 'Profile updated successfully and synced with MySQL.' });
      }
    } catch (err) {
      setStatusMsg({ type: 'error', text: err.response?.data?.message || 'Failed to update profile.' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-layout">
        <Sidebar />
        <main className="dashboard-main" style={{ padding: '2rem' }}>
          <p>Loading profile details...</p>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Student Placement Profile</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Accurate academic & skill details are required for accurate placement drive eligibility matching.
              </p>
            </div>
            <button
              onClick={handleSubmit}
              className="btn btn-primary"
              disabled={saving}
            >
              <Save size={18} /> {saving ? 'Saving...' : 'Save Profile'}
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

          <form onSubmit={handleSubmit}>
            {/* Academic Information */}
            <div className="card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h2 className="card-title">1. Academic & Personal Details</h2>
              </div>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <input type="text" className="form-control" value={profile?.fullName || ''} disabled />
                </div>
                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <input type="email" className="form-control" value={profile?.email || ''} disabled />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input
                    type="tel"
                    className="form-control"
                    value={profile?.phoneNumber || ''}
                    onChange={(e) => handleFieldChange('phoneNumber', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Registration / Roll Number *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={profile?.registrationNumber || ''}
                    onChange={(e) => handleFieldChange('registrationNumber', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">College / Institution *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={profile?.college || ''}
                    onChange={(e) => handleFieldChange('college', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Department / Branch *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={profile?.department || ''}
                    onChange={(e) => handleFieldChange('department', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Degree (e.g. B.Tech, M.Tech, MCA) *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={profile?.degree || ''}
                    onChange={(e) => handleFieldChange('degree', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Specialization</label>
                  <input
                    type="text"
                    className="form-control"
                    value={profile?.specialization || ''}
                    onChange={(e) => handleFieldChange('specialization', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Graduation Year (Batch) *</label>
                  <input
                    type="number"
                    className="form-control"
                    required
                    value={profile?.graduationYear || ''}
                    onChange={(e) => handleFieldChange('graduationYear', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">CGPA (Out of 10.00) *</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    max="10"
                    className="form-control"
                    required
                    value={profile?.cgpa || ''}
                    onChange={(e) => handleFieldChange('cgpa', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Preferred Job Roles</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Frontend Developer, Cloud Engineer"
                    value={profile?.preferredRoles || ''}
                    onChange={(e) => handleFieldChange('preferredRoles', e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Preferred Locations</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Bangalore, Hyderabad, Remote"
                    value={profile?.preferredLocations || ''}
                    onChange={(e) => handleFieldChange('preferredLocations', e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Technical Skills */}
            <div className="card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h2 className="card-title">2. Technical Skills</h2>
              </div>
              <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.25rem' }}>
                <input
                  type="text"
                  className="form-control"
                  style={{ maxWidth: '350px' }}
                  placeholder="Enter skill (e.g. React, Java, Docker)"
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAddSkill(); } }}
                />
                <button type="button" onClick={handleAddSkill} className="btn btn-secondary">
                  <Plus size={16} /> Add Skill
                </button>
              </div>

              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                {(profile?.skills || []).map((sk) => (
                  <span
                    key={sk.name}
                    className="badge badge-primary"
                    style={{ fontSize: '0.85rem', padding: '0.4rem 0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}
                  >
                    {sk.name}
                    <button
                      type="button"
                      onClick={() => handleRemoveSkill(sk.name)}
                      style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', padding: 0 }}
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            </div>

            {/* Projects */}
            <div className="card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h2 className="card-title">3. Projects</h2>
                <button type="button" onClick={handleAddProject} className="btn btn-secondary btn-sm">
                  <Plus size={16} /> Add Project
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {(profile?.projects || []).map((proj, idx) => (
                  <div key={idx} style={{ padding: '1.25rem', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: 700 }}>Project #{idx + 1}</h4>
                      <button
                        type="button"
                        onClick={() => handleRemoveProject(idx)}
                        style={{ color: 'var(--danger)', background: 'none', border: 'none', cursor: 'pointer' }}
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                    <div className="grid-2">
                      <div className="form-group">
                        <label className="form-label">Project Title</label>
                        <input
                          type="text"
                          className="form-control"
                          value={proj.title || ''}
                          onChange={(e) => handleProjectChange(idx, 'title', e.target.value)}
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Technologies Used</label>
                        <input
                          type="text"
                          className="form-control"
                          value={proj.technologies || ''}
                          onChange={(e) => handleProjectChange(idx, 'technologies', e.target.value)}
                        />
                      </div>
                    </div>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                      <label className="form-label">Description & Contributions</label>
                      <textarea
                        rows={2}
                        className="form-control"
                        value={proj.description || ''}
                        onChange={(e) => handleProjectChange(idx, 'description', e.target.value)}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Experiences & Internships */}
            <div className="card" style={{ marginBottom: '2rem' }}>
              <div className="card-header">
                <h2 className="card-title">4. Internships & Experiences</h2>
                <button type="button" onClick={handleAddExperience} className="btn btn-secondary btn-sm">
                  <Plus size={16} /> Add Experience
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {(profile?.experiences || []).map((exp, idx) => (
                  <div key={idx} style={{ padding: '1.25rem', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: 700 }}>Experience #{idx + 1}</h4>
                      <button
                        type="button"
                        onClick={() => handleRemoveExperience(idx)}
                        style={{ color: 'var(--danger)', background: 'none', border: 'none', cursor: 'pointer' }}
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                    <div className="grid-2">
                      <div className="form-group">
                        <label className="form-label">Role Title</label>
                        <input
                          type="text"
                          className="form-control"
                          value={exp.title || ''}
                          onChange={(e) => handleExperienceChange(idx, 'title', e.target.value)}
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Organization / Company</label>
                        <input
                          type="text"
                          className="form-control"
                          value={exp.organization || ''}
                          onChange={(e) => handleExperienceChange(idx, 'organization', e.target.value)}
                        />
                      </div>
                    </div>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                      <label className="form-label">Description</label>
                      <textarea
                        rows={2}
                        className="form-control"
                        value={exp.description || ''}
                        onChange={(e) => handleExperienceChange(idx, 'description', e.target.value)}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
};

export default StudentProfilePage;
