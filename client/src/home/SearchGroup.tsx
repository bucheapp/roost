import React from "react";

const SearchGroup: React.FC = () => {
	return (
		<div className="sidebar-section">
			<input
				type="text"
				className="search-input"
				placeholder="検索..."
			/>
		</div>
	);
};

export default SearchGroup;