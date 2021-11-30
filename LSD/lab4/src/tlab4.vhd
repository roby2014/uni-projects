library ieee;
use ieee.STD_LOGIC_1164.all;

entity tlab4 is 
	port(
		A: in std_logic_vector(3 downto 0);
		B: in std_logic_vector(3 downto 0);
		OP: in std_logic;
		
		R: out std_logic_vector(3 downto 0);
		CyBw: out std_logic;
		OV: out std_logic
	);
end tlab4;

architecture arq_main of tlab4 is

-- SOMADOR
component somador
	port(
		A, B: in std_logic_vector(3 downto 0);
		Cin: in std_logic;
		S: out std_logic_vector(3 downto 0);
		Cout: out std_logic);
end component;

-- FLAGS
component flags
	port(
		A, B, Cout, OP, S: in std_logic;
		CyBw, OV: out std_logic);
end component;

signal bx: std_logic_vector(3 downto 0);
signal cx: std_logic;
signal rx: std_logic_vector(3 downto 0);

begin

u_somador: somador port map (
	A => A,
	B => bx,
	Cin => OP,
	Cout => cx,
	S => rx
);


u_flags: flags port map (
	A => A(3),
	B => bx(3),
	Cout => cx,
	OP => OP,
	S => rx(3),
	OV => OV,
	CyBw => CyBw
);	


bx(0) <= B(0) xor OP;
bx(1) <= B(1) xor OP;
bx(2) <= B(2) xor OP;
bx(3) <= B(3) xor OP;

R <= rx;

end arq_main;
