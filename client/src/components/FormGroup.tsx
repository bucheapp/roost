import React from "react";
import styles from "../user/page/UserProfileEdit.module.css";

type Props = {
	label: string;
	children: React.ReactNode;
	edited?: boolean;
};

export function FormGroup({ label, children, edited }: Props) {
	return (
		<div className="form-group">
			<label>
				{label} {edited && <span className={styles.edited}>*</span>}
			</label>
			{children}
		</div>
	);
}