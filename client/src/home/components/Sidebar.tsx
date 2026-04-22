import React from "react";
import "./Sidebar.css";

import Header from "./SidebarHeader";
import SearchGroup from "./SearchGroup";
import FixedGroup from "./groups/FixedGroup";
import NewGroup from "./groups/NewGroup";
import PopularGroup from "./groups/PopularGroup";
import AffiliationGroup from "./groups/AffiliationGroup";

type Props = {
	isOpen: boolean;
	isMobile: boolean;
	onClose: () => void;
};

const Sidebar: React.FC<Props> = ({ isOpen, isMobile, onClose }) => {
	return (
		<div className={`sidebar ${isOpen ? "open" : ""}`}>
			<button 
			style={{ display: (isMobile && isOpen) ? "block" : "none" }}
			className="close-sidebar-btn" onClick={onClose}>
				×
			</button>

			<Header />
			<SearchGroup />
			<FixedGroup />
			<NewGroup />
			<PopularGroup />
			<AffiliationGroup />
		</div>
	);
};

export default Sidebar;