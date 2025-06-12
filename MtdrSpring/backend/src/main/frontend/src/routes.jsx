import { Routes, Route } from 'react-router-dom';
import KPIsDashboard from './pages/KPIsDashboard';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToDoList from './pages/ToDoList';
import Analytics from './pages/Analytics';

const AppRoutes = ({ userOptions }) => {
    return (
        <Routes>
            {/* Public routes */}
            <Route path="/" element={<Login />} />
            <Route path="/signup" element={<Signup />} />


            {/* Protected routes */}
            <Route path="/todopage" element={<ToDoList options={userOptions} />} />
            <Route path="/dashboard" element={<KPIsDashboard options={userOptions} />} />
                <Route path="/analytics" element={<Analytics />} />
            </Routes>
        </Router>
    );
};

export default AppRoutes;
