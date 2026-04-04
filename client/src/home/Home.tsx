import React, { useState, useEffect } from "react";
import "./Home.css";
import Sidebar from "./Sidebar";

function useIsMobile() {
	const [isMobile, setIsMobile] = useState(
		window.matchMedia("(max-width: 768px)").matches
	);

	useEffect(() => {
		const media = window.matchMedia("(max-width: 768px)");

		const listener = (e: MediaQueryListEvent) => {
			setIsMobile(e.matches);
		};

		media.addEventListener("change", listener);

		return () => {
			media.removeEventListener("change", listener);
		};
	}, []);

	return isMobile;
}

const Home: React.FC = () => {
	const [isOpen, setIsOpen] = useState(false);
    const isMobile = useIsMobile();

	return (
		<div className="layout">
			<Sidebar isOpen={isOpen} isMobile={isMobile} onClose={() => setIsOpen(false)} />
            {(isOpen && isMobile ) && <div className="overlay" onClick={() => setIsOpen(false)} />}

			<div className="main">
				<button className="open-btn" onClick={() => setIsOpen(true)}
                    style={{ display: (isMobile && !isOpen) ? "block" : "none" }}
                    >
					☰
				</button>

				<div className="content">
					中央コンテンツ
				</div>
			</div>
		</div>
	);
};

export default Home;