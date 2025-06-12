import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
    return (
        <nav className='navbar'>
            <ul>
                <li >
                    <Link to="/" >To-Do List</Link>
                </li>
                <li>
                    <Link to="/dashboard">KPIs Dashboard</Link>
                </li>
                <li>
                    <Link to="/analytics">Analytics</Link>
                </li>
            </ul>
        </nav>
    );
};

export default Navbar;