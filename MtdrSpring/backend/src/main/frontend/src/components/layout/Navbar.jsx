import { Link } from 'react-router-dom';

export default function Navbar() {
    return (
        <nav className="navbar">
            <ul>
                <li><Link to="/">ToDo List</Link></li>
                <li><Link to="/dashboard">Dashboard</Link></li>
                <li><a href="/">Logout</a></li> {/* Spring Security handles /logout */}
            </ul>
        </nav>
    );
}
