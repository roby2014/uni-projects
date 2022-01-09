LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
ENTITY logic_or IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_or;
ARCHITECTURE arq OF logic_or IS
BEGIN
	O <= A OR B;
END arq;