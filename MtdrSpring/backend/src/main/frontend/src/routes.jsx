import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'; // ✅ keep this only ONCE
import KPIsDashboard from './pages/KPIsDashboard';
import ToDoList from './pages/ToDoList';
import Analytics from './pages/Analytics';
import Login from './components/Login';
import Signup from './components/Signup';


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
    );
};

export default AppRoutes;
