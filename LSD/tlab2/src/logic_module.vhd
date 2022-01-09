LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY logic_module IS
	PORT (
		W : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Y : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);

		S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END logic_module;
ARCHITECTURE arq OF logic_module IS

	COMPONENT logic_and PORT (
		A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT logic_or PORT (
		A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT logic_xor PORT (
		A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT logic_not PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		O : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT logic_mux PORT (
		Mand, Mor, Mxor, Mnot : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
		S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	SIGNAL sa, so, sx, sn : STD_LOGIC_VECTOR(3 DOWNTO 0);

BEGIN

	uand : logic_and PORT MAP(
		A => W,
		B => Y,
		O => sa
	);

	uor : logic_or PORT MAP(
		A => W,
		B => Y,
		O => so
	);

	uxor : logic_xor PORT MAP(
		A => W,
		B => Y,
		O => sx
	);

	unot : logic_not PORT MAP(
		A => W,
		O => sn
	);

	umux : logic_mux PORT MAP(
		Mand => sa,
		Mor => so,
		Mxor => sx,
		Mnot => sn,
		OP => OP,
		S => S
	);

END arq;