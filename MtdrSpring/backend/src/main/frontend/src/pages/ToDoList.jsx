import { useState, useEffect} from 'react';
import ToDoItems from '../components/ToDoItems';
import Filter from '../components/ui/Filter';

export default function ToDoList({ options }) {
    const [selectedUserId, setSelectedUserId] = useState('');
    const [userName, setUserName] = useState('');

    // Update user name when selected user changes
    useEffect(() => {
        if (selectedUserId) {
            const selectedUser = options.find(option => option.userId === selectedUserId);
            setUserName(selectedUser ? selectedUser.name : '');
        }
    }, [selectedUserId, options]);


    const handleSelect = (userId) => {
        setSelectedUserId(userId);
    };

    return (
        <div className='todo-list'>
            <h1>To-Do List SEPARANDO PIPELINES</h1>
            <Filter options={options} onSelect={handleSelect} />
            <ToDoItems userId={selectedUserId} users={options} />
        </div>
    );
}
