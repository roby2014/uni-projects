library ieee;
use ieee.STD_LOGIC_1164.all;


entity m_fulladder is 
	port(
		A: in std_logic;
		B: in std_logic;
		Cin: in std_logic;
		
		S: out std_logic;
		Cout: out std_logic
	);
end m_fulladder;


architecture arq of m_fulladder is
begin
	S <= A xor B xor Cin;
	Cout <= (A and B) or (Cin and (A xor B));
end arq;