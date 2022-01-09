LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
ENTITY logic_and IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_and;
ARCHITECTURE arq OF logic_and IS
BEGIN
	O <= A AND B;
END arq;