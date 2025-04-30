import React, { useEffect, useState } from 'react';
import API from '../API';

export default function TasksSummary({ userId, userName }) {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
    if (!userId) return;

    const userIdInt = parseInt(userId, 10);

    const fetchTasks = () => {
        fetch(`${API.TODOS}/user/${userIdInt}`)
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) {
                    setTasks([...data]);
                } else {
                    setTasks([]);
                }
            })
            .catch(err => {
                console.error('Error fetching tasks:', err);
                setTasks([]);
            });
    };

    fetchTasks(); // primera carga
    const intervalId = setInterval(fetchTasks, 5000); // 🔁 actualiza cada 5 segundos

    return () => clearInterval(intervalId); // limpia el intervalo al desmontar
}, [userId]);


    return (
        <div className="taskSummary-card">
            {loading ? (
                <p>Loading...</p>
            ) : userId ? (
                <div>
                    <h1>Tasks Summary</h1>
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
