import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ToDoList from './pages/ToDoList';
import KpisDashboard from './pages/KPIsDashboard';


function App() {
    return (
        <Router>
            <Navbar />
                <div className='page-container'>
                    <Routes>
                        <Route path="/" element={<ToDoList />} />
                        <Route path="/dashboard" element={<KpisDashboard />} />
                    </Routes>
                </div>

        </Router>
    );
}

export default App;
