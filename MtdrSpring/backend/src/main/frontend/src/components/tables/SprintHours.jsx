import { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart';
import TaskCompletion from '../charts/TasksCompleted';
import HoursPerDev from '../charts/HoursPerDev';

const SprintHours = ({ tasks }) => {
  const [sprintData, setSprintData] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [sprintRes, usersRes] = await Promise.all([
          axios.get(`${API.KPI}/hours-per-sprint`, { withCredentials: true }),
          axios.get(API.USERS, { withCredentials: true })
        ]);

        setSprintData(sprintRes.data);
        setUsers(usersRes.data);
        setLoading(false);
      } catch (error) {
        setLoading(false);

        // Check for unauthorized session
        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            console.warn('Session expired. Redirecting to login...');
            window.location.href = '/'; // or use navigate('/')
            return;
          }
        }

        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, []);




  if (loading) return <p>Loading sprint data...</p>;
  console.log(users)

  return (
    <div>
      <h2>Sprint Hours (All Users)</h2>

      {/* Table view */}
      <table className="table">
        <thead>
          <tr>
            <th>Sprint</th>
            <th>Estimated Hours</th>
            <th>Actual Hours</th>
            <th>Number of Tasks Completed</th>
          </tr>
        </thead>
        <tbody>
          {sprintData.map((sprint, index) => (
            <tr key={index}>
              <td>{sprint.sprintName ?? `Sprint ${sprint.sprintId}`}</td>
              <td>{sprint.estimated_hours ?? '–'}</td>
              <td>{sprint.totalHours ?? '–'}</td>
              <td>{tasks.filter(task => task.sprint_id === sprint.sprintId).length}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Chart views */}
      <SprintBarChart data={sprintData} />
      <TaskCompletion data={sprintData} tasks={tasks} isLoading={loading} />
      <HoursPerDev tasks={tasks} users={users} isLoading={loading} />
    </div>
  );
};

export default SprintHours;
