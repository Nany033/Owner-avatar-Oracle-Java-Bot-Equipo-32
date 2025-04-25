import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToDoPage from './pages/ToDoPage';
import KpiDashboard from './pages/KpiDashboard';

const AppRoutes = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ToDoPage />} />
                <Route path="/dashboard" element={<KpiDashboard />} />
            </Routes>
        </Router>
    );
};

export default AppRoutes;