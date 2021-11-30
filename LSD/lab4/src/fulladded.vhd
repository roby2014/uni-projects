library ieee;
use ieee.STD_LOGIC_1164.all;

entity full_adder is 
	port(
		A: in std_logic;
		B: in std_logic;
		Cin: in std_logic;
		
		S: out std_logic;
		Cout: out std_logic
	);
end full_adder;

architecture arq_full_adder of full_adder is
begin
	S <= A xor B xor Cin;
	Cout <= (A and B) or (Cin and (A xor B));
end arq_full_adder;
