// taskService.js (pure JS, no React here)

import API_LIST from '../API'

export async function deleteItem(id) {
    const res = await fetch(`${API_LIST}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete');
    return true;
}


export async function modifyItem(id, description, done, deadline) {
    const data = { description, done, deadline };
    const res = await fetch(`${API_LIST}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to modify');
    return res.json();
}

export async function addItem(description, deadlineStr) {
    const res = await fetch(API_LIST, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ description }),
    });
    if (!res.ok) throw new Error('Failed to add item');
    const id = res.headers.get('location');
    
    if (deadlineStr?.trim()) {
        const deadlineRes = await fetch(`${API_LIST}/${id}/deadline`, {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: deadlineStr
        });
        if (!deadlineRes.ok) throw new Error('Failed to set deadline');
    }
    
    return id;
}

export async function getItem(id) {
    const res = await fetch(`${API_LIST}/${id}`);
    if (!res.ok) throw new Error('Failed to fetch item');
    return res.json();
}

// export async function getSprint(sprint_id){
//     const res = await fetch(`${API_LIST}/${sprint_id}`);
//     if (!res.ok) throw new Error('Failed to fetch item');
//     return res.json();
// }


// export async function getUser(user_id){
//     const res = await fetch(`${API_LIST}/${user_id}`);
//     if (!res.ok) throw new Error('Failed to fetch item');
//     return res.json();
// }