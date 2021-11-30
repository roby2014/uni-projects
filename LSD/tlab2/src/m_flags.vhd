-- flags from lab4

library ieee;
use ieee.STD_LOGIC_1164.all;


entity m_flags is 
	port(
		A: in std_logic;
		B: in std_logic;
		Cout: in std_logic;
		
		OP: in std_logic;
		S: in std_logic;
		
		CyBw: out std_logic;
		OV: out std_logic
	);
end m_flags;


architecture arq of m_flags is
begin
	CyBw <= Cout xor OP;
	OV <= (not(A) and not(B) and S) or (A and B and not(S));
end arq;
	