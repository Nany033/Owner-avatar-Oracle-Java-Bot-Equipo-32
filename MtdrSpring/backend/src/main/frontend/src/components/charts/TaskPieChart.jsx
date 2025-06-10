// TaskPieChart.jsx
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { toPng } from 'html-to-image';
import React, { useRef } from 'react';

const COLORS = ['#e60000', '#3a3632', '#831111FF']; // Completed, Pending, Overdue

export default function TaskPieChart({ completed, pending, overdue }) {
    const chartRef = useRef();
    const data = [
        { name: 'Completed', value: completed },
        { name: 'Pending', value: pending },
        { name: 'Overdue', value: overdue },
    ];
    console.log('TaskPieChart data:', data);
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
        <ResponsiveContainer ref={chartRef} width="100%" height={300} style={{ width: '90%', height: 400, margin: 'auto', background: 'white' }}>
            <div style={{ textAlign: 'right', marginBottom: '10px' }}>
                <button onClick={downloadChart}>Download Chart</button>
            </div>
            <PieChart>
                <Pie
                    data={data}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={100}
                    fill="#8884d8"
                    label
                >
                    {data.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                </Pie>
                <Tooltip />
                <Legend />
            </PieChart>
        </ResponsiveContainer>
    );
}
