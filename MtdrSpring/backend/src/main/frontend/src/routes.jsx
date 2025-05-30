import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToDoList from './pages/ToDoList';
import KpiDashboard from './pages/KpiDashboard';

const AppRoutes = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ToDoList />} />
                <Route path="/dashboard" element={<KpiDashboard />} />
            </Routes>
        </Router>
    );
};

export default AppRoutes;