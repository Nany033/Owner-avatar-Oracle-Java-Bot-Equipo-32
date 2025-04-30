import TaskPieChart from "./TaskPieChart";
export default function KPIcard({userId}) {
    return (
        <div className="kpi-card">
            <h2>KPI Card</h2>
            <p>This is a KPI card.</p>
            <TaskPieChart userId={userId} />
        </div>
    );
}
