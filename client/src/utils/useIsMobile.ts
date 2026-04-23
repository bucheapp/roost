import { useEffect, useState } from "react";

export function useIsMobile() {
	const getMatch = () =>
		typeof window !== "undefined"
			? window.matchMedia("(max-width: 768px)").matches
			: false;

	const [isMobile, setIsMobile] = useState(getMatch);

	useEffect(() => {
		const media = window.matchMedia("(max-width: 768px)");

		const listener = (e: MediaQueryListEvent) => {
			setIsMobile(e.matches);
		};

		setIsMobile(media.matches);
		media.addEventListener("change", listener);

		return () => media.removeEventListener("change", listener);
	}, []);

	return isMobile;
}