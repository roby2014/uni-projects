LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY logic_mux IS
	PORT (
		Mand : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Mor : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Mxor : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Mnot : IN STD_LOGIC_VECTOR(3 DOWNTO 0);

		OP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);

		S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_mux;
ARCHITECTURE arq OF logic_mux IS
BEGIN
	S(0) <= (OP(0) AND Mand(0)) OR (NOT OP(0) AND Mor(0)) OR (OP(1) AND Mxor(0)) OR (NOT OP(1) AND Mnot(0));
	S(1) <= (OP(0) AND Mand(1)) OR (NOT OP(0) AND Mor(1)) OR (OP(1) AND Mxor(1)) OR (NOT OP(1) AND Mnot(1));
	S(2) <= (OP(0) AND Mand(2)) OR (NOT OP(0) AND Mor(2)) OR (OP(1) AND Mxor(2)) OR (NOT OP(1) AND Mnot(2));
	S(3) <= (OP(0) AND Mand(3)) OR (NOT OP(0) AND Mor(3)) OR (OP(1) AND Mxor(3)) OR (NOT OP(1) AND Mnot(3));
END arq;