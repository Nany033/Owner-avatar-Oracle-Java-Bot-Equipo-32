import React, { useState, useEffect } from 'react';
import Filter from '../components/ui/Filter';
import KPIcard from '../components/ui/KPIcard';
import TasksSummary from '../components/TasksSummary';
import SprintHours from '../components/tables/SprintHours'; // All users' sprint data
import SprintHoursForUser from '../components/tables/SprintHoursForUser'; // User-specific sprint data
import SprintBarChart from '../components/charts/SprintBarChart';


export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    const [userName, setUserName] = useState('');

    // Update user name when selected user changes
    useEffect(() => {
        const selectedUser = options.find(option => option.userId === selectedUserId);
        setUserName(selectedUser ? selectedUser.name : '');
    }, [selectedUserId, options]);

    const handleSelect = (userId) => {
        setSelectedUserId(userId);
    };

    return (
        <div className="dashboard-container">
            {/* Title and Introduction */}
            <header className="dashboard-header">
                <h1>KPIs Dashboard</h1>
                <p>Welcome to the KPIs Dashboard. View and analyze key performance indicators here.</p>
            </header>

            {/* Filter for Selecting User */}
            <section className="filter-section">
                <Filter options={options} onSelect={handleSelect} />
            </section>

            {/* Main KPI Overview Section */}
            <section className="main-kpi-section">
                <div className="kpi-overview">
                    <KPIcard userId={selectedUserId} />
                </div>
                <div className="user-tasks-summary">
                    <TasksSummary userId={selectedUserId} userName={userName} />
                </div>
            </section>

            {/* Sprint Data Section */}
            <section className="sprint-data-section">
                <div className="sprint-summary">
                    <SprintHours /> {/* All users' data summary */}
                </div>
                <div className="sprint-chart">
                    <SprintBarChart /> {/* Chart for all users' data */}
                </div>
            </section>

            {/* User-Specific Sprint Data (Only shown if a user is selected) */}
            {selectedUserId && (
                <section className="user-specific-sprint-data">
                    <SprintHoursForUser userId={selectedUserId} userName={userName} />
                </section>
            )}
        </div>
    );
}
