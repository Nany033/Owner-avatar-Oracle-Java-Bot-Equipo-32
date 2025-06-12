import { useState, useEffect } from 'react';
import Filter from '../components/ui/Filter';
import TasksSummary from '../components/TasksSummary';
import SprintHours from '../components/tables/SprintHours';
import SprintHoursForUser from '../components/tables/SprintHoursForUser';
import SprintBarChart from '../components/charts/SprintBarChart';
import API from '../API';

export default function KPIsDashboard({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userName, setUserName] = useState('');

    useEffect(() => {
        const selectedUser = options.find(option => option.userId == selectedUserId);
        setUserName(selectedUser ? selectedUser.name : '');

        let isInitial = true;

        const fetchTasks = async () => {
            const endpoint = selectedUserId
                ? `${API.TODOS}/user/${parseInt(selectedUserId, 10)}`
                : `${API.TODOS}`;

            try {
                const res = await fetch(endpoint, {
                    credentials: 'include'
                });

                if (res.status === 401 || res.status === 403) {
                    throw new Error('Session expired');
                }

                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text || 'Failed to fetch tasks');
                }

                const data = await res.json();
                const validData = Array.isArray(data) ? data : [];

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
                if (err.message === 'Session expired') {
                    window.location.href = '/'; // or use `navigate('/')`
                    return;
                }

                if (isInitial) {
                    setTasks([]);
                    setLoading(false);
                    isInitial = false;
                }
            }
        };

        fetchTasks();
    }, [selectedUserId, options]);


    const handleSelect = (userId) => {
        setSelectedUserId(userId);
    };

    return (
        <div className="page">
            <h1>KPIs Dashboard</h1>
            <Filter options={options} onSelect={handleSelect} />
            <div>
                {!selectedUserId && (
                    <>
                        <SprintHours tasks={tasks} />
                        <SprintBarChart />
                    </>
                )}
                {selectedUserId && <SprintHoursForUser userId={selectedUserId} userName={userName} tasks={tasks} />}
            </div>
            <div>
                <TasksSummary userId={selectedUserId} userName={userName} tasks={tasks} loading={loading} />
            </div>
        </div>
    );
}
