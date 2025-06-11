import TaskPieChart from "../charts/TaskPieChart";
//export default function KPIcard({userId}) {
import React, { useEffect, useState } from 'react';
import PieChart from './PieChart';
import API from '../API';

export default function KPIcard({ userId }) {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!userId) return;

        setLoading(true);

        fetch(`${API.TODOS}?assignedTo=${userId}`, {
            credentials: 'include'
        })
            .then(res => res.json())
            .then(data => {
                setTasks(data);
                console.log(data);
            })
            .catch(err => console.error('Error fetching tasks:', err))
            .finally(() => setLoading(false)); // ⬅️ Finaliza el loading
    }, [userId]);

    return (
        <div className="kpi-card">
            <h2>KPI Card</h2>
            <p>This is a KPI card.</p>
            <TaskPieChart userId={userId} />
            {loading ? (
                <p>Loading...</p>
            ) : userId ? (
                // <PieChart tasks={tasks} />
                <div className="kpi-chart">
                    <h2>User ID: {userId}</h2>
                    <p>PIE CHART </p>
                    {/* <PieChart tasks={tasks} /> */}
                    <p>Total Tasks: {tasks.length}</p>
                    <p>Completed Tasks: {tasks.filter(task => task.completed).length}</p>
                    <p>Pending Tasks: {tasks.filter(task => !task.completed).length}</p>
                    <p>Overdue Tasks: {tasks.filter(task => new Date(task.dueDate) < new Date() && !task.completed).length}</p>
                </div> 
            ) : (
                <p>Please select a user to see KPIs.</p>
            )}  
        </div>
    );
}

