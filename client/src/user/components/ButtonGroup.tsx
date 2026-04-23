import styles from "../page/UserProfileEdit.module.css"

type Props = {
	onSubmit: () => void;
	onClose: () => void;
	isDisabled?: boolean;
	submitLabel?: string;
	closeLabel?: string;
};

export function ButtonGroup({
	onSubmit,
	onClose,
	isDisabled = false,
	submitLabel = "保存",
	closeLabel = "閉じる",
}: Props) {
	return (
		<div className={styles.button_group}>
			<button className={styles.save_btn} onClick={onSubmit} disabled={isDisabled}>
				{submitLabel}
			</button>
			<button className={styles.close_btn} onClick={onClose}>
				{closeLabel}
			</button>
		</div>
	);
}