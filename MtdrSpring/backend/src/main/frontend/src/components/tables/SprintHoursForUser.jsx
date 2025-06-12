import React, { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart'; // Make sure this path is correct
import TaskCompletion from '../charts/TasksCompleted';

const SprintHoursForUser = ({ userId, userName, tasks }) => {
  const [sprintData, setSprintData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!userId) return;

    setLoading(true);

    axios.get(`${API.KPI}/hours-per-sprint/${userId}`, {
      withCredentials: true
    })
      .then(response => {
        setSprintData(response.data);
        console.log('Sprint data for user:', response.data);
        setLoading(false);
      })
      .catch(error => {
        setLoading(false);
        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            console.warn('Session expired. Redirecting to login...');
            window.location.href = '/'; // or use `navigate('/')`
            return;
          }
        }
        console.error('Error fetching sprint hours for user:', error);
      });
  }, [userId]);


  if (!userId) return <p>Select a user to view their sprint hours.</p>;
  if (loading) return <p>Loading sprint data for {userName}...</p>;

  return (
    <div>
      <h2>Sprint Hours for {userName} <span>({userId})</span></h2>

      {sprintData.length === 0 ? (
        <p>No sprint data available for this user.</p>
      ) : (
        <>
          {/* Table View */}
          <table className='table'>
            <thead>
              <tr>
                <th>Sprint Name</th>
                <th>Estimated Hours</th>
                <th>Actual Hours</th>
                <th>Tasks Completed</th>
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

          {/* Chart View */}
          <SprintBarChart data={sprintData} />
        </>
      )}
    </div>
  );
};

export default SprintHoursForUser;
