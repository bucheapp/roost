import React from "react";
import "./Sidebar.css";

import Header from "./Header";
import SearchGroup from "./SearchGroup";
import FixedGroup from "./components/groups/FixedGroup";
import NewGroup from "./components/groups/NewGroup";
import PopularGroup from "./components/groups/PopularGroup";
import AffiliationGroup from "./components/groups/AffiliationGroup";

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
			className="close-btn" onClick={onClose}>
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