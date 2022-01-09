LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY flags IS
	PORT (
		A : IN STD_LOGIC;
		B : IN STD_LOGIC;
		Cout : IN STD_LOGIC;

		OP : IN STD_LOGIC;
		S : IN STD_LOGIC;

		CyBw : OUT STD_LOGIC;
		OV : OUT STD_LOGIC
	);
END flags;

ARCHITECTURE arq_flags OF flags IS
BEGIN
	CyBw <= Cout XOR OP;
	OV <= (NOT(A) AND NOT(B) AND S) OR (A AND B AND NOT(S));
END arq_flags;