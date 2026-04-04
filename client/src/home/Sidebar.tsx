import React from "react";
import "./Sidebar.css";

import Header from "./Header";
import SearchGroup from "./SearchGroup";
import FixedGroup from "./groups/FixedGroup";
import NewGroup from "./groups/NewGroup";
import PopularGroup from "./groups/PopularGroup";

const Sidebar: React.FC = () => {
	return (
		<div className="sidebar">
			<Header />
			<SearchGroup />
			<FixedGroup />
			<NewGroup />
			<PopularGroup />
		</div>
	);
};

export default Sidebar;