import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new URLSearchParams();
        formData.append('username', username);
        formData.append('password', password);

        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'include', // ✅ crucial for session-based auth
            body: formData
        })
            .then(res => {
                console.log('Raw response:', res);
                if (!res.ok) throw new Error('Login failed');
                return res.json();
            })
            .then(data => {
                console.log('Login success:', data);
                navigate('/todopage');
            })
            .catch(err => {
                console.error('Login error:', err);
                alert('Invalid credentials');
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
                        <h2>Login</h2>
                        <form onSubmit={handleSubmit}>
                            <label>User ID:</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Enter your user ID"
                                required
                            />
                            <br />
                            <label>Password:</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                            />
                            <br />
                            <button type="submit">Login</button>
                        </form>

                        <p>Don't have an account? <Link to="/signup">Sign up</Link></p>
                    </div>
                </div>
            </div>
    );

}
