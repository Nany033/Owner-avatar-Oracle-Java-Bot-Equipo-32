import KPIcard from '../components/KPIcard';
import Filter from '../components/Filter';
import React, { useState, useEffect } from 'react';
import TasksSummary from '../components/TasksSummary';


export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    const [userName, setUserName] = useState('');


    useEffect(() => {
        // Search for the user object in the options array whose userId matches the selectedUserId
        const selectedUser = options.find(option => option.userId === selectedUserId);
        // If found, set the userName state to the name of the selected user this is used to display the name in the KPI card
        setUserName(selectedUser ? selectedUser.name : '');
    }, [selectedUserId, options]); 

    const handleSelect = (userId) => {
        setSelectedUserId(userId);
    };


    return (
        <div>
            <h1>KPIs Dashboard</h1>
            <p>Welcome to the KPIs Dashboard. Here you can view key performance indicators.</p>
            <Filter options={options} onSelect={handleSelect} />
            <div>
                <div>
                                <TasksSummary userId={selectedUserId} userName={userName} />
                </div>
                <table className='kpi-table'>
                    <tbody>
                        <tr>
                            <td>
                                <KPIcard userId={selectedUserId} />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
}
