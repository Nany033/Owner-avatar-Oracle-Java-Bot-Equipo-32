import React, { useState, useEffect } from 'react';
import { PieChart, Pie, Sector, ResponsiveContainer } from 'recharts';
import API from '../API';

const getChartData = (tasks) => {
  const completedTasks = tasks.filter(task => task.completed).length;
  const pendingTasks = tasks.filter(task => !task.completed).length;
  const overdueTasks = tasks.filter(
    task => new Date(task.dueDate) < new Date() && !task.completed
  ).length;

  return [
    { name: 'Completed', value: completedTasks },
    { name: 'Pending', value: pendingTasks },
    { name: 'Overdue', value: overdueTasks },
  ];
};

const renderActiveShape = (props) => {
  const RADIAN = Math.PI / 180;
  const {
    cx, cy, midAngle, innerRadius, outerRadius,
    startAngle, endAngle, fill, payload, percent, value
  } = props;

  const sin = Math.sin(-RADIAN * midAngle);
  const cos = Math.cos(-RADIAN * midAngle);
  const sx = cx + (outerRadius + 10) * cos;
  const sy = cy + (outerRadius + 10) * sin;
  const mx = cx + (outerRadius + 30) * cos;
  const my = cy + (outerRadius + 30) * sin;
  const ex = mx + (cos >= 0 ? 1 : -1) * 22;
  const ey = my;
  const textAnchor = cos >= 0 ? 'start' : 'end';

  return (
    <g>
      <Sector
        cx={cx}
        cy={cy}
        innerRadius={innerRadius}
        outerRadius={outerRadius}
        startAngle={startAngle}
        endAngle={endAngle}
        fill={fill}
      />
      <Sector
        cx={cx}
        cy={cy}
        innerRadius={outerRadius + 6}
        outerRadius={outerRadius + 10}
        startAngle={startAngle}
        endAngle={endAngle}
        fill={fill}
      />
      <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
      <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
      <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey} textAnchor={textAnchor} fill="#333">{`${payload.name}: ${value}`}</text>
      <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey + 20} textAnchor={textAnchor} fill="#999">
        {`(${(percent * 100).toFixed(0)}%)`}
      </text>
    </g>
  );
};

export default function TaskPieChart({ userId }) {
  const [activeIndex, setActiveIndex] = useState(0);
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    if (!userId) return;

    const fetchTasks = async () => {
      try {
        const res = await fetch(`${API.TODOS}/user/${parseInt(userId, 10)}`);
        const data = await res.json();
        setChartData(getChartData(data));  // Directly passing all the tasks
        console.log('Chart Data:', getChartData(data));
      } catch (err) {
        console.error('Error fetching tasks for chart:', err);
        setChartData([]);
      }
    };

    fetchTasks();
  }, [userId]);

  const onPieEnter = (_, index) => setActiveIndex(index);


  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          activeIndex={activeIndex}
          activeShape={renderActiveShape}
          data={chartData}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={80}
          fill="#8884d8"
          dataKey="value"
          onMouseEnter={onPieEnter}
          labelLine={false}
        />
      </PieChart>
    </ResponsiveContainer>
  );
}
