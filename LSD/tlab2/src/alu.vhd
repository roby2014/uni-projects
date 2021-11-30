-- top entity, connect everything

library ieee;
use ieee.STD_LOGIC_1164.all;


entity alu is 
	port(
		W: in std_logic_vector(3 downto 0);
		Y: in std_logic_vector(3 downto 0);
		OP: in std_logic_vector(2 downto 0);
		
		CyBw: out std_logic;
		OV: out std_logic;
		Z: out std_logic;
		P: out std_logic;
		GE: out std_logic;
		F: out std_logic_vector(3 downto 0)
	);
end alu;


architecture arq of alu is

component w_div2 port(
			W: in std_logic_vector(3 downto 0);
			OP: in std_logic;
			A: out std_logic_vector(3 downto 0)
		);
end component;

component m_main port(
		A, B: in std_logic_vector(3 downto 0);
		OP: in std_logic;
		R: out std_logic_vector(3 downto 0);
		CyBw, OV: out std_logic
	);
end component;

component logic_module port(
		W, Y: in std_logic_vector(3 downto 0);
		OP: in std_logic_vector(1 downto 0);
		S: out std_logic_vector(3 downto 0)
	);
end component;

component mux port(
		R, S: in std_logic_vector(3 downto 0);
		sel: in std_logic;
		F: out std_logic_vector(3 downto 0)
	);
end component;

component flags port(
		A, B: in std_logic;
		F: in std_logic_vector(3 downto 0);
		CyBw, OV, Z, P, GE: out std_logic
	);
end component;

signal sa, sr, sf, sr2: std_logic_vector(3 downto 0);
signal scybw, sov: std_logic;

begin

udiv: w_div2 port map (
	W => W,
	OP => OP(1),
	A => sa
);
	
umodule: m_main port map(
	A => sa,
	B => Y,
	OP => OP(0),
	R => sr,
	CyBw => scybw,
	OV => sov
);

ulogic: logic_module port map(
	W => W,
	Y =>  Y,
	OP(0) => OP(0),
	OP(1) => OP(1),
	S => sr2
);

umux: mux port map(
	R => sr,
	S => sr2,
	sel => OP(2),
	F => sf
);

uflags: flags port map(
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

end arq;
