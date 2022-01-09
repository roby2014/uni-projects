-- testbench

LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY TLAB4_tb IS
END TLAB4_tb;

ARCHITECTURE teste OF TLAB4_tb IS
	COMPONENT ALU
		PORT (
			W, Y : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			OP : IN STD_LOGIC_VECTOR(2 DOWNTO 0);
			F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
			CyBw, OV, Z, P, GE : OUT STD_LOGIC
		);
	END COMPONENT;

	SIGNAL W, Y : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL OP : STD_LOGIC_VECTOR(2 DOWNTO 0);
	SIGNAL F : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL CyBw, OV, Z, P, GE : STD_LOGIC;

BEGIN

	U0 : ALU PORT MAP(
		W => W,
		Y => Y,
		OP => OP,
		F => F,
		CyBw => CyBw,
		OV => OV,
		Z => Z,
		P => P,
		GE => GE
	);

	PROCESS

		-- dados da tabela pdf enunciado
	BEGIN
		W <= "1010";
		Y <= "0101";
		OP <= "100";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1101";
		OP <= "100";
		WAIT FOR 10 ns;

		W <= "0110";
		Y <= "0101";
		OP <= "100";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1010";
		OP <= "100";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "0101";
		OP <= "101";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1101";
		OP <= "101";
		WAIT FOR 10 ns;

		W <= "0110";
		Y <= "0101";
		OP <= "101";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1010";
		OP <= "101";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "0101";
		OP <= "110";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1101";
		OP <= "110";
		WAIT FOR 10 ns;

		W <= "0110";
		Y <= "0101";
		OP <= "110";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1010";
		OP <= "110";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "0101";
		OP <= "111";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1101";
		OP <= "111";
		WAIT FOR 10 ns;

		W <= "0110";
		Y <= "0101";
		OP <= "111";
		WAIT FOR 10 ns;

		W <= "1010";
		Y <= "1010";
		OP <= "111";
		WAIT FOR 10 ns;

		W <= "1011";
		Y <= "1101";
		OP <= "000";
		WAIT FOR 10 ns;

		W <= "1011";
		Y <= "1101";
		OP <= "001";
		WAIT FOR 10 ns;

		W <= "1011";
		Y <= "1101";
		OP <= "010";
		WAIT FOR 10 ns;

		W <= "1011";
		Y <= "1101";
		OP <= "011";
		WAIT;

	END PROCESS;

END teste;