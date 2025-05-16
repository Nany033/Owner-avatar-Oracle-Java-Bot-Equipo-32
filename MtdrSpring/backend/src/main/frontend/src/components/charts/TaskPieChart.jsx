// TaskPieChart.jsx
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const COLORS = ['#e60000', '#3a3632', '#831111FF']; // Completed, Pending, Overdue

export default function TaskPieChart({ completed, pending, overdue }) {
    const data = [
        { name: 'Completed', value: completed },
        { name: 'Pending', value: pending },
        { name: 'Overdue', value: overdue },
    ];
    console.log('TaskPieChart data:', data);

    return (
        <ResponsiveContainer width="100%" height={300}>
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
