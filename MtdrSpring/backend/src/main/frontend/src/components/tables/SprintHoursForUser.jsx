import React, { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart'; // Make sure this path is correct

const SprintHoursForUser = ({ userId, userName }) => {
  const [sprintData, setSprintData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!userId) return;

    setLoading(true);
    axios.get(`${API.KPI}/hours-per-sprint/${userId}`)
      .then(response => {
        setSprintData(response.data);
        console.log('Sprint data for user:', response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching sprint hours for user:', error);
        setLoading(false);
      });
  }, [userId]);

  if (!userId) return <p>Select a user to view their sprint hours.</p>;
  if (loading) return <p>Loading sprint data for {userName}...</p>;

  return (
    <div>
      <h2>Sprint Hours for {userName}</h2>

      {sprintData.length === 0 ? (
        <p>No sprint data available for this user.</p>
      ) : (
        <>
          {/* Table View */}
          <table>
            <thead>
              <tr>
                <th>Sprint Name</th>
                <th>Estimated Hours</th>
                <th>Actual Hours</th>
              </tr>
            </thead>
            <tbody>
              {sprintData.map((sprint, index) => (
                <tr key={index}>
                  <td>{sprint.sprintName ?? `Sprint ${sprint.sprintId}`}</td>
                  <td>{sprint.estimated_hours ?? '–'}</td>
                  <td>{sprint.totalHours ?? '–'}</td>
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
