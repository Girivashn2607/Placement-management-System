import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  User,
  Briefcase,
  FileText,
  FileCheck2,
  AlertCircle,
  Building2,
  Calendar,
  ShieldAlert
} from 'lucide-react';

const Sidebar = () => {
  const { user } = useAuth();
  const role = user?.role;

  const linkStyle = ({ isActive }) => ({
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    padding: '0.75rem 1rem',
    borderRadius: 'var(--radius-sm)',
    fontSize: '0.9rem',
    fontWeight: 600,
    color: isActive ? 'var(--primary)' : 'var(--text-secondary)',
    backgroundColor: isActive ? 'var(--primary-light)' : 'transparent',
    marginBottom: '0.35rem',
    transition: 'all 0.15s ease',
  });

  return (
    <aside style={{
      width: '260px',
      backgroundColor: '#ffffff',
      borderRight: '1px solid var(--border)',
      padding: '1.5rem 1rem',
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem',
      flexShrink: 0
    }}>
      <div style={{ padding: '0 0.5rem 1rem', borderBottom: '1px solid var(--border)', marginBottom: '0.75rem' }}>
        <p style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--text-muted)' }}>
          {role} PORTAL
        </p>
      </div>

      {role === 'STUDENT' && (
        <>
          <NavLink to="/student/dashboard" style={linkStyle}>
            <LayoutDashboard size={18} /> Overview
          </NavLink>
          <NavLink to="/student/profile" style={linkStyle}>
            <User size={18} /> Placement Profile
          </NavLink>
          <NavLink to="/opportunities" style={linkStyle}>
            <Briefcase size={18} /> Opportunities
          </NavLink>
          <NavLink to="/resume/builder" style={linkStyle}>
            <FileText size={18} /> AI Resume Builder
          </NavLink>
          <NavLink to="/resume/analyzer" style={linkStyle}>
            <FileCheck2 size={18} /> AI Resume Analyzer
          </NavLink>
          <NavLink to="/student/complaints" style={linkStyle}>
            <AlertCircle size={18} /> Complaints & Support
          </NavLink>
        </>
      )}

      {role === 'ADMIN' && (
        <>
          <NavLink to="/admin" style={linkStyle}>
            <LayoutDashboard size={18} /> Dashboard Overview
          </NavLink>
          <NavLink to="/admin/students" style={linkStyle}>
            <User size={18} /> Student Records
          </NavLink>
          <NavLink to="/admin/companies" style={linkStyle}>
            <Building2 size={18} /> Companies & Drives
          </NavLink>
          <NavLink to="/opportunities" style={linkStyle}>
            <Briefcase size={18} /> All Opportunities
          </NavLink>
          <NavLink to="/admin/complaints" style={linkStyle}>
            <ShieldAlert size={18} /> Grievance Management
          </NavLink>
        </>
      )}

      {role === 'RECRUITER' && (
        <>
          <NavLink to="/recruiter" style={linkStyle}>
            <Building2 size={18} /> Company Dashboard
          </NavLink>
          <NavLink to="/opportunities" style={linkStyle}>
            <Briefcase size={18} /> Company Openings
          </NavLink>
          <NavLink to="/recruiter/candidates" style={linkStyle}>
            <User size={18} /> Verified Candidates
          </NavLink>
        </>
      )}
    </aside>
  );
};

export default Sidebar;
