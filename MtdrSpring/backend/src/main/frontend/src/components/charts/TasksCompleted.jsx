import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';

const TaskCompletion = ({ data, isLoading, tasks }) => {
    if (isLoading) {
        return <p>Loading task data...</p>;
    }

    if (!data || data.length === 0) {
        return <p>No data available.</p>;
    }

    // Transforming the input data
    const chartData = data.map((item) => ({
        sprint: item.sprintName ?? `Sprint ${item.sprintId}`,
        completedTasks: tasks.filter(task => task.sprintId === item.sprintId).length ?? 0,
    }));

    return (
        <div>
            <h2>Tasks Completed Per Sprint</h2>
            <div style={{ width: '90%', height: 400, margin: 'auto auto' }}>
                <ResponsiveContainer>
                    <BarChart
                        data={chartData}
                        margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
                    >
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="sprint" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="completedTasks" fill="#c40000" name="Completed Tasks" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default TaskCompletion;
