import KPIcard from '../components/KPIcard';
import Filter from '../components/Filter';
import React, { useState } from 'react';

export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    return (
        <div>
            <h1>KPIs Dashboard</h1>
            <p>Welcome to the KPIs Dashboard. Here you can view key performance indicators.</p>
            <Filter options={options} onSelect={setSelectedUserId} />
            <div>
                <table className='kpi-table'>
                    <tbody>
                        <tr>
                            <td><KPIcard userId={selectedUserId} /></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
};

