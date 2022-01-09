LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY mux IS
	PORT (
		R : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		S : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		sel : IN STD_LOGIC;

		F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END mux;
ARCHITECTURE arq OF mux IS
BEGIN
	F(0) <= (sel AND R(0)) OR (NOT sel AND S(0));
	F(1) <= (sel AND R(1)) OR (NOT sel AND S(1));
	F(2) <= (sel AND R(2)) OR (NOT sel AND S(2));
	F(3) <= (sel AND R(3)) OR (NOT sel AND S(3));
END arq;