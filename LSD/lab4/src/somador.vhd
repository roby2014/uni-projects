LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY somador IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Cin : IN STD_LOGIC;

		S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
		Cout : OUT STD_LOGIC
	);
END somador;

ARCHITECTURE arq_somador OF somador IS

	COMPONENT full_adder
		PORT (
			A : IN STD_LOGIC;
			B : IN STD_LOGIC;
			Cin : IN STD_LOGIC;

			S : OUT STD_LOGIC;
			Cout : OUT STD_LOGIC
		);
	END COMPONENT;

	SIGNAL c1, c2, c3 : STD_LOGIC;

BEGIN

	-- fa 0
	u_full_adder0 : full_adder PORT MAP(
		A => A(0),
		B => B(0),
		Cin => Cin,
		S => S(0),
		Cout => c1
	);

	-- fa 1
	u_full_adder1 : full_adder PORT MAP(
		A => A(1),
		B => B(1),
		Cin => c1,
		S => S(1),
		Cout => c2
	);

	-- fa 2
	u_full_adder2 : full_adder PORT MAP(
		A => A(2),
		B => B(2),
		Cin => c2,
		S => S(2),
		Cout => c3
	);

	-- fa 3
	u_full_adder3 : full_adder PORT MAP(
		A => A(3),
		B => B(3),
		Cin => c3,
		S => S(3),
		Cout => Cout
	);
END arq_somador;