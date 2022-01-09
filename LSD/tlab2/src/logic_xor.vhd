LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY logic_xor IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_xor;
ARCHITECTURE arq OF logic_xor IS
BEGIN
	O <= A XOR B;
END arq;