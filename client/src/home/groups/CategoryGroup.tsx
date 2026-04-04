import React from "react";

const CategoryGroup: React.FC = () => {
	return (
		<div className="sidebar-section">
			<div className="sidebar-title">カテゴリ</div>
			<ul className="category-list">
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
	);
};

export default CategoryGroup;