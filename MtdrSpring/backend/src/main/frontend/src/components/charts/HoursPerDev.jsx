import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';

const TaskPerDev = ({ isLoading, tasks, users }) => {
    if (isLoading) return <p>Loading task data...</p>;
    if (!tasks || tasks.length === 0) return <p>No data available.</p>;

    // Helper to get developer name
    const getUserName = (userId) => {
        if (!userId) return 'Sin asignar';
        const user = users.find(u => String(u.userId) === String(userId));
        return user ? user.name : 'Sin asignar';
    };

    // Step 1: Group task count by sprint and developer
    const sprintMap = {};

    tasks.forEach(task => {
        if (!task.done) return; // Only include completed tasks

        const sprintKey = task.sprintName ?? `Sprint ${task.sprint_id}`;
        const devName = getUserName(task.user_id);

        if (!sprintMap[sprintKey]) {
            sprintMap[sprintKey] = { sprint: sprintKey };
        }

        sprintMap[sprintKey][devName] = (sprintMap[sprintKey][devName] || 0) + 1;
    });

    const chartData = Object.values(sprintMap);

    // Step 2: Collect all developer names for bar series
    const developers = [...new Set(tasks.map(task => getUserName(task.user_id)))];

    const colors = ['#b9030f', '#9e0004', '#70160e', '#161917', '#e1e3db', '#ff7300', '#3a3632'];

    return (
        <div>
            <h2>Completed Tasks per Developer per Sprint</h2>
            <div style={{ width: '90%', height: 400, margin: 'auto' }}>
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
