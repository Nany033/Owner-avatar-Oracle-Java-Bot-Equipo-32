import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';
import { toPng } from 'html-to-image';
import React, { useRef } from 'react';

const TaskPerDev = ({ isLoading, tasks, users }) => {
    const chartRef = useRef();

    if (isLoading) return <p>Loading task data...</p>;
    if (!tasks || tasks.length === 0) return <p>No data available.</p>;

    const getUserName = (userId) => {
        if (!userId) return 'Sin asignar';
        const user = users.find(u => String(u.userId) === String(userId));
        return user ? user.name : 'Sin asignar';
    };

    const sprintMap = {};

    tasks.forEach(task => {
        if (!task.done) return;
        const sprintKey = task.sprintName ?? `Sprint ${task.sprint_id}`;
        const devName = getUserName(task.user_id);
        if (!sprintMap[sprintKey]) {
            sprintMap[sprintKey] = { sprint: sprintKey };
        }
        sprintMap[sprintKey][devName] = (sprintMap[sprintKey][devName] || 0) + 1;
    });

    const chartData = Object.values(sprintMap);
    const developers = [...new Set(tasks.map(task => getUserName(task.user_id)))];
    const colors = [
        // Reds (Oracle red & tonal accents)
        '#AC4D32FF',  // Warm coral red
        '#B00020', // Deep crimson red

        // Greens (Oracle-style dark greens / teals)
        '#004129FF', // Deep teal green
        '#038065FF', // Soft accent green

        // Blues (Oracle blue & tonal accents)
        '#0D365DFF', // Deep blue
        '#0B4880FF', // Bright blue
    ];


    const downloadChart = () => {
        if (!chartRef.current) return;
        toPng(chartRef.current)
            .then((dataUrl) => {
                const link = document.createElement('a');
                link.download = 'tasks_chart.png';
                link.href = dataUrl;
                link.click();
            })
            .catch((error) => {
                console.error('Error generating image:', error);
            });
    };

    return (
        <div>
            <h2>Completed Tasks per Developer per Sprint</h2>

            <div style={{ textAlign: 'right', marginBottom: '10px' }}>
                <button onClick={downloadChart}>Download Chart</button>
            </div>

            <div ref={chartRef} style={{ width: '90%', height: 400, margin: 'auto', background: 'white' }}>
                <ResponsiveContainer>
                    <BarChart
                        data={chartData}
                        margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
                    >
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="sprint" />
                        <YAxis allowDecimals={false} />
                        <Tooltip />
                        <Legend />
                        {developers.map((dev, index) => (
                            <Bar
                                key={dev}
                                dataKey={dev}
                                fill={colors[index % colors.length]}
                                name={dev}
                            />
                        ))}
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default TaskPerDev;
