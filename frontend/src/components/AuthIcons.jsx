export function MailIcon() {
  return (
    <svg className="auth-svg-icon" viewBox="0 0 24 24" aria-hidden="true">
      <path fill="currentColor" d="M3 5h18a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Zm9 7.2L4.6 7h14.8L12 12.2Zm0 1.8L4 8.4V17h16V8.4L12 14Z" />
    </svg>
  )
}

export function LockIcon() {
  return (
    <svg className="auth-svg-icon" viewBox="0 0 24 24" aria-hidden="true">
      <path fill="currentColor" d="M17 9V7a5 5 0 0 0-10 0v2H5v12h14V9h-2Zm-8-2a3 3 0 0 1 6 0v2H9V7Zm3 11a2 2 0 1 1 0-4 2 2 0 0 1 0 4Z" />
    </svg>
  )
}

export function EyeIcon({ off }) {
  return (
    <svg className="auth-svg-icon" viewBox="0 0 24 24" aria-hidden="true">
      <path fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z" />
      <circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" strokeWidth="2" />
      {off && <path stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" d="M4 4l16 16" />}
    </svg>
  )
}

export function GoogleIcon() {
  return (
    <svg className="auth-brand-icon" viewBox="0 0 48 48" aria-hidden="true">
      <path fill="#EA4335" d="M24 9.5c3.5 0 6.6 1.2 9.1 3.6l6.8-6.8C35.8 2.4 30.3 0 24 0 14.6 0 6.5 5.4 2.6 13.2l7.9 6.1C12.4 13.6 17.7 9.5 24 9.5Z" />
      <path fill="#4285F4" d="M46.5 24.5c0-1.6-.1-3.1-.4-4.5H24v9h12.7c-.6 3-2.3 5.5-4.8 7.2l7.6 5.9c4.4-4.1 7-10.1 7-17.6Z" />
      <path fill="#FBBC05" d="M10.5 28.7a14.5 14.5 0 0 1 0-9.4l-7.9-6.1a24 24 0 0 0 0 21.6l7.9-6.1Z" />
      <path fill="#34A853" d="M24 48c6.5 0 11.9-2.1 15.9-5.8l-7.6-5.9c-2.1 1.4-4.9 2.3-8.3 2.3-6.3 0-11.6-4.1-13.5-9.8l-7.9 6.1C6.5 42.6 14.6 48 24 48Z" />
    </svg>
  )
}

export function AppleIcon() {
  return (
    <svg className="auth-brand-icon" viewBox="0 0 22 26" aria-hidden="true">
      <path fill="currentColor" d="M16.37 1.43c0 1.14-.46 2.22-1.2 3.01-.8.85-2.1 1.5-3.15 1.42-.13-1.1.42-2.25 1.15-3 .82-.85 2.2-1.46 3.2-1.43Zm4.13 15.67c-.55 1.27-.82 1.84-1.53 2.96-1 1.57-2.4 3.52-4.14 3.54-1.55.02-1.95-1-4.05-.99-2.1.01-2.54 1.01-4.09.99-1.74-.02-3.07-1.78-4.07-3.35C.2 19.1-.5 14.2 1.3 11.5c1.27-1.92 3.28-3.04 5.17-3.04 1.92 0 3.12 1.05 4.7 1.05 1.53 0 2.46-1.05 4.67-1.05 1.68 0 3.46.92 4.73 2.5-4.15 2.28-3.48 8.2.93 9.14Z" />
    </svg>
  )
}

export function UserIcon() {
  return (
    <svg className="auth-svg-icon" viewBox="0 0 24 24" aria-hidden="true">
      <path fill="currentColor" d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0 2c-4.4 0-8 2.2-8 5v3h16v-3c0-2.8-3.6-5-8-5Z" />
    </svg>
  )
}
