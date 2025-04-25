import React, { useEffect, useState } from 'react';
import PieChart from './PieChart';
import API from '../API';

export default function KPIcard({ userId }) {
    const [tasks, setTasks] = useState([]);
    const [stats, setStats] = useState(null); // ⬅️ Add this line
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!userId) return;

        setLoading(true);

        const fetchTasks = fetch(`${API.TODOS}?assignedTo=${userId}`).then(res => res.json());
        const fetchStats = fetch(`${API.TODOS}/completion-stats`).then(res => res.json());

        Promise.all([fetchTasks, fetchStats])
            .then(([taskData, statsData]) => {
                setTasks(taskData);
                setStats(statsData);
            })
            .catch(err => console.error('Error fetching data:', err))
            .finally(() => setLoading(false));
    }, [userId]);

    return (
        <div className="kpi-card">
            {loading ? (
                <p>Loading...</p>
            ) : userId ? (
                <div className="kpi-chart">
                    <h2>User: {userId}</h2>
                    {/* <PieChart tasks={tasks} /> */}

                    {stats && stats.completedOnTime !== undefined && (
                        <div className="completion-stats">
                            <p>Completed on Time: {stats.completedOnTime.toFixed(2)}%</p>
                            <p>Completed Late: {stats.completedLate.toFixed(2)}%</p>
                            <p>Total tasks: {stats.totalTasks}</p>
                        </div>
                    )}
                    {stats && stats.message && <p>{stats.message}</p>}
                </div>
            ) : (
                <p>Please select a user to see KPIs.</p>
            )}
        </div>
    );
}
