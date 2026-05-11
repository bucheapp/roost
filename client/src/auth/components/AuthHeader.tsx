import React from "react";
import { Link } from "react-router-dom";
import styles from "./AuthHeader.module.css";


const AuthHeader: React.FC = () => {
	return (
		<header className={styles.header}>
			<Link to="/" className={styles.home}>
				ホーム
			</Link>
		</header>
	);
};

export default AuthHeader;