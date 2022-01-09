LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
ENTITY logic_not IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_not;
ARCHITECTURE arq OF logic_not IS
BEGIN
	O <= NOT(A);
END arq;