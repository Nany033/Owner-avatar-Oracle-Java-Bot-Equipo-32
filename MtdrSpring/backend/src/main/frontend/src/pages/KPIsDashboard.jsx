import React from 'react';
import KPIcard from '../components/KPIcard';
import Filter from '../components/Filter';

export default function KPIsDashboard() {
    return (
        <div>
            <h1>KPIs Dashboard</h1>
            <p>Welcome to the KPIs Dashboard. Here you can view key performance indicators.</p>
            <Filter />
            <div>
                <table className='kpi-table'>
                    <tr>
                        <td><KPIcard /></td>
                        <td><KPIcard /></td>
                        <td><KPIcard /></td>
                    </tr>
                </table>
                <KPIcard />
            </div>
        </div>
    );
};

