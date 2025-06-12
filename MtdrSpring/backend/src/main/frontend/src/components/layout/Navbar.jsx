import { useNavigate,Link } from 'react-router-dom';

export default function Navbar() {
    const navigate = useNavigate();

    const handleLogout = async (e) => {
        e.preventDefault();

        try {
            const res = await fetch('/logout', {
                method: 'POST',
                credentials: 'include'
            });

            if (res.ok) {
                console.log("Logout successful");
                navigate('/'); // Redirect to login page
            } else {
                console.error("Logout failed");
            }
        } catch (err) {
            console.error("Error during logout:", err);
        }
    };

    return (
        <nav className="navbar">
            <ul>
                <li><Link to="/todopage">ToDo List</Link></li>
                <li><Link to="/dashboard">Dashboard</Link></li>
                <li><a href="#" onClick={handleLogout}>Logout</a></li>
            </ul>
        </nav>
    );
}
