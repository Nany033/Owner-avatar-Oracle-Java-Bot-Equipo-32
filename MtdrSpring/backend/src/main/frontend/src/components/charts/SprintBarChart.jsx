import React from 'react';
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';

const SprintBarChart = ({ data }) => {
    if (!data || data.length === 0) {
        return <p>No data available for the chart.</p>;
    }

    const chartData = data.map((item) => ({
        sprint: item.sprintName ?? `Sprint ${item.sprintId}`,
        estimated: item.estimated_hours ?? 0,
        actual: item.totalHours ?? 0,
    }));

    return (
        <div style={{ width: '100%', height: 400 }}>
            <h3 style={{ textAlign: 'center' }}>Sprint Hours Overview</h3>
            <ResponsiveContainer>
                <BarChart data={chartData} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="sprint" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="estimated" fill="#8884d8" name="Estimated Hours" />
                    <Bar dataKey="actual" fill="#82ca9d" name="Actual Hours" />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};

export default SprintBarChart;
