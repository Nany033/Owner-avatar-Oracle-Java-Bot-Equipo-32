import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToDoPage from './pages/ToDoPage';
import KpiDashboard from './pages/KpiDashboard';
import ToDoList from './pages/ToDoList';

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