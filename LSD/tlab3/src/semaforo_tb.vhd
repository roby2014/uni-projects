LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY tlab3_tb IS
END tlab3_tb;

ARCHITECTURE behavior OF tlab3_tb IS

	-- Component Declaration for the Unit Under Test (UUT)

	COMPONENT tlab3 IS
		PORT (
			clk : IN STD_LOGIC;
			reset : IN STD_LOGIC;
			B : IN STD_LOGIC;
			Tempo : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			E, A, V, EP, VP : OUT STD_LOGIC;
			HEX0 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0);
			Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;
	--Inputs
	SIGNAL clk, reset, B : STD_LOGIC := '0';
	SIGNAL Tempo : STD_LOGIC_VECTOR(3 DOWNTO 0) := "0000";

	--Outputs
	SIGNAL Q : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL E, A, V, EP, VP : STD_LOGIC;
	SIGNAL HEX0 : STD_LOGIC_VECTOR(7 DOWNTO 0);

	-- Clock period definitions
	CONSTANT clk_period : TIME := 10 ns;

BEGIN

	-- Instantiate the Unit Under Test (UUT)
	uut : tlab3 PORT MAP(
		clk => clk,
		reset => reset,
		B => B,
		Tempo => Tempo,
		Q => Q,
		E => E,
		A => A,
		V => V,
		EP => EP,
		VP => VP,
		HEX0 => HEX0
	);

	clk <= NOT clk AFTER 10 ns;

	-- Stimulus process
	stim_proc : PROCESS
	BEGIN

		WAIT FOR 20 ns;
		reset <= '1';
		Tempo <= "0100";
		B <= '0';

		WAIT FOR 20 ns;
		reset <= '0';
		B <= '0';

		WAIT FOR 100 ns;

		reset <= '0';
		B <= '1';

		WAIT FOR 100 ns;
		B <= '0';

		WAIT;

	END PROCESS;

END;