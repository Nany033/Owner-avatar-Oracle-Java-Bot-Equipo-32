import { useEffect, useState } from 'react';
import TaskPieChart from './charts/TaskPieChart';

export default function TasksSummary({ userId, userName, tasks, loading}) {

    useEffect(() => {
        
        const intervalId = setInterval(tasks, 5000);

        return () => clearInterval(intervalId);
    }, [userId]);

    const completed = tasks.filter(task => task.done).length;
    const overdue = tasks.filter(task => new Date(task.deadline) < new Date() && !task.done).length;
    const pending = tasks.filter(task => !task.done && new Date(task.deadline) >= new Date()).length;
    console.log('completed:', completed);
    console.log('overdue:', overdue);
    console.log('pending:', pending);


    return (
        <div className='chart'>
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
