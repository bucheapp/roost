import React from "react";
import "./Home.css";
import Sidebar from "./Sidebar";

const Home: React.FC = () => {
	return (
		<div className="layout">
			<Sidebar />
			<div className="main">

				<div className="content">
					中央コンテンツ
				</div>
			</div>
		</div>
	);
};


export default Home;