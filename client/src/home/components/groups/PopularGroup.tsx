import React from "react";

const PopularGroup: React.FC = () => {
	return (
		<div className="sidebar-section">
			<div className="sidebar-title">人気</div>

			<div className="popular-categories">
				<ul>
					<li>テクノロジー</li>
					<li>科学</li>
					<li>芸術</li>
					<li>スポーツ</li>
					<li>ゲーム</li>
					<li>文学</li>
					<li>教育</li>
					<li>エンターテインメント</li>
				</ul>
			</div>
		</div>
	);
};

export default PopularGroup;