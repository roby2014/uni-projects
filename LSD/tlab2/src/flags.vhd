LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY flags IS
	PORT (
		A : IN STD_LOGIC; -- = cybw from module_main
		B : IN STD_LOGIC; -- = ov from module_main
		F : IN STD_LOGIC_VECTOR(3 DOWNTO 0);

		CyBw : OUT STD_LOGIC;
		OV : OUT STD_LOGIC;
		Z : OUT STD_LOGIC;
		P : OUT STD_LOGIC;
		GE : OUT STD_LOGIC
	);
END flags;
ARCHITECTURE arq OF flags IS
BEGIN
	CyBw <= A;
	OV <= B;
	Z <= (NOT(F(3) OR F(2) OR F(1) OR F(0)));
	P <= (F(0) XOR F(1) XOR F(2) XOR F(3));
	GE <= B XNOR F(3);
END arq;