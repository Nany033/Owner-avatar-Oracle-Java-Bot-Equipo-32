import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ToDoList from './pages/ToDoList';
import KpisDashboard from './pages/KPIsDashboard';
import API from './API';

function App() {
    const [userOptions, setUserOptions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(API.USERS)
            .then(res => res.json())
            .then(data => {
                const formatted = data.map(user => ({
                    id: user.id,
                    label: user.name,
                    value: user.id
                }));
                setUserOptions(formatted);
                // console.log(formatted);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading app...</p>;

    return (
        <Router>
            <Navbar />
            <div className='page-container'>
                <Routes>
                    <Route path="/" element={<ToDoList />} />
                    <Route path="/dashboard" element={<KpisDashboard options={userOptions} />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
