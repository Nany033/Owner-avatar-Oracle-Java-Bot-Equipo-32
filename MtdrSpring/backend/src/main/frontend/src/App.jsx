import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/layout/Navbar';
import ToDoList from './pages/ToDoList';
import KpisDashboard from './pages/KPIsDashboard';
import Analytics from './pages/Analytics';
import './index.css';

import API from './API';

function App() {
    const [userOptions, setUserOptions] = useState([]);
    const [loading, setLoading] = useState(true);

    // Fetch user options from the API on component mount to populate the filter dropdown
    // and pass them to the KPIs Dashboard page and ToDoList page.
    useEffect(() => {
        fetch(API.USERS)
            .then(res => res.json())
            .then(data => {
                setUserOptions(data); 
                setLoading(false);
            })
            .catch(err => {
                console.error('Error fetching users:', err);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading app...</p>;
    
    return (
        <Router>
            <Navbar />
            <div className='page-container'>
                <Routes>
                    <Route path="/" element={<ToDoList options={userOptions} />} />
                    <Route path="/dashboard" element={<KpisDashboard options={userOptions} />} />
                    <Route path="/analytics" element={<Analytics />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
