import React from "react";

type Props = {
	label: string;
	type: string;
	placeholder?: string;
	value: string;
	onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
	required?: boolean;
};

export function FormGroup({
	label,
	type,
	placeholder,
	value,
	onChange,
	required
}: Props) {
	return (
		<div className="form-group">
			<label>{label}</label>
			<input
				type={type}
				placeholder={placeholder}
				value={value}
				onChange={onChange}
				required={required}
			/>
		</div>
	);
}