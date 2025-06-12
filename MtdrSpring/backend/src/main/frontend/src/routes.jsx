import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToDoList from './pages/ToDoList';
import KpiDashboard from './pages/KpiDashboard';
import ToDoList from './pages/ToDoList';
import Analytics from './pages/Analytics';

const AppRoutes = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ToDoList />} />
                <Route path="/dashboard" element={<KpiDashboard />} />
                <Route path="/analytics" element={<Analytics />} />
            </Routes>
        </Router>
    );
};

export default AppRoutes;