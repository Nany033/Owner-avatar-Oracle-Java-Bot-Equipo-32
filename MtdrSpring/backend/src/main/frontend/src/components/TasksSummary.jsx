import { useEffect, useState } from 'react';
import TaskPieChart from './charts/TaskPieChart';
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

    const completed = tasks.filter(task => task.done).length;
    const overdue = tasks.filter(task => new Date(task.deadline) < new Date() && !task.done).length;
    const pending = tasks.filter(task => !task.done && new Date(task.deadline) >= new Date()).length;
    console.log('completed:', completed);
    console.log('overdue:', overdue);
    console.log('pending:', pending);


    return (
        <div >
            {loading ? (
                <p>Loading...</p>
            ) : (
                <div>
                    {!userId && <h2>Tasks Summary</h2>}
                    {userId && <h2>Tasks Summary for {userName} (<span>{userId}</span>)</h2>}
                    <p>Total Tasks: {tasks.length}</p>
                    <ul>
                        <li>Completed Tasks: {tasks.filter(task => task.done).length}</li>
                        <li>Pending Tasks: {tasks.filter(task => !task.done && new Date(task.deadline) >= new Date()).length}</li>
                        <li>
                            Overdue Tasks: {tasks.filter(task =>
                                new Date(task.deadline) < new Date() && !task.done
                            ).length}
                        </li>
                    </ul>
                    <TaskPieChart
                        completed={completed}
                        pending={pending}
                        overdue={overdue}
                    />
                </div>
            )}
        </div>
    );
}
