import React, { useEffect, useState } from 'react';
import axios from 'axios';
import API from '../../API';
import SprintBarChart from '../charts/SprintBarChart'; // Make sure path is correct

const SprintHours = () => {
  const [sprintData, setSprintData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get(`${API.KPI}/hours-per-sprint`)
      .then(response => {
        setSprintData(response.data);
        console.log('Sprint data:', response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching sprint hours:', error);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Loading sprint data...</p>;

  return (
    <div>
      <h2>Sprint Hours (All Users)</h2>

      {/* Table view */}
      <table>
        <thead>
          <tr>
            <th>Sprint</th>
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

      {/* Chart view */}
      <SprintBarChart data={sprintData} />
    </div>
  );
};

export default SprintHours;
