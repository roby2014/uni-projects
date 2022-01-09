LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY tlab4 IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC;

		R : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
		CyBw : OUT STD_LOGIC;
		OV : OUT STD_LOGIC
	);
END tlab4;

ARCHITECTURE arq_main OF tlab4 IS

	-- SOMADOR
	COMPONENT somador
		PORT (
			A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			Cin : IN STD_LOGIC;
			S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
			Cout : OUT STD_LOGIC);
	END COMPONENT;

	-- FLAGS
	COMPONENT flags
		PORT (
			A, B, Cout, OP, S : IN STD_LOGIC;
			CyBw, OV : OUT STD_LOGIC);
	END COMPONENT;

	SIGNAL bx : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL cx : STD_LOGIC;
	SIGNAL rx : STD_LOGIC_VECTOR(3 DOWNTO 0);

BEGIN

	u_somador : somador PORT MAP(
		A => A,
		B => bx,
		Cin => OP,
		Cout => cx,
		S => rx
	);
	u_flags : flags PORT MAP(
		A => A(3),
		B => bx(3),
		Cout => cx,
		OP => OP,
		S => rx(3),
		OV => OV,
		CyBw => CyBw
	);
	bx(0) <= B(0) XOR OP;
	bx(1) <= B(1) XOR OP;
	bx(2) <= B(2) XOR OP;
	bx(3) <= B(3) XOR OP;

	R <= rx;

END arq_main;