LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY full_adder IS
	PORT (
		A : IN STD_LOGIC;
		B : IN STD_LOGIC;
		Cin : IN STD_LOGIC;

		S : OUT STD_LOGIC;
		Cout : OUT STD_LOGIC
	);
END full_adder;

ARCHITECTURE arq_full_adder OF full_adder IS
BEGIN
	S <= A XOR B XOR Cin;
	Cout <= (A AND B) OR (Cin AND (A XOR B));
END arq_full_adder;