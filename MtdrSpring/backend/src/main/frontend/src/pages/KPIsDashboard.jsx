import React, { useState } from 'react';
import KPIcard from '../components/KPIcard';
import Filter from '../components/Filter';

export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');

    return (
        <div>
            <h1>KPIs Dashboard</h1>
            <p>Welcome to the KPIs Dashboard. Here you can view key performance indicators.</p>
            <Filter options={options} onSelect={setSelectedUserId} />
            <div>
                <KPIcard userId={selectedUserId} />
            </div>
        </div>
    );
}
