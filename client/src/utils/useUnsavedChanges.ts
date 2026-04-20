export const useUnsavedChanges = (isChanged: () => boolean) => {
	const confirmClose = () => {
		if (isChanged()) {
			return window.confirm("保存されていない変更があります。閉じますか？");
		}
		return true;
	};

	return { confirmClose };
};