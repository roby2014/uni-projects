-- top entity, connect everything

LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;
ENTITY alu IS
	PORT (
		W : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		Y : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC_VECTOR(2 DOWNTO 0);

		CyBw : OUT STD_LOGIC;
		OV : OUT STD_LOGIC;
		Z : OUT STD_LOGIC;
		P : OUT STD_LOGIC;
		GE : OUT STD_LOGIC;
		F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
	);
END alu;
ARCHITECTURE arq OF alu IS

	COMPONENT w_div2 PORT (
		W : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC;
		A : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT m_main PORT (
		A, B : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC;
		R : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
		CyBw, OV : OUT STD_LOGIC
		);
	END COMPONENT;

	COMPONENT logic_module PORT (
		W, Y : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		OP : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
		S : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT mux PORT (
		R, S : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		sel : IN STD_LOGIC;
		F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
		);
	END COMPONENT;

	COMPONENT flags PORT (
		A, B : IN STD_LOGIC;
		F : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
		CyBw, OV, Z, P, GE : OUT STD_LOGIC
		);
	END COMPONENT;

	SIGNAL sa, sr, sf, sr2 : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL scybw, sov : STD_LOGIC;

BEGIN

	udiv : w_div2 PORT MAP(
		W => W,
		OP => OP(1),
		A => sa
	);

	umodule : m_main PORT MAP(
		A => sa,
		B => Y,
		OP => OP(0),
		R => sr,
		CyBw => scybw,
		OV => sov
	);

	ulogic : logic_module PORT MAP(
		W => W,
		Y => Y,
		OP(0) => OP(0),
		OP(1) => OP(1),
		S => sr2
	);

	umux : mux PORT MAP(
		R => sr,
		S => sr2,
		sel => OP(2),
		F => sf
	);

	uflags : flags PORT MAP(
		A => scybw,
		B => sov,
		F => sf,
		CyBw => CyBw,
		Ov => OV,
		Z => Z,
		P => P,
		GE => GE
	);

	F <= sf;

END arq;