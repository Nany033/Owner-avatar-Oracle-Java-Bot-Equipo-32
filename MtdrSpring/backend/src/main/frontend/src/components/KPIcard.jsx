import React, { useEffect, useState } from 'react';
import API from '../API';

export default function KPIcard({ userId, userName }) {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!userId) return;

        setLoading(true);

        // Convert userId to an integer
        const userIdInt = parseInt(userId, 10);

        // Fetch tasks for the user
        fetch(`${API.TODOS}/user/${userIdInt}`)
            .then(res => res.json())
            .then(data => {
                const tasksByUser = data.filter(task => task.user_id === userIdInt);
                setTasks(tasksByUser);
                console.log('Tasks for user:', tasksByUser);
            })
            .catch(err => console.error('Error fetching tasks:', err))
            .finally(() => setLoading(false)); 
    }, [userId]);

    return (
        <div className="kpi-card">
            {loading ? (
                <p>Loading...</p>
            ) : userId ? (
                <div className="kpi-chart">
                    <h2>User ID: {userId}</h2>
                    <h2>User Name: {userName}</h2>
                    <p>Total Tasks: {tasks.length}</p>
                    <p>Completed Tasks: {tasks.filter(task => task.completed).length}</p>
                    <p>Pending Tasks: {tasks.filter(task => !task.completed).length}</p>
                    <p>
                        Overdue Tasks: {tasks.filter(task => new Date(task.dueDate) < new Date() && !task.completed).length}
                    </p>
                </div>
            ) : (
                <p>Please select a user to see KPIs.</p>
            )}
        </div>
    );
}
