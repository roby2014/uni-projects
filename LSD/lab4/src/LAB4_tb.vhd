LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY LAB4_tb IS
END LAB4_tb;

ARCHITECTURE teste OF LAB4_tb IS
	COMPONENT tlab4
		PORT (
			A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			OP : IN STD_LOGIC;
			R : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
			CyBw, OV : OUT STD_LOGIC
		);
	END COMPONENT;

	SIGNAL A, B : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL OP : STD_LOGIC;
	SIGNAL R : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL CyBw, OV : STD_LOGIC;

BEGIN

	U0 : tlab4 PORT MAP(
		A => A,
		B => B,
		OP => OP,
		R => R,
		CyBw => CyBw,
		OV => OV
	);

	PROCESS
	BEGIN
		A <= "0101";
		B <= "1101";
		OP <= '0';
		WAIT FOR 10 ns;

		A <= "0101";
		B <= "1101";
		OP <= '1';
		WAIT FOR 10 ns;

		WAIT;

	END PROCESS;

END teste;