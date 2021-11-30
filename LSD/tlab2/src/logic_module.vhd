library ieee;
use ieee.STD_LOGIC_1164.all;


entity logic_module is 
	port(
		W: in std_logic_vector(3 downto 0);
		Y: in std_logic_vector(3 downto 0);
		OP: in std_logic_vector(1 downto 0);
		
		S: out std_logic_vector(3 downto 0)
	);
end logic_module;


architecture arq of logic_module is

component logic_and port(
		A, B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
	);
end component;

component logic_or port(	
		A, B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
	);
end component;

component logic_xor port(	
		A, B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
	);
end component;

component logic_not port(	
		A: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
	);
end component;

component logic_mux port(
		Mand, Mor, Mxor, Mnot: in std_logic_vector(3 downto 0);
		OP: in std_logic_vector(1 downto 0);
		S: out std_logic_vector(3 downto 0)
	);
end component;
	
signal sa, so, sx, sn: std_logic_vector(3 downto 0);

begin

uand: logic_and port map (
	A => W, 
	B => Y, 
	O => sa
);
	
uor: logic_or port map (
	A => W, 
	B => Y, 
	O => so
);
	
uxor: logic_xor port map (
	A => W, 
	B => Y, 
	O => sx
);
	
unot: logic_not port map (
	A => W, 
	O => sn
);

umux: logic_mux port map (
	Mand => sa,
	Mor => so,
	Mxor => sx,
	Mnot => sn,
	OP => OP,
	S => S
);

end arq;