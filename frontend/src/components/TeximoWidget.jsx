import React, { useState, useRef, useEffect } from 'react';
import api from '../services/api';
import { Send, X, Bot, AlertCircle, RefreshCw } from 'lucide-react';

const ElephantAvatar = ({ size = 48 }) => (
  <svg width={size} height={size} viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="50" cy="50" r="46" fill="#1e3a8a" />
    {/* Elephant Head & Ears */}
    <circle cx="34" cy="40" r="18" fill="#93c5fd" />
    <circle cx="66" cy="40" r="18" fill="#93c5fd" />
    <circle cx="50" cy="48" r="26" fill="#bfdbfe" />
    {/* Trunk */}
    <path d="M46 54 C46 68, 54 75, 59 72 C62 70, 60 64, 55 64 C52 64, 52 56, 52 54" 
          stroke="#60a5fa" strokeWidth="5" strokeLinecap="round" fill="none" />
    {/* Eyes */}
    <circle cx="42" cy="42" r="3.5" fill="#1e3a8a" />
    <circle cx="58" cy="42" r="3.5" fill="#1e3a8a" />
    <circle cx="43" cy="41" r="1" fill="#ffffff" />
    <circle cx="59" cy="41" r="1" fill="#ffffff" />
    {/* Tusks */}
    <path d="M44 58 Q40 64 36 62" stroke="#ffffff" strokeWidth="3" strokeLinecap="round" />
    <path d="M56 58 Q60 64 64 62" stroke="#ffffff" strokeWidth="3" strokeLinecap="round" />
    {/* Cute Cap */}
    <path d="M40 26 Q50 20 60 26 L55 18 Q50 16 45 18 Z" fill="#d97706" />
    <circle cx="50" cy="16" r="3" fill="#fbbf24" />
  </svg>
);

const TeximoWidget = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: 'teximo',
      text: 'Hello! I am Teximo, your placement & career mentor. Ask me anything about upcoming drives, interview rounds, resume advice, or aptitude prep!',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = async (e) => {
    e?.preventDefault();
    if (!input.trim() || loading) return;

    const userMsg = {
      id: Date.now(),
      sender: 'user',
      text: input.trim(),
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    const messageToSend = input.trim();
    setInput('');
    setLoading(true);
    setError(null);

    try {
      const res = await api.post('/chat/teximo', { message: messageToSend });
      if (res.data?.success) {
        const reply = res.data.data.reply;
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now() + 1,
            sender: 'teximo',
            text: reply,
            time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          },
        ]);
      } else {
        throw new Error(res.data?.message || 'Failed to get response');
      }
    } catch (err) {
      setError('Connection interrupted. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', bottom: '24px', right: '24px', zIndex: 1000 }}>
      {/* Closed State Floating Avatar */}
      {!isOpen && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <button
            onClick={() => setIsOpen(true)}
            aria-label="Open Teximo Career Assistant"
            style={{
              background: '#ffffff',
              border: '2px solid #e2e8f0',
              borderRadius: '50%',
              padding: '4px',
              cursor: 'pointer',
              boxShadow: 'var(--shadow-lg)',
              transition: 'transform 0.2s ease',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.05)')}
            onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
          >
            <ElephantAvatar size={56} />
          </button>
          <span style={{
            marginTop: '6px',
            fontSize: '0.8rem',
            fontWeight: 700,
            color: 'var(--primary)',
            backgroundColor: '#ffffff',
            padding: '2px 8px',
            borderRadius: '9999px',
            border: '1px solid #cbd5e1',
            boxShadow: 'var(--shadow-sm)'
          }}>
            Ask me anything
          </span>
        </div>
      )}

      {/* Open State Chat Panel */}
      {isOpen && (
        <div
          style={{
            width: '380px',
            height: '520px',
            maxWidth: 'calc(100vw - 32px)',
            background: '#ffffff',
            borderRadius: 'var(--radius-lg)',
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.15), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
            border: '1px solid var(--border)',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
          }}
        >
          {/* Header */}
          <div
            style={{
              background: 'var(--primary)',
              color: '#ffffff',
              padding: '0.9rem 1.25rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <ElephantAvatar size={34} />
              <div>
                <h3 style={{ fontSize: '0.95rem', fontWeight: 700, margin: 0 }}>Teximo AI</h3>
                <p style={{ fontSize: '0.75rem', opacity: 0.85, margin: 0 }}>Placement Mentor</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#ffffff',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                padding: '4px',
              }}
            >
              <X size={20} />
            </button>
          </div>

          {/* Message List */}
          <div
            style={{
              flex: 1,
              padding: '1rem',
              overflowY: 'auto',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.85rem',
              background: '#f8fafc',
            }}
          >
            {messages.map((m) => (
              <div
                key={m.id}
                style={{
                  alignSelf: m.sender === 'user' ? 'flex-end' : 'flex-start',
                  maxWidth: '82%',
                }}
              >
                <div
                  style={{
                    padding: '0.65rem 0.95rem',
                    borderRadius: '12px',
                    fontSize: '0.875rem',
                    lineHeight: 1.45,
                    backgroundColor: m.sender === 'user' ? 'var(--primary)' : '#ffffff',
                    color: m.sender === 'user' ? '#ffffff' : 'var(--text-primary)',
                    border: m.sender === 'user' ? 'none' : '1px solid var(--border)',
                    boxShadow: 'var(--shadow-sm)',
                    whiteSpace: 'pre-wrap',
                  }}
                >
                  {m.text}
                </div>
                <div
                  style={{
                    fontSize: '0.7rem',
                    color: 'var(--text-muted)',
                    marginTop: '2px',
                    textAlign: m.sender === 'user' ? 'right' : 'left',
                    padding: '0 4px',
                  }}
                >
                  {m.time}
                </div>
              </div>
            ))}

            {loading && (
              <div style={{ alignSelf: 'flex-start', display: 'flex', alignItems: 'center', gap: '6px', padding: '6px 12px', background: '#ffffff', borderRadius: '12px', border: '1px solid var(--border)' }}>
                <RefreshCw size={14} className="spin-animate" color="var(--primary)" />
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Teximo is thinking...</span>
              </div>
            )}

            {error && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '8px', background: 'var(--danger-bg)', color: 'var(--danger)', borderRadius: 'var(--radius-sm)', fontSize: '0.8rem' }}>
                <AlertCircle size={14} />
                <span>{error}</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Prompts */}
          <div style={{ padding: '0.4rem 0.75rem', background: '#f1f5f9', display: 'flex', gap: '0.4rem', overflowX: 'auto', borderTop: '1px solid var(--border)' }}>
            {['Resume tips', 'Aptitude rounds', 'DSA prep'].map((prompt) => (
              <button
                key={prompt}
                onClick={() => setInput(prompt)}
                style={{
                  padding: '2px 8px',
                  fontSize: '0.75rem',
                  background: '#ffffff',
                  border: '1px solid var(--border)',
                  borderRadius: '9999px',
                  color: 'var(--primary)',
                  cursor: 'pointer',
                  whiteSpace: 'nowrap'
                }}
              >
                {prompt}
              </button>
            ))}
          </div>

          {/* Input Form */}
          <form
            onSubmit={handleSend}
            style={{
              padding: '0.75rem 1rem',
              background: '#ffffff',
              borderTop: '1px solid var(--border)',
              display: 'flex',
              gap: '0.5rem',
            }}
          >
            <input
              type="text"
              placeholder="Ask about placement, resumes..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={loading}
              style={{
                flex: 1,
                border: '1px solid var(--border)',
                borderRadius: 'var(--radius-sm)',
                padding: '0.5rem 0.75rem',
                fontSize: '0.875rem',
                outline: 'none',
              }}
            />
            <button
              type="submit"
              disabled={loading || !input.trim()}
              className="btn btn-primary btn-sm"
              style={{ padding: '0.5rem 0.85rem' }}
            >
              <Send size={16} />
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default TeximoWidget;
