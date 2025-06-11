import { useState } from 'react';
import API from '../API';

export default function Signup() {
    const [userId, setUserId] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();

        fetch(API.SIGNUP, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId, name, password })
        })
            .then((res) => {
                if (res.ok) {
                    alert("Signup successful!");
                    window.location.href = '/'; // Redirect to login
                } else {
                    alert("Signup failed. Try a different user ID?");
                }
            })
            .catch((err) => {
                console.error("Signup error:", err);
                alert("Error during signup. Please try again.");
            });
    };

    return (
            <div className="auth-page">
                <div className="auth-left">
                    <h1>Welcome to JavaBot</h1>
                    <p>
                        Designed for Teams. Built for Results.
                    </p>
                </div>

                <div className="auth-right">
                    <div className="auth-container">
                        <h2>Create Your Account</h2>

                        <form onSubmit={handleSubmit}>

                            {/* User ID Field */}
                            <div className="form-group">
                                <label htmlFor="userId">User ID</label>
                                <input
                                    type="text"
                                    id="userId"
                                    name="userId"
                                    value={userId}
                                    onChange={(e) => setUserId(e.target.value)}
                                    placeholder="Enter your user ID"
                                    required
                                />
                            </div>

                            {/* Full Name Field */}
                            <div className="form-group">
                                <label htmlFor="name">Full Name</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    placeholder="Enter your full name"
                                    required
                                />
                            </div>

                            {/* Password Field */}
                            <div className="form-group">
                                <label htmlFor="password">Password</label>
                                <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Create a strong password"
                                    required
                                />
                            </div>

                            <button type="submit">Register</button>
                        </form>

                        {/* Navigation to Login */}
                        <p style={{ marginTop: '1rem', textAlign: 'center' }}>
                            Already have an account?{' '}
                            <a href="/" style={{ color: 'var(--oracle-dark-red)', fontWeight: 'bold', textDecoration: 'none' }}>
                                Back to Login
                            </a>
                        </p>
                    </div>
                </div>
            </div>
    );



}
