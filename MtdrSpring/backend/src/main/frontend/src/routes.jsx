import { Routes, Route } from 'react-router-dom';
import KPIsDashboard from './pages/KPIsDashboard';
import ToDoList from './pages/ToDoList';
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
        </Routes>
    );
};

export default AppRoutes;
