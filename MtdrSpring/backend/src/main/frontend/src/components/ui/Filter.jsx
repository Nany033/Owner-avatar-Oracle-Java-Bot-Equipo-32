import React, { useState } from 'react';

const Filter = ({ options, onSelect }) => {
    const [selectedOption, setSelectedOption] = useState('');

    const handleChange = (event) => {
        const value = event.target.value;
        setSelectedOption(value);
        onSelect(value); // Notify parent
    };

    return (
        <div className='Filter'>
            <select
                id="filter-dropdown"
                value={selectedOption}
                onChange={handleChange}
            >
                <option value="">Team Overview</option>
                {Array.isArray(options) && options.map(option => (
                    <option key={option.userId} value={option.userId}>
                        {option.name}
                    </option>
                ))}
            </select>
            {/* {selectedOption && <p>You selected: {options.find(o => o.userId === selectedOption)?.name}</p>} */}
            {/* <p>Selected User ID: {selectedOption}</p> */}
        </div>
    );
};

export default Filter;

