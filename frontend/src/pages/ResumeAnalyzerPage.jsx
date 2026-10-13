import React, { useState, useEffect } from 'react';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import {
  UploadCloud,
  FileCheck2,
  AlertCircle,
  CheckCircle,
  HelpCircle,
  BookOpen,
  ArrowRight,
  TrendingUp
} from 'lucide-react';

const ResumeAnalyzerPage = () => {
  const [file, setFile] = useState(null);
  const [targetGoal, setTargetGoal] = useState('Software Development Engineer');
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Check if previous analysis exists
    api.get('/resumes/latest-analysis')
      .then((res) => {
        if (res.data?.success && res.data.data) {
          setAnalysis(res.data.data);
        }
      })
      .catch(() => {});
  }, []);

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    if (selected) {
      if (selected.type !== 'application/pdf' && !selected.name.toLowerCase().endsWith('.pdf')) {
        setError('Please upload a valid PDF document.');
        setFile(null);
        return;
      }
      if (selected.size > 500 * 1024 * 1024) {
        setError('File exceeds the 500 MB maximum size limit.');
        setFile(null);
        return;
      }
      setError(null);
      setFile(selected);
    }
  };

  const handleUploadAndAnalyze = async (e) => {
    e.preventDefault();
    if (!file) return;

    setLoading(true);
    setError(null);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('targetGoal', targetGoal);

    try {
      const res = await api.post('/resumes/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      if (res.data?.success) {
        setAnalysis(res.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to analyze PDF resume.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar />
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div style={{ marginBottom: '2rem' }}>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>AI PDF Resume Analyzer</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Upload your existing PDF resume (max 500 MB) for Apache PDFBox text extraction and AI placement scoring.
            </p>
          </div>

          {/* Upload Card */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <form onSubmit={handleUploadAndAnalyze}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 280px', gap: '1.5rem', alignItems: 'center' }}>
                <div>
                  <label className="form-label">Select PDF Resume (Max 500 MB)</label>
                  <input
                    type="file"
                    accept=".pdf"
                    onChange={handleFileChange}
                    className="form-control"
                    style={{ padding: '0.5rem' }}
                  />
                  {file && (
                    <p style={{ fontSize: '0.8rem', color: 'var(--success)', marginTop: '0.35rem', fontWeight: 600 }}>
                      Selected: {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
                    </p>
                  )}
                </div>

                <div>
                  <label className="form-label">Target Role / Specialization</label>
                  <select
                    className="form-control"
                    value={targetGoal}
                    onChange={(e) => setTargetGoal(e.target.value)}
                  >
                    <option value="Software Development Engineer">Software Engineer (SDE)</option>
                    <option value="Frontend Developer">Frontend Developer</option>
                    <option value="Backend Developer">Backend Developer</option>
                    <option value="Data Scientist / AI Engineer">AI / Data Scientist</option>
                    <option value="DevOps & Cloud Engineer">Cloud & DevOps Engineer</option>
                  </select>
                </div>
              </div>

              {error && (
                <div style={{ marginTop: '1rem', color: 'var(--danger)', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <AlertCircle size={16} /> {error}
                </div>
              )}

              <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'flex-end' }}>
                <button
                  type="submit"
                  disabled={!file || loading}
                  className="btn btn-primary"
                >
                  <UploadCloud size={18} /> {loading ? 'Extracting & Analyzing PDF...' : 'Run AI Analysis'}
                </button>
              </div>
            </form>
          </div>

          {/* Analysis Results Display */}
          {analysis && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              {/* Score & Summary Banner */}
              <div className="card" style={{
                background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
                border: '1px solid #cbd5e1',
                padding: '2rem'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1.5rem' }}>
                  <div style={{ maxWidth: '700px' }}>
                    <span className="badge badge-primary" style={{ marginBottom: '0.5rem' }}>
                      Analysis Report
                    </span>
                    <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '0.5rem' }}>
                      Resume Quality Overview
                    </h2>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: 1.6 }}>
                      {analysis.qualitySummary}
                    </p>
                  </div>

                  <div style={{ textAlign: 'center', minWidth: '140px' }}>
                    <div style={{
                      width: '100px',
                      height: '100px',
                      borderRadius: '50%',
                      background: analysis.overallScore >= 75 ? 'var(--success-bg)' : 'var(--warning-bg)',
                      border: `3px solid ${analysis.overallScore >= 75 ? 'var(--success)' : 'var(--warning)'}`,
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      justifyContent: 'center',
                      margin: '0 auto'
                    }}>
                      <span style={{ fontSize: '1.8rem', fontWeight: 800, color: analysis.overallScore >= 75 ? 'var(--success)' : 'var(--warning)' }}>
                        {analysis.overallScore}
                      </span>
                      <span style={{ fontSize: '0.7rem', fontWeight: 700, color: 'var(--text-muted)' }}>/ 100</span>
                    </div>
                    <p style={{ fontSize: '0.8rem', fontWeight: 700, marginTop: '0.5rem', color: 'var(--text-secondary)' }}>
                      ATS Placement Score
                    </p>
                  </div>
                </div>
              </div>

              {/* Skills Analysis: Detected vs Missing */}
              <div className="grid-2">
                <div className="card">
                  <div className="card-header">
                    <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <CheckCircle size={18} color="var(--success)" /> Skills Detected in Resume
                    </h3>
                  </div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                    {analysis.detectedSkills?.map((s) => (
                      <span key={s} className="badge badge-success" style={{ fontSize: '0.85rem' }}>
                        {s}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="card">
                  <div className="card-header">
                    <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <TrendingUp size={18} color="var(--accent)" /> Recommended Skills for {targetGoal}
                    </h3>
                  </div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                    {analysis.missingSkills?.map((s) => (
                      <span key={s} className="badge badge-warning" style={{ fontSize: '0.85rem' }}>
                        + {s}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              {/* Formatting & Grammar Feedback */}
              <div className="grid-2">
                <div className="card">
                  <h3 className="card-title" style={{ marginBottom: '0.75rem' }}>
                    Formatting & Readability
                  </h3>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.5 }}>
                    {analysis.formattingFeedback}
                  </p>
                </div>

                <div className="card">
                  <h3 className="card-title" style={{ marginBottom: '0.75rem' }}>
                    Grammar & Tone Evaluation
                  </h3>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.5 }}>
                    {analysis.grammarFeedback}
                  </p>
                </div>
              </div>

              {/* Improvement Points & Learning Suggestions */}
              <div className="card">
                <div className="card-header">
                  <h3 className="card-title">Actionable Improvement Checklist</h3>
                </div>
                <ul style={{ paddingLeft: '1.25rem', color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.8 }}>
                  {analysis.improvementPoints?.map((pt, i) => (
                    <li key={i}>{pt}</li>
                  ))}
                </ul>
              </div>

              {/* Advisory Disclaimer */}
              <div style={{
                padding: '0.85rem 1.25rem',
                background: 'var(--bg-subtle)',
                borderRadius: 'var(--radius-sm)',
                border: '1px solid var(--border)',
                fontSize: '0.8rem',
                color: 'var(--text-muted)',
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem'
              }}>
                <HelpCircle size={16} />
                <span>{analysis.advisoryDisclaimer}</span>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default ResumeAnalyzerPage;
