library ieee;
use ieee.STD_LOGIC_1164.all;

entity flags is 
	port(
		A: in std_logic;
		B: in std_logic;
		Cout: in std_logic;
		
		OP: in std_logic;
		S: in std_logic;
		
		CyBw: out std_logic;
		OV: out std_logic
	);
end flags;

architecture arq_flags of flags is
begin
	CyBw <= Cout xor OP;
	OV <= (not(A) and not(B) and S) or (A and B and not(S));
end arq_flags;
	