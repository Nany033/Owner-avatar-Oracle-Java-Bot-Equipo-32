import { useEffect, useState } from 'react';
import API from '../API';

export default function TasksSummary({ userId, userName }) {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let isInitial = true;

        const fetchTasks = async () => {
            const endpoint = userId
                ? `${API.TODOS}/user/${parseInt(userId, 10)}`
                : `${API.TODOS}`;

            try {
                const res = await fetch(endpoint);
                const data = await res.json();

                const validData = Array.isArray(data) ? data : [];

                // Compare previous and new data
                const oldDataString = JSON.stringify(tasks);
                const newDataString = JSON.stringify(validData);

                if (oldDataString !== newDataString) {
                    setTasks(validData);
                }

                if (isInitial) {
                    setLoading(false);
                    isInitial = false;
                }
            } catch (err) {
                console.error('Error fetching tasks:', err);
                if (isInitial) {
                    setTasks([]);
                    setLoading(false);
                    isInitial = false;
                }
            }
        };

        fetchTasks();
        const intervalId = setInterval(fetchTasks, 5000);

        return () => clearInterval(intervalId);
    }, [userId]);

    return (
        <div >
            {loading ? (
                <p>Loading...</p>
            ) : (
                <div>
                    <h2>Tasks Summary</h2>
                    {userId && <h2>User ID: {userId}</h2>}
                    {userName && <h2>User Name: {userName}</h2>}
                    <p>Total Tasks: {tasks.length}</p>
                    <p>Completed Tasks: {tasks.filter(task => task.completed).length}</p>
                    <p>Pending Tasks: {tasks.filter(task => !task.completed).length}</p>
                    <p>
                        Overdue Tasks: {tasks.filter(task =>
                            new Date(task.dueDate) < new Date() && !task.completed
                        ).length}
                    </p>
                </div>
            )}
        </div>
    );
}
