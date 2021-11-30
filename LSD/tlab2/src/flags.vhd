library ieee;
use ieee.STD_LOGIC_1164.all;


entity flags is 
	port(
		A: in std_logic; -- = cybw from module_main
		B: in std_logic; -- = ov from module_main
		F: in std_logic_vector(3 downto 0);
		
		CyBw: out std_logic;
		OV: out std_logic;
		Z: out std_logic;
		P: out std_logic;
		GE: out std_logic
	);
end flags;


architecture arq of flags is
begin
	CyBw <= A;
	OV <= B;
	Z <= (not(F(3) or F(2) or F(1) or F(0)));
	P <= (F(0) xor F(1) xor F(2) xor F(3));
	GE <= B xnor F(3);
end arq;
	