import React, { useState } from 'react';
import KPIcard from '../components/KPIcard';
import Filter from '../components/Filter';
import { useState, useEffect } from 'react';
import Filter from '../components/ui/Filter';
import TasksSummary from '../components/TasksSummary';
import SprintHours from '../components/tables/SprintHours';
import SprintHoursForUser from '../components/tables/SprintHoursForUser';
import SprintBarChart from '../components/charts/SprintBarChart';

export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');

export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    const [userName, setUserName] = useState('');

    useEffect(() => {
        const selectedUser = options.find(option => option.userId === selectedUserId);
        setUserName(selectedUser ? selectedUser.name : '');
    }, [selectedUserId, options]);

    const handleSelect = (userId) => {
        setSelectedUserId(userId);
    };

    return (
        <div>
            <h1>KPIs Dashboard</h1>
            <p>Welcome to the KPIs Dashboard. Here you can view key performance indicators.</p>
            <Filter options={options} onSelect={setSelectedUserId} />
            <Filter options={options} onSelect={handleSelect} />
            <div >
                {!selectedUserId && (
                    <>
                        <SprintHours />
                        <SprintBarChart />
                    </>
                )}
                {selectedUserId && <SprintHoursForUser userId={selectedUserId} userName={userName} />}
            </div>
            <div>
                <KPIcard userId={selectedUserId} />
                <TasksSummary userId={selectedUserId} userName={userName} />
            </div>
        </div>
    );
}
