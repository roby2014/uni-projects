LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY w_div2 IS
	PORT (
		W : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC;

		A : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END w_div2;
ARCHITECTURE arq_div OF w_div2 IS
BEGIN
	A(0) <= (W(0) AND NOT(OP)) OR (W(1) AND OP);
	A(1) <= (W(1) AND NOT(OP)) OR (W(2) AND OP);
	A(2) <= (W(2) AND NOT(OP)) OR (W(3) AND OP);
	A(3) <= W(3);
END arq_div;