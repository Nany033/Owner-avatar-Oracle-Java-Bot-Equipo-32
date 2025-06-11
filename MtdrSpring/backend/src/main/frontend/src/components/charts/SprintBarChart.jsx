import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';
import { toPng } from 'html-to-image';
import React, { useRef } from 'react';


const SprintBarChart = ({ data, isLoading }) => {
    const chartRef = useRef();
    if (isLoading) {
        return <p>Loading chart data...</p>;
    }

    if (!data || data.length === 0) {
        return <p> </p>;
    }

    const chartData = data.map((item) => ({
        sprint: item.sprintName ?? `Sprint ${item.sprintId}`,
        estimated: item.estimated_hours ?? 0,
        actual: item.totalHours ?? 0,
    }));
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
            <h2>Sprint Hours Overview</h2>
            <div style={{ textAlign: 'right', marginBottom: '10px' }}>
                <button onClick={downloadChart}>Download Chart</button>
            </div>
            <div ref={chartRef} style={{ width: '90%', height: 400, margin: 'auto', background: 'white' }}>
                <ResponsiveContainer>
                    <BarChart data={chartData} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="sprint" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="estimated" fill="#3a3632" name="Estimated Hours" />
                        <Bar dataKey="actual" fill="#c40000" name="Actual Hours" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default SprintBarChart;