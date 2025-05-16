import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';

const TaskCompletion = ({ isLoading, tasks }) => {
    if (isLoading) {
        return <p>Loading task data...</p>;
    }

    if (!tasks || tasks.length === 0) {
        return <p>No data available.</p>;
    }

    console.log('TaskCompletion tasks:', tasks);
    // Crear los datos para el gráfico
    const chartDataMap = tasks.reduce((acc, task) => {
        const sprintKey = task.sprintName ?? `Sprint ${task.sprint_id}`;
        if (!acc[sprintKey]) {
            acc[sprintKey] = {
                sprint: sprintKey,
                completedTasks: 0,
            };
        }
        if (task.done) {
            acc[sprintKey].completedTasks += 1;
        }
        return acc;
    }, {});

    const chartData = Object.values(chartDataMap);


    console.log('Chart data:', chartData);

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
